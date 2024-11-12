package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.TelepenMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Telepen
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TelepenImpl @Inject constructor() : SettingsBase(), Telepen {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val telepenCodeModeCommands: Map<TelepenMode, String> = mapOf(
        TelepenMode.NUMERIC to CodeSpecificCommands.TELEPEN_NUMERIC_MODE,
        TelepenMode.ASCII to CodeSpecificCommands.TELEPEN_ASCII_MODE
    )

    override suspend fun setMode(deviceId: String, mode: TelepenMode): CommandResponse {
        val command = telepenCodeModeCommands[mode]
        Timber.d("Setting Telepen code mode for deviceId $deviceId to $mode")
        return sendCommand(deviceId, command!!)
    }

    override fun setMode(deviceId: String, mode: TelepenMode, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMode(deviceId, mode) }
    }
}
