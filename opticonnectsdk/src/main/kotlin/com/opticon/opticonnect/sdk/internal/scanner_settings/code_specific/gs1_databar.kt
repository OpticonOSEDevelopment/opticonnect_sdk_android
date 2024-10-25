package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.GS1Databar
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GS1DatabarImpl @Inject constructor() : SettingsBase(), GS1Databar {

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.GS1_DATABAR_TRANSMIT_CD
        } else {
            CodeSpecificCommands.GS1_DATABAR_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting GS1 Databar transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override suspend fun setTransmitAI(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.GS1_DATABAR_TRANSMIT_AI
        } else {
            CodeSpecificCommands.GS1_DATABAR_DO_NOT_TRANSMIT_AI
        }
        Timber.d("Setting GS1 Databar transmit AI for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }
}
