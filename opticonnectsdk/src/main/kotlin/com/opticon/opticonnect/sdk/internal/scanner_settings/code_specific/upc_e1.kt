package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE1
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.UPCE1SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCE1Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), UPCE1 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UPCE1SettingDescriptors.addOnPlus2.commandFor(enabled)
        Timber.d("Setting UPCE1 plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override fun isAddOnPlus2Enabled(deviceId: String): Boolean {
        return UPCE1SettingDescriptors.addOnPlus2.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UPCE1SettingDescriptors.addOnPlus5.commandFor(enabled)
        Timber.d("Setting UPCE1 plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }

    override fun isAddOnPlus5Enabled(deviceId: String): Boolean {
        return UPCE1SettingDescriptors.addOnPlus5.valueFrom(settingsFor(deviceId))
    }
}
