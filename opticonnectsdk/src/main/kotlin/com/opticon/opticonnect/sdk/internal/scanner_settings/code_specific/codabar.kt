package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarStartStopTransmission
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Codabar
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CodabarImpl @Inject constructor() : SettingsBase(), Codabar {

    private val modeCommands: Map<CodabarMode, String> = mapOf(
        CodabarMode.NORMAL to CodeSpecificCommands.CODABAR_NORMAL,
        CodabarMode.ABC_CODE_ONLY to CodeSpecificCommands.CODABAR_ABC_CODE_ONLY,
        CodabarMode.CX_CODE_ONLY to CodeSpecificCommands.CODABAR_CX_CODE_ONLY,
        CodabarMode.CODABAR_ABC_AND_CX to CodeSpecificCommands.CODABAR_ABC_AND_CX
    )

    private val startStopTransmissionCommands: Map<CodabarStartStopTransmission, String> = mapOf(
        CodabarStartStopTransmission.DO_NOT_TRANSMIT_START_STOP to CodeSpecificCommands.CODABAR_DO_NOT_TRANSMIT_ST_SP,
        CodabarStartStopTransmission.START_STOP_ABCD_ABCD to CodeSpecificCommands.CODABAR_ST_SP_ABCD_ABCD,
        CodabarStartStopTransmission.START_STOP_ABCD_ABCD_LOWER to CodeSpecificCommands.CODABAR_ST_SP_ABCD_ABCD_LOWERCASE,
        CodabarStartStopTransmission.START_STOP_ABCD_TNX_E to CodeSpecificCommands.CODABAR_ST_SP_ABCD_TNE,
        CodabarStartStopTransmission.START_STOP_ABCD_TNX_E_LOWER to CodeSpecificCommands.CODABAR_ST_SP_ABCD_TNE_LOWERCASE,
        CodabarStartStopTransmission.START_STOP_DC1_DC2_DC3_DC4 to CodeSpecificCommands.CODABAR_ST_SP_DC1_DC2_DC3_DC4
    )

    private val minLengthCommands: Map<CodabarMinimumLength, String> = mapOf(
        CodabarMinimumLength.ONE_CHARACTER to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_ONE_CHAR,
        CodabarMinimumLength.THREE_CHARACTERS to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_THREE_CHARS,
        CodabarMinimumLength.FIVE_CHARACTERS to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_FIVE_CHARS
    )

    override suspend fun setMode(deviceId: String, mode: CodabarMode): CommandResponse {
        val command = modeCommands[mode]
        Timber.d("Setting Codabar mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODABAR_CHECK_CD
        } else {
            CodeSpecificCommands.CODABAR_DO_NOT_CHECK_CD
        }
        Timber.d("Setting Codabar check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODABAR_TRANSMIT_CD
        } else {
            CodeSpecificCommands.CODABAR_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting Codabar transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODABAR_ENABLE_SPACE_INSERTION
        } else {
            CodeSpecificCommands.CODABAR_DISABLE_SPACE_INSERTION
        }
        Timber.d("Setting Codabar space insertion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setMinimumLength(deviceId: String, length: CodabarMinimumLength): CommandResponse {
        val command = minLengthCommands[length]
        Timber.d("Setting Codabar minimum length for deviceId $deviceId to $length")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setIntercharacterGapCheck(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODABAR_ENABLE_INTERCHARACTER_GAP_CHECK
        } else {
            CodeSpecificCommands.CODABAR_DISABLE_INTERCHARACTER_GAP_CHECK
        }
        Timber.d("Setting Codabar intercharacter gap check for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setStartStopTransmission(
        deviceId: String, transmission: CodabarStartStopTransmission
    ): CommandResponse {
        val command = startStopTransmissionCommands[transmission]
        Timber.d("Setting Codabar start/stop transmission for deviceId $deviceId to $transmission")
        return sendCommand(deviceId, command!!)
    }
}
