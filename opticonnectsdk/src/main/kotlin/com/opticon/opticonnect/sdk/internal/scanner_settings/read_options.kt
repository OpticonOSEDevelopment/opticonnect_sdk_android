package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.scan.ScanCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.*
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ReadOptions
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadOptionsImpl @Inject constructor() : ReadOptions, SettingsBase() {

    // Map Positive and Negative Barcodes Modes to corresponding command strings.
    private val positiveAndNegativeBarcodesModeCommands = mapOf(
        PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES to ScanCommands.POSITIVE_BARCODES,
        PositiveAndNegativeBarcodesMode.NEGATIVE_BARCODES to ScanCommands.NEGATIVE_BARCODES,
        PositiveAndNegativeBarcodesMode.POSITIVE_AND_NEGATIVE_BARCODES to ScanCommands.POSITIVE_AND_NEGATIVE_BARCODES
    )

    // Map Read Modes to corresponding command strings.
    private val readModeCommands = mapOf(
        ReadMode.SINGLE_READ to ScanCommands.SINGLE_READ,
        ReadMode.MULTIPLE_READ to ScanCommands.MULTIPLE_READ
    )

    // Map Read Times to corresponding command strings.
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

    // Map Illumination Modes to corresponding command strings.
    private val illuminationModeCommands = mapOf(
        IlluminationMode.ENABLE_FLOODLIGHT to ScanCommands.ENABLE_FLOODLIGHT,
        IlluminationMode.DISABLE_FLOODLIGHT to ScanCommands.DISABLE_FLOODLIGHT,
        IlluminationMode.ALTERNATING_FLOODLIGHT to ScanCommands.ALTERNATING_FLOODLIGHT,
        IlluminationMode.PREVENT_SPECULAR_REFLECTION to ScanCommands.PREVENT_SPECULAR_REFLECTION
    )

    override suspend fun setPositiveAndNegativeBarcodesMode(deviceId: String, mode: PositiveAndNegativeBarcodesMode): CommandResponse {
        Timber.d("Setting PositiveAndNegativeBarcodesMode to $mode")
        return sendCommand(deviceId, positiveAndNegativeBarcodesModeCommands[mode] ?: throw IllegalArgumentException("Invalid mode"))
    }

    override suspend fun setReadMode(deviceId: String, mode: ReadMode): CommandResponse {
        Timber.d("Setting ReadMode to $mode")
        return sendCommand(deviceId, readModeCommands[mode] ?: throw IllegalArgumentException("Invalid mode"))
    }

    override suspend fun setReadTime(deviceId: String, time: ReadTime): CommandResponse {
        Timber.d("Setting ReadTime to $time")
        return sendCommand(deviceId, readTimeCommands[time] ?: throw IllegalArgumentException("Invalid time"))
    }

    override suspend fun setIlluminationMode(deviceId: String, mode: IlluminationMode): CommandResponse {
        Timber.d("Setting IlluminationMode to $mode")
        return sendCommand(deviceId, illuminationModeCommands[mode] ?: throw IllegalArgumentException("Invalid mode"))
    }

    override suspend fun setAiming(deviceId: String, enabled: Boolean): CommandResponse {
        Timber.d("Setting Aiming to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, if (enabled) ScanCommands.AIMING_ENABLED else ScanCommands.AIMING_DISABLED)
    }

    override suspend fun setTriggerRepeat(deviceId: String, enabled: Boolean): CommandResponse {
        Timber.d("Setting TriggerRepeat to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, if (enabled) ScanCommands.TRIGGER_REPEAT_ENABLED else ScanCommands.TRIGGER_REPEAT_DISABLED)
    }

    override suspend fun setDeleteKey(deviceId: String, enabled: Boolean): CommandResponse {
        Timber.d("Setting DeleteKey to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, if (enabled) ScanCommands.DELETE_KEY_ENABLED else ScanCommands.DELETE_KEY_DISABLED)
    }
}
