package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.symbology.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN13
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EAN13Impl @Inject constructor() : SettingsBase(), EAN13 {

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.EAN_13_TRANSMIT_CD
        } else {
            CodeSpecificCommands.EAN_13_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting EAN-13 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_EAN_13_PLUS_2
        } else {
            SymbologyCommands.DISABLE_EAN_13_PLUS_2
        }
        Timber.d("Setting EAN-13 plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_EAN_13_PLUS_5
        } else {
            SymbologyCommands.DISABLE_EAN_13_PLUS_5
        }
        Timber.d("Setting EAN-13 plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}