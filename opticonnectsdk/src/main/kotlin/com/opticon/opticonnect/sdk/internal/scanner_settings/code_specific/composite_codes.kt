package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CompositeCodesOutputMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CompositeCodes
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.CompositeCodesSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CompositeCodesImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), CompositeCodes {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setOutputMode(deviceId: String, outputMode: CompositeCodesOutputMode): CommandResponse {
        val command = CompositeCodesSettingDescriptors.outputMode.commandFor(outputMode)
        Timber.d("Setting composite codes output mode for deviceId $deviceId to $outputMode")
        return sendMappedCommand(deviceId, command, "Unsupported composite codes output mode: $outputMode")
    }

    override fun setOutputMode(deviceId: String, outputMode: CompositeCodesOutputMode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setOutputMode(deviceId, outputMode) }
    }

    override fun getOutputMode(deviceId: String): CompositeCodesOutputMode {
        return CompositeCodesSettingDescriptors.outputMode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setIgnoreLinkFlag(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CompositeCodesSettingDescriptors.ignoreLinkFlag.commandFor(enabled)
        Timber.d("Setting composite codes link flag for deviceId $deviceId to ${if (enabled) "ignored" else "not ignored"}")
        return sendCommand(deviceId, command)
    }

    override fun setIgnoreLinkFlag(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setIgnoreLinkFlag(deviceId, enabled) }
    }

    override fun isIgnoreLinkFlagEnabled(deviceId: String): Boolean {
        return CompositeCodesSettingDescriptors.ignoreLinkFlag.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setCompositeGS1DatabarGS1128(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CompositeCodesSettingDescriptors.compositeGS1DatabarGS1128.commandFor(enabled)
        Timber.d("Setting composite GS1 for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCompositeGS1DatabarGS1128(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCompositeGS1DatabarGS1128(deviceId, enabled) }
    }

    override fun isCompositeGS1DatabarGS1128Enabled(deviceId: String): Boolean {
        return CompositeCodesSettingDescriptors.compositeGS1DatabarGS1128.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setCompositeEANUPC(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CompositeCodesSettingDescriptors.compositeEANUPC.commandFor(enabled)
        Timber.d("Setting composite EAN/UPC for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCompositeEANUPC(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCompositeEANUPC(deviceId, enabled) }
    }

    override fun isCompositeEANUPCEnabled(deviceId: String): Boolean {
        return CompositeCodesSettingDescriptors.compositeEANUPC.valueFrom(settingsFor(deviceId))
    }
}
