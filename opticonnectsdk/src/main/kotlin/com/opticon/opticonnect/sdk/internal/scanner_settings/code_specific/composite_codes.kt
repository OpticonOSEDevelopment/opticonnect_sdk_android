package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CompositeCodesOutputMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CompositeCodes
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CompositeCodesImpl @Inject constructor() : SettingsBase(), CompositeCodes {

    private val outputModeCommands: Map<CompositeCodesOutputMode, String> = mapOf(
        CompositeCodesOutputMode.ONLY_1D_AND_2D_COMPONENTS_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_1D_AND_2D_COMPONENT,
        CompositeCodesOutputMode.ONLY_2D_COMPONENT_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_2D_COMPONENT,
        CompositeCodesOutputMode.ONLY_1D_COMPONENT_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_1D_COMPONENT
    )

    override suspend fun setOutputMode(deviceId: String, outputMode: CompositeCodesOutputMode): CommandResponse {
        val command = outputModeCommands[outputMode]
        Timber.d("Setting composite codes output mode for deviceId $deviceId to $outputMode")
        return sendCommand(deviceId, command!!)
    }

    override suspend fun setIgnoreLinkFlag(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.COMPOSITE_IGNORE_LINK_FLAG
        } else {
            CodeSpecificCommands.COMPOSITE_DO_NOT_IGNORE_LINK_FLAG
        }
        Timber.d("Setting composite codes link flag for deviceId $deviceId to ${if (enabled) "ignored" else "not ignored"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setCompositeGS1DatabarGS1128(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.ENABLE_COMPOSITE_GS1
        } else {
            CodeSpecificCommands.DISABLE_COMPOSITE_GS1
        }
        Timber.d("Setting composite GS1 for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setCompositeEANUPC(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.ENABLE_COMPOSITE_EAN_UPC
        } else {
            CodeSpecificCommands.DISABLE_COMPOSITE_EAN_UPC
        }
        Timber.d("Setting composite EAN/UPC for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
