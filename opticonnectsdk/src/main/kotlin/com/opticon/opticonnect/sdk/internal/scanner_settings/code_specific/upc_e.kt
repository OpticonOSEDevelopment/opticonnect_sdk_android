package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCELeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCEConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.UPCESettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCEImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), UPCE {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode
    ): CommandResponse {
        val command = UPCESettingDescriptors.leadingZeroAndTransmitCDMode.commandFor(mode)
        Timber.d("Setting UPCE leading zero and transmit CD mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported UPC-E leading zero/transmit CD mode: $mode")
    }

    override fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLeadingZeroAndTransmitCDMode(deviceId, mode) }
    }

    override fun getLeadingZeroAndTransmitCDMode(deviceId: String): UPCELeadingZeroAndTransmitCDMode {
        return UPCESettingDescriptors.leadingZeroAndTransmitCDMode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode
    ): CommandResponse {
        val command = UPCESettingDescriptors.conversionMode.commandFor(mode)
        Timber.d("Setting UPCE conversion mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported UPC-E conversion mode: $mode")
    }

    override fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConversionMode(deviceId, mode) }
    }

    override fun getConversionMode(deviceId: String): UPCEConversionMode {
        return UPCESettingDescriptors.conversionMode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse {
        val command = UPCESettingDescriptors.addOnPlus2.commandFor(enabled)
        Timber.d("Setting UPCE plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override fun isAddOnPlus2Enabled(deviceId: String): Boolean {
        return UPCESettingDescriptors.addOnPlus2.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse {
        val command = UPCESettingDescriptors.addOnPlus5.commandFor(enabled)
        Timber.d("Setting UPCE plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }

    override fun isAddOnPlus5Enabled(deviceId: String): Boolean {
        return UPCESettingDescriptors.addOnPlus5.valueFrom(settingsFor(deviceId))
    }
}
