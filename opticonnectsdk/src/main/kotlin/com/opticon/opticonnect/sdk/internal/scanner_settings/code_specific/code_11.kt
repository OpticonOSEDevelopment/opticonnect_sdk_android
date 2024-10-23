package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code11CheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code11
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code11Impl @Inject constructor() : SettingsBase(), Code11 {

    private val checkCDCommands: Map<Code11CheckCDSettings, String> = mapOf(
        Code11CheckCDSettings.DO_NOT_CHECK to CodeSpecificCommands.CODE_11_DO_NOT_CHECK_CD,
        Code11CheckCDSettings.CHECK_1_CD to CodeSpecificCommands.CODE_11_CHECK_1_CD,
        Code11CheckCDSettings.CHECK_2_CDS to CodeSpecificCommands.CODE_11_CHECK_2_CDS,
        Code11CheckCDSettings.CHECK_1_CD_OR_2_CDS_AUTOMATICALLY to CodeSpecificCommands.CODE_11_CHECK_1_OR_2_CDS
    )

    override suspend fun setCheckCD(deviceId: String, setting: Code11CheckCDSettings): CommandResponse {
        val command = checkCDCommands[setting]
        Timber.d("Setting Code 11 check digit validation for deviceId $deviceId to $setting")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_11_TRANSMIT_CD
        } else {
            CodeSpecificCommands.CODE_11_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting Code 11 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
