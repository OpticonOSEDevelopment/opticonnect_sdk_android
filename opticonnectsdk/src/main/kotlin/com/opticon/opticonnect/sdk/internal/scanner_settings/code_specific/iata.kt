package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.IATACheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.IATA
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class IATAImpl @Inject constructor() : SettingsBase(), IATA {

    private val checkCDCommands: Map<IATACheckCDSettings, String> = mapOf(
        IATACheckCDSettings.DO_NOT_CHECK_CD to CodeSpecificCommands.IATA_DO_NOT_CHECK_CD,
        IATACheckCDSettings.CHECK_FC_AND_SN_ONLY to CodeSpecificCommands.IATA_CHECK_FC_AND_SN_ONLY,
        IATACheckCDSettings.CHECK_CPN_FC_AND_SN to CodeSpecificCommands.IATA_CHECK_CPN_FC_AND_SN,
        IATACheckCDSettings.CHECK_CPN_AC_FC_AND_SN to CodeSpecificCommands.IATA_CHECK_CPN_AC_FC_AND_SN
    )

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, setting: IATACheckCDSettings): CommandResponse {
        val command = checkCDCommands[setting]
        Timber.d("Setting IATA check digit mode for deviceId $deviceId to $setting")
        return sendCommand(deviceId, command!!)
    }

    override fun setCheckCD(deviceId: String, setting: IATACheckCDSettings, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, setting) }
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            CodeSpecificCommands.IATA_TRANSMIT_CD
        } else {
            CodeSpecificCommands.IATA_DO_NOT_TRANSMIT_CD
        }
        Timber.d("Setting IATA transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }
}
