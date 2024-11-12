package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UKPlessey
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UKPlesseyImpl @Inject constructor() : SettingsBase(), UKPlessey {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCDs(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.UK_PLESSEY_TRANSMIT_CDS
        } else {
            CodeSpecificCommands.UK_PLESSEY_DO_NOT_TRANSMIT_CDS
        }
        Timber.d("Setting UK Plessey transmit check digits for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCDs(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCDs(deviceId, enabled) }
    }

    override suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.UK_PLESSEY_ENABLE_SPACE_INSERTION
        } else {
            CodeSpecificCommands.UK_PLESSEY_DISABLE_SPACE_INSERTION
        }
        Timber.d("Setting UK Plessey space insertion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setSpaceInsertion(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSpaceInsertion(deviceId, enabled) }
    }

    override suspend fun setAToXConversion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.UK_PLESSEY_ENABLE_A_TO_X_CONVERSION
        } else {
            CodeSpecificCommands.UK_PLESSEY_DISABLE_A_TO_X_CONVERSION
        }
        Timber.d("Setting UK Plessey A to X conversion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAToXConversion(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAToXConversion(deviceId, enabled) }
    }
}
