package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarStartStopTransmission
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Codabar
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.CodabarSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CodabarImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Codabar {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setMode(deviceId: String, mode: CodabarMode): CommandResponse {
        val command = CodabarSettingDescriptors.mode.commandFor(mode)
        Timber.d("Setting Codabar mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported Codabar mode: $mode")
    }

    override fun setMode(deviceId: String, mode: CodabarMode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMode(deviceId, mode) }
    }

    override fun getMode(deviceId: String): CodabarMode {
        return CodabarSettingDescriptors.mode.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CodabarSettingDescriptors.checkCD.commandFor(enabled)
        Timber.d("Setting Codabar check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCheckCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, enabled) }
    }

    override fun isCheckCDEnabled(deviceId: String): Boolean {
        return CodabarSettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CodabarSettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting Codabar transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return CodabarSettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CodabarSettingDescriptors.spaceInsertion.commandFor(enabled)
        Timber.d("Setting Codabar space insertion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setSpaceInsertion(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSpaceInsertion(deviceId, enabled) }
    }

    override fun isSpaceInsertionEnabled(deviceId: String): Boolean {
        return CodabarSettingDescriptors.spaceInsertion.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setMinimumLength(deviceId: String, length: CodabarMinimumLength): CommandResponse {
        val command = CodabarSettingDescriptors.minimumLength.commandFor(length)
        Timber.d("Setting Codabar minimum length for deviceId $deviceId to $length")
        return sendMappedCommand(deviceId, command, "Unsupported Codabar minimum length: $length")
    }

    override fun setMinimumLength(deviceId: String, length: CodabarMinimumLength, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMinimumLength(deviceId, length) }
    }

    override fun getMinimumLength(deviceId: String): CodabarMinimumLength {
        return CodabarSettingDescriptors.minimumLength.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setIntercharacterGapCheck(deviceId: String, enabled: Boolean): CommandResponse {
        val command = CodabarSettingDescriptors.intercharacterGapCheck.commandFor(enabled)
        Timber.d("Setting Codabar intercharacter gap check for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setIntercharacterGapCheck(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setIntercharacterGapCheck(deviceId, enabled) }
    }

    override fun isIntercharacterGapCheckEnabled(deviceId: String): Boolean {
        return CodabarSettingDescriptors.intercharacterGapCheck.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setStartStopTransmission(
        deviceId: String, transmission: CodabarStartStopTransmission
    ): CommandResponse {
        val command = CodabarSettingDescriptors.startStopTransmission.commandFor(transmission)
        Timber.d("Setting Codabar start/stop transmission for deviceId $deviceId to $transmission")
        return sendMappedCommand(deviceId, command, "Unsupported Codabar start/stop transmission: $transmission")
    }

    override fun setStartStopTransmission(deviceId: String, transmission: CodabarStartStopTransmission, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setStartStopTransmission(deviceId, transmission) }
    }

    override fun getStartStopTransmission(deviceId: String): CodabarStartStopTransmission {
        return CodabarSettingDescriptors.startStopTransmission.valueFrom(settingsFor(deviceId))
    }
}
