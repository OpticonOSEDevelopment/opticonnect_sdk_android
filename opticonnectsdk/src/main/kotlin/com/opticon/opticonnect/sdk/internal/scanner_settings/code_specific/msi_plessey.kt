package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCDTransmissionSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.MSIPlessey
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.MSIPlesseySettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MSIPlesseyImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), MSIPlessey {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings): CommandResponse {
        val command = MSIPlesseySettingDescriptors.checkCD.commandFor(setting)
        Timber.d("Setting MSI Plessey check digit mode for deviceId $deviceId to $setting")
        return sendMappedCommand(deviceId, command, "Unsupported MSI Plessey check digit setting: $setting")
    }

    override fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, setting) }
    }

    override fun getCheckCD(deviceId: String): MSIPlesseyCheckCDSettings {
        return MSIPlesseySettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings): CommandResponse {
        val command = MSIPlesseySettingDescriptors.cdTransmission.commandFor(setting)
        Timber.d("Setting MSI Plessey check digit transmission mode for deviceId $deviceId to $setting")
        return sendMappedCommand(deviceId, command, "Unsupported MSI Plessey check digit transmission setting: $setting")
    }

    override fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCDTransmission(deviceId, setting) }
    }

    override fun getCDTransmission(deviceId: String): MSIPlesseyCDTransmissionSettings {
        return MSIPlesseySettingDescriptors.cdTransmission.valueFrom(settingsFor(deviceId))
    }
}
