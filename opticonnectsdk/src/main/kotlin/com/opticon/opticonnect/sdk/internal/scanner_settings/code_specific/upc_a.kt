package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCALeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCA
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.UPCASettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCAImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), UPCA {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setLeadingZeroAndTransmitCDMode(deviceId: String, mode: UPCALeadingZeroAndTransmitCDMode): CommandResponse {
        val command = UPCASettingDescriptors.leadingZeroAndTransmitCDMode.commandFor(mode)
        Timber.d("Setting UPC_A leading zero and transmit CD mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported UPC-A leading zero/transmit CD mode: $mode")
    }

    override fun setLeadingZeroAndTransmitCDMode(deviceId: String, mode: UPCALeadingZeroAndTransmitCDMode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLeadingZeroAndTransmitCDMode(deviceId, mode) }
    }

    override fun getLeadingZeroAndTransmitCDMode(deviceId: String): UPCALeadingZeroAndTransmitCDMode {
        return UPCASettingDescriptors.leadingZeroAndTransmitCDMode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UPCASettingDescriptors.addOnPlus2.commandFor(enabled)
        Timber.d("Setting UPC_A plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override fun isAddOnPlus2Enabled(deviceId: String): Boolean {
        return UPCASettingDescriptors.addOnPlus2.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UPCASettingDescriptors.addOnPlus5.commandFor(enabled)
        Timber.d("Setting UPC_A plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }

    override fun isAddOnPlus5Enabled(deviceId: String): Boolean {
        return UPCASettingDescriptors.addOnPlus5.valueFrom(settingsFor(deviceId))
    }
}
