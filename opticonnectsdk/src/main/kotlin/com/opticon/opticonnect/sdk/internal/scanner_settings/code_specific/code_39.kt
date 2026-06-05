package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39MinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code39
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.Code39SettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code39Impl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Code39 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setMode(deviceId: String, mode: Code39Mode): CommandResponse {
        val command = Code39SettingDescriptors.mode.commandFor(mode)
        Timber.d("Setting Code 39 mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported Code 39 mode: $mode")
    }

    override fun setMode(deviceId: String, mode: Code39Mode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMode(deviceId, mode) }
    }

    override fun getMode(deviceId: String): Code39Mode {
        return Code39SettingDescriptors.mode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code39SettingDescriptors.checkCD.commandFor(enabled)
        Timber.d("Setting Code 39 check digit validation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCheckCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, enabled) }
    }

    override fun isCheckCDEnabled(deviceId: String): Boolean {
        return Code39SettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code39SettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting Code 39 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return Code39SettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitSTSP(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code39SettingDescriptors.transmitSTSP.commandFor(enabled)
        Timber.d("Setting Code 39 transmit start/stop characters for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitSTSP(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitSTSP(deviceId, enabled) }
    }

    override fun isTransmitSTSPEnabled(deviceId: String): Boolean {
        return Code39SettingDescriptors.transmitSTSP.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code39SettingDescriptors.concatenation.commandFor(enabled)
        Timber.d("Setting Code 39 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setConcatenation(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConcatenation(deviceId, enabled) }
    }

    override fun isConcatenationEnabled(deviceId: String): Boolean {
        return Code39SettingDescriptors.concatenation.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitLdAForItPharm(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code39SettingDescriptors.transmitLdAForItPharm.commandFor(enabled)
        Timber.d("Setting Code 39 transmit leading 'A' for IT Pharmaceutical for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitLdAForItPharm(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitLdAForItPharm(deviceId, enabled) }
    }

    override fun isTransmitLdAForItPharmEnabled(deviceId: String): Boolean {
        return Code39SettingDescriptors.transmitLdAForItPharm.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setMinLength(deviceId: String, length: Code39MinimumLength): CommandResponse {
        val command = Code39SettingDescriptors.minimumLength.commandFor(length)
        Timber.d("Setting Code 39 minimum length for deviceId $deviceId to $length")
        return sendMappedCommand(deviceId, command, "Unsupported Code 39 minimum length: $length")
    }

    override fun setMinLength(deviceId: String, length: Code39MinimumLength, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMinLength(deviceId, length) }
    }

    override fun getMinLength(deviceId: String): Code39MinimumLength {
        return Code39SettingDescriptors.minimumLength.valueFrom(settingsFor(deviceId))
    }
}
