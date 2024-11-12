package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCELeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCEConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCEImpl @Inject constructor() : SettingsBase(), UPCE {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val upceLeadingZeroAndTransmitCDModeCommands: Map<UPCELeadingZeroAndTransmitCDMode, String> = mapOf(
        UPCELeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_E_NO_LEADING_ZERO_TRANSMIT_CD,
        UPCELeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_E_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD,
        UPCELeadingZeroAndTransmitCDMode.LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_E_LEADING_ZERO_TRANSMIT_CD,
        UPCELeadingZeroAndTransmitCDMode.LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_E_LEADING_ZERO_DO_NOT_TRANSMIT_CD
    )

    private val upceConversionModeCommands: Map<UPCEConversionMode, String> = mapOf(
        UPCEConversionMode.TRANSMIT_AS_IS to CodeSpecificCommands.UPC_E_TRANSMIT_AS_IS,
        UPCEConversionMode.TRANSMIT_AS_UPC_A to CodeSpecificCommands.UPC_E_TRANSMIT_AS_UPC_A
    )

    override suspend fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode
    ): CommandResponse {
        val command = upceLeadingZeroAndTransmitCDModeCommands[mode]
        Timber.d("Setting UPCE leading zero and transmit CD mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLeadingZeroAndTransmitCDMode(deviceId, mode) }
    }

    override suspend fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode
    ): CommandResponse {
        val command = upceConversionModeCommands[mode]
        Timber.d("Setting UPCE conversion mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConversionMode(deviceId, mode) }
    }

    override suspend fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_E_PLUS_2
        } else {
            SymbologyCommands.DISABLE_UPC_E_PLUS_2
        }
        Timber.d("Setting UPCE plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override suspend fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_E_PLUS_5
        } else {
            SymbologyCommands.DISABLE_UPC_E_PLUS_5
        }
        Timber.d("Setting UPCE plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }
}
