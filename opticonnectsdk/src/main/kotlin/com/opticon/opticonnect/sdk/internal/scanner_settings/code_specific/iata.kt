package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.IATACheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.IATA
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.IATASettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class IATAImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), IATA {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setCheckCD(deviceId: String, setting: IATACheckCDSettings): CommandResponse {
        val command = IATASettingDescriptors.checkCD.commandFor(setting)
        Timber.d("Setting IATA check digit mode for deviceId $deviceId to $setting")
        return sendMappedCommand(deviceId, command, "Unsupported IATA check digit setting: $setting")
    }

    override fun setCheckCD(deviceId: String, setting: IATACheckCDSettings, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setCheckCD(deviceId, setting) }
    }

    override fun getCheckCD(deviceId: String): IATACheckCDSettings {
        return IATASettingDescriptors.checkCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = IATASettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting IATA transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return IATASettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }
}
