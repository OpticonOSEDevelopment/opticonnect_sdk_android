package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.CommandResponsePacket
import com.opticon.opticonnect.sdk.internal.utils.TimeoutManager
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ACK
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.NAK
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import java.io.Closeable
import java.util.LinkedList

internal class CommandExecutor @Inject constructor(
    private val deviceId: String,
    private val bleDataWriter: BleDataWriter,
    private val bleCommandResponseReader: BleCommandResponseReader,
    private val commandBytesProvider: CommandBytesProvider,
    private val commandFeedbackService: CommandFeedbackService,
    private val timeoutManager: TimeoutManager
) : CommandSender, Closeable {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val commandMutex = Mutex()
    private val commandSubmissionChannel = Channel<Command>(Channel.UNLIMITED)
    private val pendingCommandsQueue = LinkedList<Command>()
    private val responseData = StringBuilder()

    private var saveSettingsJob: Job? = null
    private val saveSettingsCode: String = "Z2"
    private var closed = false

    init {
        coroutineScope.launch { processCommandSubmissions() }
        coroutineScope.launch { initializeResponseListener() }
    }

    private suspend fun processCommandSubmissions() {
        for (command in commandSubmissionChannel) {
            commandMutex.withLock {
                if (closed) {
                    command.completer.complete(CommandResponse.failed("Command executor closed."))
                } else {
                    enqueueCommandLocked(command)
                }
            }
        }
    }

    private suspend fun initializeResponseListener() {
        bleCommandResponseReader.getCommandResponseStream(deviceId)
            .onEach { data -> commandResponseReceivedEvent(data) }
            .catch { error -> Timber.e("Error receiving command response: $error") }
            .launchIn(coroutineScope)
    }

    override fun sendCommand(command: Command) {
        Timber.d("Sending command to queue: ${command.code}")
        val result = commandSubmissionChannel.trySend(command)
        if (result.isFailure) {
            command.completer.complete(CommandResponse.failed("Command executor closed."))
        }
    }

    private fun enqueueCommandLocked(command: Command) {
        val shouldExecute = pendingCommandsQueue.isEmpty()
        pendingCommandsQueue.addLast(command)
        if (shouldExecute) {
            executeCommandLocked(command)
        }
    }

    private fun executeCommandLocked(command: Command) {
        Timber.d("Executing command from queue: ${command.code}")
        responseData.clear()
        startCommandTimeout(command)

        try {
            val packet = commandBytesProvider.getCommandPacket(command)
            command.sentSequenceNumber = packet.sequenceNumber
            coroutineScope.launch {
                bleDataWriter.writeData(deviceId, command.code, packet.bytes)
            }
        } catch (e: Exception) {
            Timber.e("Error sending command: ${command.code}, Error: $e")
            command.completer.complete(CommandResponse("", false))
            finalizeCommandAndProcessNextLocked(false)
        }
    }

    private fun startCommandTimeout(command: Command) {
        timeoutManager.startTimeout(2000L) {
            coroutineScope.launch {
                onCommandTimeout(command)
            }
        }
    }

    private suspend fun onCommandTimeout(command: Command) {
        commandMutex.withLock {
            if (pendingCommandsQueue.firstOrNull() != command) return@withLock

            Timber.w("Command timeout occurred for: ${command.code}")
            if (command.retried || pendingCommandsQueue.isEmpty()) {
                if (!command.completer.isCompleted) {
                    command.completer.complete(CommandResponse("", false))
                }
                finalizeCommandAndProcessNextLocked(false)
            } else {
                retryCommandLocked(command)
            }
        }
    }

    private suspend fun retryCommandLocked(command: Command) {
        try {
            Timber.w("Retrying command: ${command.code}")
            delay(200L)
            if (pendingCommandsQueue.firstOrNull() != command) return
            command.retried = true
            pendingCommandsQueue.removeFirstOrNull()
            pendingCommandsQueue.addFirst(command)
            executeCommandLocked(command)
        } catch (e: Exception) {
            Timber.e("Failed to retry command: ${command.code}, Error: $e")
            if (!command.completer.isCompleted) {
                command.completer.complete(CommandResponse.failed("Retry failed due to: $e"))
                finalizeCommandAndProcessNextLocked(false)
            }
        }
    }

    private suspend fun commandResponseReceivedEvent(responsePacket: CommandResponsePacket) {
        commandMutex.withLock {
            commandResponseReceivedEventLocked(responsePacket)
        }
    }

    private suspend fun commandResponseReceivedEventLocked(responsePacket: CommandResponsePacket) {
        if (pendingCommandsQueue.isEmpty()) return

        val command = pendingCommandsQueue.first()
        val sequenceNumber = responsePacket.sequenceNumber
        if (sequenceNumber != null && command.sentSequenceNumber != sequenceNumber) {
            Timber.w(
                "Ignoring command response for sequence $sequenceNumber. " +
                    "Expected ${command.sentSequenceNumber} for command ${command.code}."
            )
            return
        }

        val data = responsePacket.data
        Timber.d("Command response received for: ${command.code}, Sequence: $sequenceNumber, Data: $data")

        when (data) {
            NAK.toString() -> {
                if (!command.retried) {
                    retryCommandLocked(command)
                } else {
                    if (!command.completer.isCompleted) {
                        command.completer.complete(CommandResponse.failed(responseData.toString()))
                    }
                    finalizeCommandAndProcessNextLocked(false)
                }
            }
            ACK.toString() -> {
                if (!command.completer.isCompleted) {
                    command.completer.complete(CommandResponse(responseData.toString(), true))
                }
                finalizeCommandAndProcessNextLocked(true)
            }
            else -> responseData.append(data)
        }
    }

    private fun finalizeCommandAndProcessNextLocked(succeeded: Boolean = false) {
        timeoutManager.cancelTimeout()

        val command = pendingCommandsQueue.firstOrNull()
        if (command != null) {
            // Trigger settings persistence if needed
            if (command.code != saveSettingsCode) {
                persistSettingsLocked()
            }

            val feedbackCommands = commandFeedbackService.generateFeedbackCommands(succeeded, !succeeded, command)

            pendingCommandsQueue.removeFirstOrNull()
            feedbackCommands.forEach { pendingCommandsQueue.addLast(it) }

            if (pendingCommandsQueue.isNotEmpty()) {
                executeCommandLocked(pendingCommandsQueue.first())
            }
        }
    }

    private fun persistSettingsLocked() {
        // Cancel any previous save settings job
        saveSettingsJob?.cancel()

        // Schedule a new save settings job after 5 seconds
        saveSettingsJob = coroutineScope.launch {
            delay(5000L)
            sendCommand(Command(saveSettingsCode, sendFeedback = false))
        }
    }

    override fun close() {
        runBlocking {
            commandMutex.withLock {
                closed = true
                pendingCommandsQueue.forEach { command ->
                    if (!command.completer.isCompleted) {
                        command.completer.complete(CommandResponse.failed("Command executor closed."))
                    }
                }
                pendingCommandsQueue.clear()
                commandSubmissionChannel.close()
                timeoutManager.close()
                saveSettingsJob?.cancel()
            }
        }
        coroutineScope.cancel()
    }
}
