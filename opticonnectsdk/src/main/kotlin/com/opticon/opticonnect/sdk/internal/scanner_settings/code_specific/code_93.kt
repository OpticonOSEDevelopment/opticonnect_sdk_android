package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code93
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.Code93SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code93Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Code93 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code93SettingDescriptors.checkCD.commandFor(enabled)
        Timber.d("Setting Code 93 check digit validation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCheckCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, enabled) }
    }

    override fun isCheckCDEnabled(deviceId: String): Boolean {
        return Code93SettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code93SettingDescriptors.concatenation.commandFor(enabled)
        Timber.d("Setting Code 93 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setConcatenation(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConcatenation(deviceId, enabled) }
    }

    override fun isConcatenationEnabled(deviceId: String): Boolean {
        return Code93SettingDescriptors.concatenation.valueFrom(settingsFor(deviceId))
    }
}
