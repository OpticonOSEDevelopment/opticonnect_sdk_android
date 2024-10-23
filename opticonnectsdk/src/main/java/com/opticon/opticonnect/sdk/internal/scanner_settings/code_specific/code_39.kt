package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39MinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code39
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code39Impl @Inject constructor() : SettingsBase(), Code39 {

    private val modeCommands: Map<Code39Mode, String> = mapOf(
        Code39Mode.NORMAL to CodeSpecificCommands.NORMAL_CODE_39,
        Code39Mode.FULL_ASCII to CodeSpecificCommands.FULL_ASCII_CODE_39,
        Code39Mode.FULL_ASCII_IF_POSSIBLE to CodeSpecificCommands.FULL_ASCII_CODE_39_IF_POSSIBLE,
        Code39Mode.IT_PHARMACEUTICAL_ONLY to CodeSpecificCommands.IT_PHARMACEUTICAL_ONLY,
        Code39Mode.IT_PHARMACEUTICAL_IF_POSSIBLE to CodeSpecificCommands.IT_PHARMACEUTICAL_IF_POSSIBLE
    )

    private val minLengthCommands: Map<Code39MinimumLength, String> = mapOf(
        Code39MinimumLength.ONE_DIGIT to CodeSpecificCommands.CODE_39_MIN_LENGTH_1_DIGIT,
        Code39MinimumLength.THREE_DIGITS to CodeSpecificCommands.CODE_39_MIN_LENGTH_3_DIGITS
    )

    override suspend fun setMode(deviceId: String, mode: Code39Mode): CommandResponse {
        val command = modeCommands[mode]
        Timber.d("Setting Code 39 mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_39_CHECK_CD
        } else {
            CodeSpecificCommands.CODE_39_DO_NOT_CHECK_CD
        }
        Timber.d("Setting Code 39 check digit validation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_39_TRANSMIT_CD
        } else {
            CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting Code 39 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitSTSP(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_39_TRANSMIT_ST_SP
        } else {
            CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_ST_SP
        }
        Timber.d("Setting Code 39 transmit start/stop characters for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_39_ENABLE_CONCATENATION
        } else {
            CodeSpecificCommands.CODE_39_DISABLE_CONCATENATION
        }
        Timber.d("Setting Code 39 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitLdAForItPharm(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_39_TRANSMIT_LEADING_A_FOR_IT_PHARM
        } else {
            CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_LEADING_A_FOR_IT_PHARM
        }
        Timber.d("Setting Code 39 transmit leading 'A' for IT Pharmaceutical for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setMinLength(deviceId: String, length: Code39MinimumLength): CommandResponse {
        val command = minLengthCommands[length]
        Timber.d("Setting Code 39 minimum length for deviceId $deviceId to $length")
        return sendCommand(deviceId, command!!)
    }
}
