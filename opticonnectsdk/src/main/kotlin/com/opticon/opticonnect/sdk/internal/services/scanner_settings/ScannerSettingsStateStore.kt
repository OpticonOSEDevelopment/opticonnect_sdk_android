package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ScannerSettingsStateStore @Inject constructor(
    private val settingsHandler: SettingsHandler
) {
    private val lock = Any()
    private val settingsByDevice = mutableMapOf<String, MutableMap<String, List<String>>>()

    fun replaceSettings(deviceId: String, settings: List<CommandData>) {
        val rebuiltSettings = mutableMapOf<String, List<String>>()
        settings.forEach { commandData ->
            settingsHandler.applyCommandToSettings(rebuiltSettings, commandData)
        }

        synchronized(lock) {
            settingsByDevice[deviceId] = rebuiltSettings
        }
    }

    fun applyCommand(deviceId: String, commandData: CommandData) {
        synchronized(lock) {
            val deviceSettings = settingsByDevice.getOrPut(deviceId) { mutableMapOf() }
            settingsHandler.applyCommandToSettings(deviceSettings, commandData)
        }
    }

    fun settingsFor(deviceId: String): Map<String, List<String>> {
        return synchronized(lock) {
            val deviceSettings = settingsByDevice[deviceId]
            require(deviceSettings != null) {
                "Settings state for device $deviceId is not initialized. Connect the scanner or call getSettings first."
            }

            deviceSettings.mapValues { it.value.toList() }
        }
    }

    fun clear(deviceId: String) {
        synchronized(lock) {
            settingsByDevice.remove(deviceId)
        }
    }

    fun clearAll() {
        synchronized(lock) {
            settingsByDevice.clear()
        }
    }
}
