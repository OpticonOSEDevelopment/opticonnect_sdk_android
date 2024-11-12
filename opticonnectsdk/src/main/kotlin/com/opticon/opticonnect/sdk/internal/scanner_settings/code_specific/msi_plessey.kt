package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCDTransmissionSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.MSIPlessey
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MSIPlesseyImpl @Inject constructor() : SettingsBase(), MSIPlessey {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val checkCDCommands: Map<MSIPlesseyCheckCDSettings, String> = mapOf(
        MSIPlesseyCheckCDSettings.DO_NOT_CHECK_CD to CodeSpecificCommands.MSI_PLESSEY_DO_NOT_CHECK_CD,
        MSIPlesseyCheckCDSettings.CHECK_1_CD_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_1_CD_MOD_10,
        MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD10_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_10,
        MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD10_MOD11 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_11,
        MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD11_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10,
        MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD11_MOD11 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_11
    )

    private val cdTransmissionCommands: Map<MSIPlesseyCDTransmissionSettings, String> = mapOf(
        MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1 to CodeSpecificCommands.MSI_PLESSEY_TRANSMIT_CD_1,
        MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1_AND_CD2 to CodeSpecificCommands.MSI_PLESSEY_TRANSMIT_CD_1_AND_CD_2,
        MSIPlesseyCDTransmissionSettings.DO_NOT_TRANSMIT_CD to CodeSpecificCommands.MSI_PLESSEY_DO_NOT_TRANSMIT_CD
    )

    override suspend fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings): CommandResponse {
        val command = checkCDCommands[setting]
        Timber.d("Setting MSI Plessey check digit mode for deviceId $deviceId to $setting")
        return sendCommand(deviceId, command!!)
    }

    override fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, setting) }
    }

    override suspend fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings): CommandResponse {
        val command = cdTransmissionCommands[setting]
        Timber.d("Setting MSI Plessey check digit transmission mode for deviceId $deviceId to $setting")
        return sendCommand(deviceId, command!!)
    }

    override fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCDTransmission(deviceId, setting) }
    }
}
