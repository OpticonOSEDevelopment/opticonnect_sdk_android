package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.KoreanPostalAuthority
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class KoreanPostalAuthorityImpl @Inject constructor() : SettingsBase(), KoreanPostalAuthority {

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.KOREAN_POSTAL_TRANSMIT_CD
        } else {
            CodeSpecificCommands.KOREAN_POSTAL_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting Korean Postal transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitDash(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.KOREAN_POSTAL_TRANSMIT_DASH
        } else {
            CodeSpecificCommands.KOREAN_POSTAL_DO_NOT_TRANSMIT_DASH
        }
        Timber.d("Setting Korean Postal transmit dash for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setKoreanPostalOrientation(deviceId: String, setUpsideDown: Boolean): CommandResponse {
        val command = if (setUpsideDown) {
            CodeSpecificCommands.KOREAN_POSTAL_ORIENTATION_UPSIDE_DOWN
        } else {
            CodeSpecificCommands.KOREAN_POSTAL_ORIENTATION_NORMAL
        }
        Timber.d("Setting Korean Postal orientation for deviceId $deviceId to ${if (setUpsideDown) "upside-down" else "normal"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setUpsideDownReading(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.KOREAN_POSTAL_UPSIDE_DOWN_READING_ENABLED
        } else {
            CodeSpecificCommands.KOREAN_POSTAL_UPSIDE_DOWN_READING_DISABLED
        }
        Timber.d("Setting Korean Postal upside-down reading for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
