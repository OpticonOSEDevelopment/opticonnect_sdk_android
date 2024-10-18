package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.constants.commands.communication.SAVE_SETTINGS
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.helpers.TimeoutManager
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.LinkedList
import java.util.Timer
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn

class CommandExecutor @Inject constructor(
    private val deviceId: String,
    private val bleDataWriter: BleDataWriter,
    private val bleCommandResponseReader: BleCommandResponseReader,
    private val commandBytesProvider: CommandBytesProvider,
    private val commandFeedbackService: CommandFeedbackService,
    private val timeoutManager: TimeoutManager
) : CommandSender {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val pendingCommandsQueue = LinkedList<Command>()
    private val responseData = StringBuilder()
    private val mutex = Mutex()

    init {
        coroutineScope.launch {
            initializeResponseListener()
        }
    }

    private suspend fun initializeResponseListener() {
        bleCommandResponseReader.getCommandResponseStream(deviceId)
            .onEach { data -> commandResponseReceivedEvent(data) }
            .catch { error -> Timber.e("Error receiving command response: $error") }
            .launchIn(coroutineScope)  // Use the custom scope here
    }

    override fun sendCommand(command: Command) {
        coroutineScope.launch {
            mutex.withLock {
                enqueueCommand(command)
            }
        }
    }

    private fun enqueueCommand(command: Command) {
        pendingCommandsQueue.addLast(command)
        if (pendingCommandsQueue.size == 1) {
            executeCommand(pendingCommandsQueue.first())
        }
    }

    private fun executeCommand(command: Command) {
        responseData.clear()
        startCommandTimeout(command)

        try {
            val bytes = commandBytesProvider.getCommandBytes(command).toByteArray()
            coroutineScope.launch {
                bleDataWriter.writeData(deviceId, command.code, bytes)
            }
        } catch (e: Exception) {
            Timber.e("Error sending command: ${command.code}, Error: $e")
            coroutineScope.launch {
                finishWithFailedRequest(command)
            }
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
        Timber.w("Command timeout occurred for: ${command.code}")
        if (command.retried) {
            finishWithFailedRequest(command)
        } else {
            retryCommand(command)
        }
    }

    private fun sendScannerFeedback(sendAckFeedback: Boolean, sendNakFeedback: Boolean, command: Command) {
        val feedbackCommands = commandFeedbackService.generateFeedbackCommands(sendAckFeedback, sendNakFeedback, command)
        for (feedbackCommand in feedbackCommands) {
            sendCommand(feedbackCommand)
        }
    }

    private fun completeCommand(command: Command, responseData: String, hasFailed: Boolean) {
        timeoutManager.cancelTimeout()

        if (!command.completer.isCompleted) {
            command.completer.complete(CommandResponse(responseData, !hasFailed))
        }
    }

    private suspend fun finalizeCommandAndProcessNext(responseData: String, hasFailed: Boolean) {
        if (pendingCommandsQueue.isNotEmpty()) {
            mutex.withLock {
                if (pendingCommandsQueue.isNotEmpty()) {
                    val command = pendingCommandsQueue.first()

                    completeCommand(command, responseData, hasFailed)

                    pendingCommandsQueue.removeFirst()

                    if (command.code != SAVE_SETTINGS) {
                        persistSettings()
                    }
                }
                if (pendingCommandsQueue.isNotEmpty()) {
                    executeCommand(pendingCommandsQueue.first())
                }
            }
        }
    }

    private suspend fun finishWithFailedRequest(command: Command) {
        finishCommandRequest("", false, false, command, hasFailed = true)
    }

    private suspend fun finishCommandRequest(responseData: String, sendAckFeedback: Boolean, sendNakFeedback: Boolean, command: Command, hasFailed: Boolean = false) {
        sendScannerFeedback(sendAckFeedback, sendNakFeedback, command)
        finalizeCommandAndProcessNext(responseData, hasFailed)
    }

    private suspend fun retryCommand(command: Command) {
        Timber.w("Retrying command: ${command.code}")
        command.retried = true
        mutex.withLock {
            pendingCommandsQueue.removeFirst()
            pendingCommandsQueue.add(command)
            executeCommand(pendingCommandsQueue.first())
        }
    }

    private suspend fun commandResponseReceivedEvent(data: String) {
        if (pendingCommandsQueue.isEmpty()) return

        val command = pendingCommandsQueue.first()
        Timber.w("Command response received for: ${command.code}, Data: $data")

        when (data) {
            NAK.toString() -> {
                if (!command.retried) {
                    retryCommand(command)
                } else {
                    val responseData = this.responseData.toString()
                    finishCommandRequest(responseData, false, true, command)
                }
            }
            ACK.toString() -> {
                val responseData = this.responseData.toString()
                finishCommandRequest(responseData, true, false, command)
            }
            else -> responseData.append(data)
        }
    }

    private var saveToNonVolatileMemoryJob: Job? = null

    private fun persistSettings() {
        saveToNonVolatileMemoryJob?.cancel()

        saveToNonVolatileMemoryJob = coroutineScope.launch {
            delay(5000L)  // Wait for 5 seconds
            sendCommand(Command(SAVE_SETTINGS, sendFeedback = false))
        }
    }

    fun dispose() {
        pendingCommandsQueue.clear()
        timeoutManager.dispose()
    }
}