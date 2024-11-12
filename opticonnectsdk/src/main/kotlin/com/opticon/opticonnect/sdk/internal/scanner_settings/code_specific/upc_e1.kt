package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE1
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UPCE1Impl @Inject constructor() : SettingsBase(), UPCE1 {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_E1_PLUS_2
        } else {
            SymbologyCommands.DISABLE_UPC_E1_PLUS_2
        }
        Timber.d("Setting UPCE1 plus 2 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus2(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus2(deviceId, enabled) }
    }

    override suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            SymbologyCommands.ENABLE_UPC_E1_PLUS_5
        } else {
            SymbologyCommands.DISABLE_UPC_E1_PLUS_5
        }
        Timber.d("Setting UPCE1 plus 5 add-on for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAddOnPlus5(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAddOnPlus5(deviceId, enabled) }
    }
}
