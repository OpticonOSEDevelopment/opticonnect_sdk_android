package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.symbology.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN8
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EAN8Impl @Inject constructor() : SettingsBase(), EAN8 {

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.EAN_8_TRANSMIT_CD
        } else {
            CodeSpecificCommands.EAN_8_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting EAN-8 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_EAN_8_PLUS_2
        } else {
            SymbologyCommands.DISABLE_EAN_8_PLUS_2
        }
        Timber.d("Setting EAN-8 plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_EAN_8_PLUS_5
        } else {
            SymbologyCommands.DISABLE_EAN_8_PLUS_5
        }
        Timber.d("Setting EAN-8 plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
