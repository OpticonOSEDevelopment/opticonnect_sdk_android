package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.DataLength
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code2Of5AndSCode
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code2Of5AndSCodeImpl @Inject constructor() : SettingsBase(), Code2Of5AndSCode {

    private val dataLengthCommands: Map<DataLength, String> = mapOf(
        DataLength.ONE_CHARACTER to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_ONE_CHAR,
        DataLength.THREE_CHARACTERS to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_THREE_CHARS,
        DataLength.FIVE_CHARACTERS to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_FIVE_CHARS
    )

    override suspend fun setSpaceCheck(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_ENABLE_SPACE_CHECK_INDUSTRIAL_2OF5
        } else {
            CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_DISABLE_SPACE_CHECK_INDUSTRIAL_2OF5
        }
        Timber.d("Setting space check for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setSCodeTransmissionAsInterleaved(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_TRANSMIT_AS_INTERLEAVED_2OF5
        } else {
            CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_DO_NOT_TRANSMIT_AS_INTERLEAVED_2OF5
        }
        Timber.d("Setting S-Code transmission as Interleaved for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setMinimumDataLength(deviceId: String, dataLength: DataLength): CommandResponse {
        val command = dataLengthCommands[dataLength]
        Timber.d("Setting minimum data length for deviceId $deviceId to $dataLength")
        return sendCommand(deviceId, command!!)
    }
}
