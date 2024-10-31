package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ConnectionPool
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Formatting
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Indicator
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ReadOptions
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Symbology
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CodeSpecific
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ScannerSettingsImpl @Inject constructor(
    override val symbology: Symbology,
    override val codeSpecific: CodeSpecific,
    override val readOptions: ReadOptions,
    override val indicator: Indicator,
    override val formatting: Formatting,
    override val connectionPool: ConnectionPool,
    private val commandExecutorsManager: CommandExecutorsManager,
    private val settingsCompressor: SettingsCompressor
) : ScannerSettings {

    override suspend fun executeCommand(deviceId: String, command: ScannerCommand): CommandResponse {
        return try {
            Timber.d("Sending command to device $deviceId: $command")
            commandExecutorsManager.sendCommand(deviceId, command)
        } catch (e: Exception) {
            Timber.e("Error sending command to device $deviceId: $e")
            throw e
        }
    }

    override suspend fun getSettings(deviceId: String): List<CommandData> {
        return try {
            val result = commandExecutorsManager.sendCommand(
                deviceId,
                ScannerCommand(CommunicationCommands.FETCH_SETTINGS, sendFeedback = false)
            )

            if (!result.succeeded) {
                throw Exception("Error fetching settings for device $deviceId: ${result.response}")
            }

            settingsCompressor.getCompressedSettingsList(result.response)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun resetSettings(deviceId: String): Boolean {
        return try {
            Timber.d("Resetting settings for device $deviceId")
            val result = commandExecutorsManager.sendCommand(
                deviceId,
                ScannerCommand(CommunicationCommands.BLUETOOTH_LOW_ENERGY_DEFAULT, sendFeedback = false)
            )
            result.succeeded
        } catch (e: Exception) {
            Timber.e("Error resetting settings for device $deviceId: $e")
            throw e
        }
    }
}