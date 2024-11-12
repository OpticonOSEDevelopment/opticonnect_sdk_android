package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN8
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EAN8Impl @Inject constructor() : SettingsBase(), EAN8 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.EAN_8_TRANSMIT_CD
        } else {
            CodeSpecificCommands.EAN_8_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting EAN-8 transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
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

    override fun setAddOnPlus2(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
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

    override fun setAddOnPlus5(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }
}
