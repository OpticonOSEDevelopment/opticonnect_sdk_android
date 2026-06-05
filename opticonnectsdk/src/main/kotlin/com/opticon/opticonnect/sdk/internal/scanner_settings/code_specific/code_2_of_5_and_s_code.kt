package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.DataLength
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code2Of5AndSCode
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.Code2Of5AndSCodeSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code2Of5AndSCodeImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Code2Of5AndSCode {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setSpaceCheck(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code2Of5AndSCodeSettingDescriptors.spaceCheck.commandFor(enabled)
        Timber.d("Setting space check for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setSpaceCheck(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSpaceCheck(deviceId, enabled) }
    }

    override fun isSpaceCheckEnabled(deviceId: String): Boolean {
        return Code2Of5AndSCodeSettingDescriptors.spaceCheck.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setSCodeTransmissionAsInterleaved(deviceId: String, enabled: Boolean): CommandResponse {
        val command = Code2Of5AndSCodeSettingDescriptors.sCodeTransmissionAsInterleaved.commandFor(enabled)
        Timber.d("Setting S-Code transmission as Interleaved for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setSCodeTransmissionAsInterleaved(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSCodeTransmissionAsInterleaved(deviceId, enabled) }
    }

    override fun isSCodeTransmissionAsInterleavedEnabled(deviceId: String): Boolean {
        return Code2Of5AndSCodeSettingDescriptors.sCodeTransmissionAsInterleaved.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setMinimumDataLength(deviceId: String, dataLength: DataLength): CommandResponse {
        val command = Code2Of5AndSCodeSettingDescriptors.minimumDataLength.commandFor(dataLength)
        Timber.d("Setting minimum data length for deviceId $deviceId to $dataLength")
        return sendMappedCommand(deviceId, command, "Unsupported 2 of 5/S-Code minimum data length: $dataLength")
    }

    override fun setMinimumDataLength(
        deviceId: String,
        dataLength: DataLength,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMinimumDataLength(deviceId, dataLength) }
    }

    override fun getMinimumDataLength(deviceId: String): DataLength {
        return Code2Of5AndSCodeSettingDescriptors.minimumDataLength.valueFrom(settingsFor(deviceId))
    }
}
