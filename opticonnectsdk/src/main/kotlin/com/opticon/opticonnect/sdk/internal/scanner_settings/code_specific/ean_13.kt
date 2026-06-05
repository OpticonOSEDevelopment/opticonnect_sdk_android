package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN13
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.EAN13SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EAN13Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), EAN13 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = EAN13SettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting EAN-13 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return EAN13SettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = EAN13SettingDescriptors.addOnPlus2.commandFor(enabled)
        Timber.d("Setting EAN-13 plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override fun isAddOnPlus2Enabled(deviceId: String): Boolean {
        return EAN13SettingDescriptors.addOnPlus2.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = EAN13SettingDescriptors.addOnPlus5.commandFor(enabled)
        Timber.d("Setting EAN-13 plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }

    override fun isAddOnPlus5Enabled(deviceId: String): Boolean {
        return EAN13SettingDescriptors.addOnPlus5.valueFrom(settingsFor(deviceId))
    }
}
