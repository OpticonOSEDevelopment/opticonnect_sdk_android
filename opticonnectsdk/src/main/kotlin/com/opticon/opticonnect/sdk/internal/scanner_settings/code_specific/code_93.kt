package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code93
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class Code93Impl @Inject constructor() : SettingsBase(), Code93 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_93_CHECK_CD
        } else {
            CodeSpecificCommands.CODE_93_DO_NOT_CHECK_CD
        }
        Timber.d("Setting Code 93 check digit validation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setCheckCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, enabled) }
    }

    override suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.CODE_93_ENABLE_CONCATENATION
        } else {
            CodeSpecificCommands.CODE_93_DISABLE_CONCATENATION
        }
        Timber.d("Setting Code 93 concatenation for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setConcatenation(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setConcatenation(deviceId, enabled) }
    }
}