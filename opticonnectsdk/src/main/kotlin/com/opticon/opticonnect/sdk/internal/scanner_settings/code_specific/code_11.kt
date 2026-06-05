package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code11CheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code11
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.Code11SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code11Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Code11 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, setting: Code11CheckCDSettings): CommandResponse {
        val command = Code11SettingDescriptors.checkCD.commandFor(setting)
        Timber.d("Setting Code 11 check digit validation for deviceId $deviceId to $setting")
        return sendMappedCommand(deviceId, command, "Unsupported Code 11 check digit setting: $setting")
    }

    override fun setCheckCD(
        deviceId: String,
        setting: Code11CheckCDSettings,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, setting) }
    }

    override fun getCheckCD(deviceId: String): Code11CheckCDSettings {
        return Code11SettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code11SettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting Code 11 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return Code11SettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }
}
