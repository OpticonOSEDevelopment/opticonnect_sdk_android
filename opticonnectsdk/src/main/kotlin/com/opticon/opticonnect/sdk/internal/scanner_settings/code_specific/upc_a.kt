package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCALeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCA
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCAImpl @Inject constructor() : SettingsBase(), UPCA {

    private val upcALeadingZeroAndTransmitCDModeCommands: Map<UPCALeadingZeroAndTransmitCDMode, String> = mapOf(
        UPCALeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_A_NO_LEADING_ZERO_TRANSMIT_CD,
        UPCALeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_A_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD,
        UPCALeadingZeroAndTransmitCDMode.LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_A_LEADING_ZERO_TRANSMIT_CD,
        UPCALeadingZeroAndTransmitCDMode.LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_A_LEADING_ZERO_DO_NOT_TRANSMIT_CD
    )

    override suspend fun setLeadingZeroAndTransmitCDMode(deviceId: String, mode: UPCALeadingZeroAndTransmitCDMode): CommandResponse {
        val command = upcALeadingZeroAndTransmitCDModeCommands[mode]
        Timber.d("Setting UPC_A leading zero and transmit CD mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_A_PLUS_2
        } else {
            SymbologyCommands.DISABLE_UPC_A_PLUS_2
        }
        Timber.d("Setting UPC_A plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_A_PLUS_5
        } else {
            SymbologyCommands.DISABLE_UPC_A_PLUS_5
        }
        Timber.d("Setting UPC_A plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
