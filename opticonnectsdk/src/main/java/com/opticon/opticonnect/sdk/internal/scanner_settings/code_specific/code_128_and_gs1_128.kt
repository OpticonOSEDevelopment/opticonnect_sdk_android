package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code128AndGS1128Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.GS1128ConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code128AndGS1128
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code128AndGS1128Impl @Inject constructor() : SettingsBase(), Code128AndGS1128 {

    private val modeCommands: Map<Code128AndGS1128Mode, String> = mapOf(
        Code128AndGS1128Mode.DISABLE_GS1_128 to CodeSpecificCommands.CODE_128_DISABLE_GS1_128,
        Code128AndGS1128Mode.ENABLE_GS1_128_ONLY to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_ONLY,
        Code128AndGS1128Mode.ENABLE_GS1_128_IF_POSSIBLE to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_IF_POSSIBLE
    )

    private val conversionCommands: Map<GS1128ConversionMode, String> = mapOf(
        GS1128ConversionMode.DISABLED to CodeSpecificCommands.CODE_128_DISABLE_GS1_128_CONVERSION,
        GS1128ConversionMode.ENABLE_MODE_1 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_1,
        GS1128ConversionMode.ENABLE_MODE_2 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_2,
        GS1128ConversionMode.ENABLE_MODE_3 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_3,
        GS1128ConversionMode.ENABLE_MODE_4 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_4
    )

    override suspend fun setGS1128Mode(deviceId: String, mode: Code128AndGS1128Mode): CommandResponse {
        val command = modeCommands[mode]
        Timber.d("Setting GS1-128 mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setGS1128ConversionMode(deviceId: String, mode: GS1128ConversionMode): CommandResponse {
        val command = conversionCommands[mode]
        Timber.d("Setting GS1-128 conversion mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_128_ENABLE_CONCATENATION
        } else {
            CodeSpecificCommands.CODE_128_DISABLE_CONCATENATION
        }
        Timber.d("Setting Code 128 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setLeadingC1Output(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_128_ENABLE_LEADING_C1_OUTPUT
        } else {
            CodeSpecificCommands.CODE_128_DISABLE_LEADING_C1_OUTPUT
        }
        Timber.d("Setting leading C1 output for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
