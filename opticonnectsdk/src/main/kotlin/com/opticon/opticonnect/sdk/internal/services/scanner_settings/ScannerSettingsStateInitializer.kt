package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ScannerSettingsStateInitializer @Inject constructor(
    private val commandExecutorsManager: CommandExecutorsManager,
    private val settingsCompressor: SettingsCompressor,
    private val scannerSettingsStateStore: ScannerSettingsStateStore
) {
    suspend fun initialize(deviceId: String) {
        val result = commandExecutorsManager.sendCommand(
            deviceId,
            ScannerCommand(CommunicationCommands.FETCH_SETTINGS, sendFeedback = false)
        )

        if (result.succeeded) {
            val settings = settingsCompressor.getCompressedSettingsList(result.response)
            scannerSettingsStateStore.replaceSettings(deviceId, settings)
        } else {
            scannerSettingsStateStore.clear(deviceId)
            Timber.w("Failed to initialize scanner settings state for device $deviceId: ${result.response}")
        }
    }
}
