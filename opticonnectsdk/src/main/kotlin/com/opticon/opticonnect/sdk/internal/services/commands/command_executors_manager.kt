package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import timber.log.Timber
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CommandExecutorsManager @Inject constructor(
    private val commandExecutorFactory: CommandExecutorFactory,
    private val commandFactory: CommandFactory,
    private val settingsCompressor: SettingsCompressor // Inject SettingsCompressor
) : Closeable {

    private val commandExecutors = ConcurrentHashMap<String, CommandExecutor>()
    private val compressionManagers = ConcurrentHashMap<String, CompressionManager>()

    fun createCommandExecutor(deviceId: String) {
        // Use the factory to create a new instance of CommandExecutor
        val commandExecutor = commandExecutorFactory.create(deviceId)
        commandExecutors.put(deviceId, commandExecutor)?.close()

        // Create and store the CompressionManager for the device
        val compressionManager = CompressionManager(
            commandExecutor,
            settingsCompressor
        )
        compressionManagers[deviceId] = compressionManager
    }

    fun getCommandExecutor(deviceId: String): CommandExecutor {
        return commandExecutors[deviceId]
            ?: throw IllegalArgumentException("CommandExecutor for device $deviceId does not exist.")
    }

    suspend fun sendCommand(deviceId: String, scannerCommand: ScannerCommand): CommandResponse {
        return try {
            val commandExecutor = getCommandExecutor(deviceId)
            val command = commandFactory.createCommand(scannerCommand)

            commandExecutor.sendCommand(command)

            // Check compression threshold
            compressionManagers[deviceId]?.checkAndHandleCompressionThreshold()
                ?: Timber.e("Compression manager for device $deviceId does not exist.")

            command.completer.await()
        } catch (e: Exception) {
            Timber.e("Error sending command to device $deviceId: ${e.message}")
            //log the available commandExecutors mac addresses
            Timber.e("Available commandExecutors: ${commandExecutors.keys}")
            CommandResponse.failed(e.message ?: "Unknown error")
        }
    }

    fun close(deviceId: String) {
        Timber.d("Closing CommandExecutor for device $deviceId")
        commandExecutors.remove(deviceId)?.close()
        compressionManagers.remove(deviceId)
    }

    override fun close() {
        Timber.d("Closing all CommandExecutors")
        commandExecutors.values.forEach { it.close() }
        commandExecutors.clear()
        compressionManagers.clear()
    }
}
