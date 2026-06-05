package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.SymbologyType
import com.opticon.opticonnect.sdk.api.interfaces.Callback
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Symbology
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.SymbologySettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SymbologyImpl @Inject constructor(
    private val scannerSettingsStateStore: ScannerSettingsStateStore,
    private val settingsHandler: SettingsHandler
) : SettingsBase(), Symbology {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun enableOnlySymbology(deviceId: String, type: SymbologyType): CommandResponse {
        val command = SymbologySettingDescriptors.symbology.enableOnlyCommandFor(type)
        return sendSymbologyCommand(deviceId, command, type)
    }

    override fun enableOnlySymbology(
        deviceId: String,
        type: SymbologyType,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { enableOnlySymbology(deviceId, type) }
    }

    override suspend fun setSymbology(deviceId: String, type: SymbologyType, enabled: Boolean): CommandResponse {
        val command = SymbologySettingDescriptors.symbology.commandFor(type, enabled)
        return sendSymbologyCommand(deviceId, command, type)
    }

    override fun setSymbology(
        deviceId: String,
        type: SymbologyType,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSymbology(deviceId, type, enabled) }
    }

    override fun isSymbologyEnabled(deviceId: String, type: SymbologyType): Boolean {
        return SymbologySettingDescriptors.symbology.isEnabledFrom(
            type,
            scannerSettingsStateStore.settingsFor(deviceId),
            settingsHandler::isDefaultCode
        )
    }

    private suspend fun sendSymbologyCommand(
        deviceId: String,
        command: String?,
        type: SymbologyType
    ): CommandResponse {
        return if (command != null) {
            sendCommand(deviceId, command)
        } else {
            val message = "Command not found for $type"
            Timber.e(message)
            CommandResponse.failed(message)
        }
    }
}
