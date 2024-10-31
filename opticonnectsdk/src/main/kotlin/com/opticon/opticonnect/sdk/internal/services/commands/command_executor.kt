package com.opticon.opticonnect.sdk.internal.services.commands

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
    private val pendingCommandsQueue = LinkedList<Command>()
    private val responseData = StringBuilder()

    private var saveSettingsJob: Job? = null
    private var saveSettingsCode: String = "Z2";

    init {
        coroutineScope.launch { initializeResponseListener() }
    }

    private suspend fun initializeResponseListener() {
        bleCommandResponseReader.getCommandResponseStream(deviceId)
            .onEach { data -> commandResponseReceivedEvent(data) }
            .catch { error -> Timber.e("Error receiving command response: $error") }
            .launchIn(coroutineScope)
    }

    override fun sendCommand(command: Command) {
        Timber.d("Sending command to queue: ${command.code}")
        coroutineScope.launch {
            enqueueCommand(command)
        }
    }

    private fun enqueueCommand(command: Command) {
        pendingCommandsQueue.addLast(command)
        if (pendingCommandsQueue.size == 1) {
            executeCommand(command)
        }

    }

    private fun executeCommand(command: Command) {
        Timber.d("Executing command from queue: ${command.code}")
        responseData.clear()
        startCommandTimeout(command)

        try {
            val bytes = commandBytesProvider.getCommandBytes(command)
            coroutineScope.launch {
                bleDataWriter.writeData(deviceId, command.code, bytes)
            }
        } catch (e: Exception) {
            Timber.e("Error sending command: ${command.code}, Error: $e")
            coroutineScope.launch {
                command.completer.complete(CommandResponse("", false))
                finalizeCommandAndProcessNext(false)
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
        if (command.retried || pendingCommandsQueue.isEmpty()) {
            command.completer.complete(CommandResponse("", false))
            finalizeCommandAndProcessNext(false)
        } else {
            retryCommand(command)
        }
    }

    private suspend fun retryCommand(command: Command) {
        try {
            Timber.w("Retrying command: ${command.code}")
            delay(200L)
            command.retried = true
            pendingCommandsQueue.removeFirstOrNull()
            pendingCommandsQueue.addFirst(command)
            executeCommand(command)
        } catch (e: Exception) {
            Timber.e("Failed to retry command: ${command.code}, Error: $e")
            if (!command.completer.isCompleted) {
                command.completer.complete(CommandResponse.failed("Retry failed due to: $e"))
                finalizeCommandAndProcessNext(false)
            }
        }
    }

    private suspend fun commandResponseReceivedEvent(data: String) {
        if (pendingCommandsQueue.isEmpty()) return

        val command = pendingCommandsQueue.first()
        Timber.d("Command response received for: ${command.code}, Data: $data")

        when (data) {
            NAK.toString() -> {
                if (!command.retried) {
                    retryCommand(command)
                } else {
                    command.completer.complete(CommandResponse.failed(responseData.toString()))
                    finalizeCommandAndProcessNext(false)
                }
            }
            ACK.toString() -> {
                command.completer.complete(CommandResponse(responseData.toString(), true))
                finalizeCommandAndProcessNext(true)
            }
            else -> responseData.append(data)
        }
    }

    private fun finalizeCommandAndProcessNext(succeeded: Boolean = false) {
        timeoutManager.cancelTimeout()

        val command = pendingCommandsQueue.firstOrNull()
        if (command != null) {
            // Trigger settings persistence if needed
            if (command.code != saveSettingsCode) {
                persistSettings()
            }

            val feedbackCommands = commandFeedbackService.generateFeedbackCommands(succeeded, !succeeded, command)

            pendingCommandsQueue.removeFirstOrNull()

            feedbackCommands.forEach { enqueueCommand(it) }

            if (pendingCommandsQueue.isNotEmpty()) {
                executeCommand(pendingCommandsQueue.first())
            }
        }
    }

    private fun persistSettings() {
        // Cancel any previous save settings job
        saveSettingsJob?.cancel()

        // Schedule a new save settings job after 5 seconds
        saveSettingsJob = coroutineScope.launch {
            delay(5000L)
            sendCommand(Command(saveSettingsCode, sendFeedback = false))
        }
    }

    override fun close() {
        pendingCommandsQueue.clear()
        timeoutManager.close()
        saveSettingsJob?.cancel()
    }
}
