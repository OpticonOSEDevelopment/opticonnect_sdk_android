package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code128AndGS1128Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.GS1128ConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code128AndGS1128
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.Code128AndGS1128SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code128AndGS1128Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Code128AndGS1128 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setGS1128Mode(deviceId: String, mode: Code128AndGS1128Mode): CommandResponse {
        val command = Code128AndGS1128SettingDescriptors.gs1128Mode.commandFor(mode)
        Timber.d("Setting GS1-128 mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported GS1-128 mode: $mode")
    }

    override fun setGS1128Mode(deviceId: String, mode: Code128AndGS1128Mode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setGS1128Mode(deviceId, mode) }
    }

    override fun getGS1128Mode(deviceId: String): Code128AndGS1128Mode {
        return Code128AndGS1128SettingDescriptors.gs1128Mode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setGS1128ConversionMode(deviceId: String, mode: GS1128ConversionMode): CommandResponse {
        val command = Code128AndGS1128SettingDescriptors.gs1128ConversionMode.commandFor(mode)
        Timber.d("Setting GS1-128 conversion mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported GS1-128 conversion mode: $mode")
    }

    override fun setGS1128ConversionMode(deviceId: String, mode: GS1128ConversionMode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setGS1128ConversionMode(deviceId, mode) }
    }

    override fun getGS1128ConversionMode(deviceId: String): GS1128ConversionMode {
        return Code128AndGS1128SettingDescriptors.gs1128ConversionMode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code128AndGS1128SettingDescriptors.concatenation.commandFor(enabled)
        Timber.d("Setting Code 128 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setConcatenation(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConcatenation(deviceId, enabled) }
    }

    override fun isConcatenationEnabled(deviceId: String): Boolean {
        return Code128AndGS1128SettingDescriptors.concatenation.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setLeadingC1Output(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code128AndGS1128SettingDescriptors.leadingC1Output.commandFor(enabled)
        Timber.d("Setting leading C1 output for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setLeadingC1Output(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLeadingC1Output(deviceId, enabled) }
    }

    override fun isLeadingC1OutputEnabled(deviceId: String): Boolean {
        return Code128AndGS1128SettingDescriptors.leadingC1Output.valueFrom(settingsFor(deviceId))
    }
}
