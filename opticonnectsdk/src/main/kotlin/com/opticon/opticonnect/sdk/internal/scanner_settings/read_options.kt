package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.ScanCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.*
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ReadOptions
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReadOptionsImpl @Inject constructor() : ReadOptions, SettingsBase() {

    private val positiveAndNegativeBarcodesModeCommands = mapOf(
        PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES to ScanCommands.POSITIVE_BARCODES,
        PositiveAndNegativeBarcodesMode.NEGATIVE_BARCODES to ScanCommands.NEGATIVE_BARCODES,
        PositiveAndNegativeBarcodesMode.POSITIVE_AND_NEGATIVE_BARCODES to ScanCommands.POSITIVE_AND_NEGATIVE_BARCODES
    )

    private val readModeCommands = mapOf(
        ReadMode.SINGLE_READ to ScanCommands.SINGLE_READ,
        ReadMode.MULTIPLE_READ to ScanCommands.MULTIPLE_READ
    )

    private val readTimeCommands = mapOf(
        ReadTime.ZERO_SECONDS to ScanCommands.READ_TIME_0_SECONDS,
        ReadTime.ONE_SECOND to ScanCommands.READ_TIME_1_SECOND,
        ReadTime.TWO_SECONDS to ScanCommands.READ_TIME_2_SECONDS,
        ReadTime.THREE_SECONDS to ScanCommands.READ_TIME_3_SECONDS,
        ReadTime.FOUR_SECONDS to ScanCommands.READ_TIME_4_SECONDS,
        ReadTime.FIVE_SECONDS to ScanCommands.READ_TIME_5_SECONDS,
        ReadTime.SIX_SECONDS to ScanCommands.READ_TIME_6_SECONDS,
        ReadTime.SEVEN_SECONDS to ScanCommands.READ_TIME_7_SECONDS,
        ReadTime.EIGHT_SECONDS to ScanCommands.READ_TIME_8_SECONDS,
        ReadTime.NINE_SECONDS to ScanCommands.READ_TIME_9_SECONDS,
        ReadTime.INDEFINITE to ScanCommands.READ_TIME_INDEFINITELY
    )

    private val illuminationModeCommands = mapOf(
        IlluminationMode.ENABLE_FLOODLIGHT to ScanCommands.ENABLE_FLOODLIGHT,
        IlluminationMode.DISABLE_FLOODLIGHT to ScanCommands.DISABLE_FLOODLIGHT,
        IlluminationMode.ALTERNATING_FLOODLIGHT to ScanCommands.ALTERNATING_FLOODLIGHT,
        IlluminationMode.PREVENT_SPECULAR_REFLECTION to ScanCommands.PREVENT_SPECULAR_REFLECTION
    )

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setPositiveAndNegativeBarcodesMode(deviceId: String, mode: PositiveAndNegativeBarcodesMode): CommandResponse {
        return sendCommand(deviceId, positiveAndNegativeBarcodesModeCommands[mode]
            ?: throw IllegalArgumentException("Invalid PositiveAndNegativeBarcodesMode"))
    }

    override fun setPositiveAndNegativeBarcodesMode(
        deviceId: String,
        mode: PositiveAndNegativeBarcodesMode,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPositiveAndNegativeBarcodesMode(deviceId, mode) }
    }

    override suspend fun setReadMode(deviceId: String, mode: ReadMode): CommandResponse {
        return sendCommand(deviceId, readModeCommands[mode]
            ?: throw IllegalArgumentException("Invalid ReadMode"))
    }

    override fun setReadMode(
        deviceId: String,
        mode: ReadMode,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setReadMode(deviceId, mode) }
    }

    override suspend fun setReadTime(deviceId: String, time: ReadTime): CommandResponse {
        return sendCommand(deviceId, readTimeCommands[time]
            ?: throw IllegalArgumentException("Invalid ReadTime"))
    }

    override fun setReadTime(
        deviceId: String,
        time: ReadTime,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setReadTime(deviceId, time) }
    }

    override suspend fun setIlluminationMode(deviceId: String, mode: IlluminationMode): CommandResponse {
        return sendCommand(deviceId, illuminationModeCommands[mode]
            ?: throw IllegalArgumentException("Invalid IlluminationMode"))
    }

    override fun setIlluminationMode(
        deviceId: String,
        mode: IlluminationMode,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setIlluminationMode(deviceId, mode) }
    }

    override suspend fun setAiming(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, if (enabled) ScanCommands.AIMING_ENABLED else ScanCommands.AIMING_DISABLED)
    }

    override fun setAiming(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAiming(deviceId, enabled) }
    }

    override suspend fun setTriggerRepeat(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, if (enabled) ScanCommands.TRIGGER_REPEAT_ENABLED else ScanCommands.TRIGGER_REPEAT_DISABLED)
    }

    override fun setTriggerRepeat(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTriggerRepeat(deviceId, enabled) }
    }

    override suspend fun setDeleteKey(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, if (enabled) ScanCommands.DELETE_KEY_ENABLED else ScanCommands.DELETE_KEY_DISABLED)
    }

    override fun setDeleteKey(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setDeleteKey(deviceId, enabled) }
    }
}
