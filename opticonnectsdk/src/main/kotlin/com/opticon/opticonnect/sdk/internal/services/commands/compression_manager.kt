package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.constants.commands.communication.CommunicationCommands
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class CompressionManager(
    private val commandExecutor: CommandExecutor,
    private val settingsCompressor: SettingsCompressor
) {

    companion object {
        private const val COMPRESSION_THRESHOLD = 30
    }

    private var commandsSentCounter = 0

    suspend fun checkAndHandleCompressionThreshold() {
        commandsSentCounter++

        if (commandsSentCounter >= COMPRESSION_THRESHOLD) {
            commandsSentCounter = 0

            try {
                // Fetch the current uncompressed settings
                val command = Command(CommunicationCommands.FETCH_SETTINGS, sendFeedback = false)
                commandExecutor.sendCommand(command)

                // Await the result of the command
                val settingsResult = withContext(Dispatchers.IO) {
                    command.completer.await()
                }

                // Compress the settings and send the compressed command
                val compressedCommand = withContext(Dispatchers.IO) {
                    settingsCompressor.getCompressedSettingsCommand(settingsResult.response)
                }

                commandExecutor.sendCommand(compressedCommand)
            } catch (error: Exception) {
                Timber.e("Error during command compression: ${error.message}")
            }
        }
    }
}
