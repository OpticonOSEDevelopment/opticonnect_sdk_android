package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.CommandPacket
import com.opticon.opticonnect.sdk.internal.entities.CommandResponsePacket
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ACK
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.NAK
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import com.opticon.opticonnect.sdk.internal.utils.TimeoutManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.Closeable

class CommandExecutorTest {

    @Test
    fun completesQueuedCommandWhenAckIsReceived() {
        runBlocking {
            testExecutor().use { fixture ->
                val command = Command("Z1", sendFeedback = false)

                fixture.executor.sendCommand(command)
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.emitResponse("FBMV0136\r", command)
                fixture.emitResponse(ACK.toString(), command)

                val response = withTimeout(1000) { command.completer.await() }
                assertTrue(response.succeeded)
                assertEquals("FBMV0136\r", response.response)
            }
        }
    }

    @Test
    fun sendsQueuedCommandsOneAtATime() {
        runBlocking {
            testExecutor().use { fixture ->
                val firstCommand = Command("Z1", sendFeedback = false)
                val secondCommand = Command("Z2", sendFeedback = false)

                fixture.executor.sendCommand(firstCommand)
                fixture.executor.sendCommand(secondCommand)

                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)
                assertEquals(null, fixture.writer.nextWriteOrNull())

                fixture.emitResponse(ACK.toString(), firstCommand)
                assertTrue(withTimeout(1000) { firstCommand.completer.await() }.succeeded)

                assertEquals("Z2", fixture.writer.awaitWrite().commandCode)
                fixture.emitResponse(ACK.toString(), secondCommand)
                assertTrue(withTimeout(1000) { secondCommand.completer.await() }.succeeded)
            }
        }
    }

    @Test
    fun retriesOnceAfterNakThenCompletesOnAck() {
        runBlocking {
            testExecutor().use { fixture ->
                val command = Command("Z1", sendFeedback = false)

                fixture.executor.sendCommand(command)
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.emitResponse(NAK.toString(), command)
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.emitResponse("OK", command)
                fixture.emitResponse(ACK.toString(), command)

                val response = withTimeout(1000) { command.completer.await() }
                assertTrue(response.succeeded)
                assertEquals("OK", response.response)
            }
        }
    }

    @Test
    fun failsAfterSecondNakAndProcessesNextQueuedCommand() {
        runBlocking {
            testExecutor().use { fixture ->
                val firstCommand = Command("Z1", sendFeedback = false)
                val secondCommand = Command("Z2", sendFeedback = false)

                fixture.executor.sendCommand(firstCommand)
                fixture.executor.sendCommand(secondCommand)

                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)
                fixture.emitResponse(NAK.toString(), firstCommand)
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)
                fixture.emitResponse(NAK.toString(), firstCommand)

                val firstResponse = withTimeout(1000) { firstCommand.completer.await() }
                assertFalse(firstResponse.succeeded)

                assertEquals("Z2", fixture.writer.awaitWrite().commandCode)
                fixture.emitResponse(ACK.toString(), secondCommand)
                assertTrue(withTimeout(1000) { secondCommand.completer.await() }.succeeded)
            }
        }
    }

    @Test
    fun ignoresResponseWithWrongSequenceNumber() {
        runBlocking {
            testExecutor().use { fixture ->
                val firstCommand = Command("Z1", sendFeedback = false)
                val secondCommand = Command("Z2", sendFeedback = false)

                fixture.executor.sendCommand(firstCommand)
                fixture.executor.sendCommand(secondCommand)

                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.responses.emit(CommandResponsePacket(ACK.toString(), 999))
                assertEquals(null, withTimeoutOrNull(200) { firstCommand.completer.await() })
                assertEquals(null, fixture.writer.nextWriteOrNull())

                fixture.emitResponse(ACK.toString(), firstCommand)
                assertTrue(withTimeout(1000) { firstCommand.completer.await() }.succeeded)
                assertEquals("Z2", fixture.writer.awaitWrite().commandCode)
            }
        }
    }

    @Test
    fun retriesOnceAfterTimeoutThenFailsOnSecondTimeout() {
        runBlocking {
            testExecutor().use { fixture ->
                val command = Command("Z1", sendFeedback = false)

                fixture.executor.sendCommand(command)
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.timeoutManager.fireTimeout()
                assertEquals("Z1", fixture.writer.awaitWrite().commandCode)

                fixture.timeoutManager.fireTimeout()

                val response = withTimeout(1000) { command.completer.await() }
                assertFalse(response.succeeded)
            }
        }
    }

    private fun testExecutor(): CommandExecutorFixture {
        val writer = FakeBleDataWriter()
        val responseReader = FakeBleCommandResponseReader()
        val timeoutManager = FakeTimeoutManager()
        val executor = CommandExecutor(
            deviceId = TEST_DEVICE_ID,
            bleDataWriter = writer,
            bleCommandResponseReader = responseReader,
            commandBytesProvider = EchoCommandBytesProvider(),
            commandFeedbackService = CommandFeedbackService(DisabledScannerFeedback()),
            timeoutManager = timeoutManager
        )

        return CommandExecutorFixture(
            executor = executor,
            writer = writer,
            responses = responseReader.responses,
            timeoutManager = timeoutManager
        )
    }

    private data class CommandExecutorFixture(
        val executor: CommandExecutor,
        val writer: FakeBleDataWriter,
        val responses: MutableSharedFlow<CommandResponsePacket>,
        val timeoutManager: FakeTimeoutManager
    ) : Closeable {
        override fun close() {
            executor.close()
        }

        suspend fun emitResponse(data: String, command: Command) {
            responses.emit(CommandResponsePacket(data, command.sentSequenceNumber))
        }
    }

    private data class WriteCall(
        val deviceId: String,
        val commandCode: String,
        val dataBytes: ByteArray
    )

    private class FakeBleDataWriter : BleDataWriter {
        private val writes = Channel<WriteCall>(Channel.UNLIMITED)

        override suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray) {
            writes.send(WriteCall(deviceId, data, dataBytes))
        }

        suspend fun awaitWrite(): WriteCall =
            withTimeout(1000) { writes.receive() }

        suspend fun nextWriteOrNull(): WriteCall? =
            withTimeoutOrNull(200) { writes.receive() }
    }

    private class FakeBleCommandResponseReader : BleCommandResponseReader {
        val responses = MutableSharedFlow<CommandResponsePacket>(replay = 20)

        override suspend fun getCommandResponseStream(deviceId: String): Flow<CommandResponsePacket> =
            responses
    }

    private class EchoCommandBytesProvider : CommandBytesProvider {
        private var sequenceNumber = 0

        override fun getCommandPacket(command: Command): CommandPacket {
            val currentSequenceNumber = sequenceNumber
            sequenceNumber = (sequenceNumber + 1) % (1 shl 16)
            return CommandPacket(command.data.toByteArray(Charsets.UTF_8), currentSequenceNumber)
        }
    }

    private class DisabledScannerFeedback : ScannerFeedback {
        override val led = false
        override val buzzer = false
        override val vibration = false

        override fun set(led: Boolean?, buzzer: Boolean?, vibration: Boolean?) = Unit
    }

    private class FakeTimeoutManager : TimeoutManager() {
        private var timeoutCallback: (() -> Unit)? = null

        override fun startTimeout(timeoutDuration: Long, onTimeout: () -> Unit) {
            timeoutCallback = onTimeout
        }

        override fun cancelTimeout() {
            timeoutCallback = null
        }

        override fun close() {
            timeoutCallback = null
        }

        fun fireTimeout() {
            timeoutCallback?.invoke()
        }
    }

    private companion object {
        const val TEST_DEVICE_ID = "38:89:DC:0E:00:0F"
    }
}
