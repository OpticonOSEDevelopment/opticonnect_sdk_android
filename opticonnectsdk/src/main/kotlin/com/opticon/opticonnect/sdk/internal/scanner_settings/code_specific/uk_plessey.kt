package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UKPlessey
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.UKPlesseySettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UKPlesseyImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), UKPlessey {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCDs(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UKPlesseySettingDescriptors.transmitCDs.commandFor(enabled)
        Timber.d("Setting UK Plessey transmit check digits for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCDs(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCDs(deviceId, enabled) }
    }

    override fun isTransmitCDsEnabled(deviceId: String): Boolean {
        return UKPlesseySettingDescriptors.transmitCDs.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UKPlesseySettingDescriptors.spaceInsertion.commandFor(enabled)
        Timber.d("Setting UK Plessey space insertion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setSpaceInsertion(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSpaceInsertion(deviceId, enabled) }
    }

    override fun isSpaceInsertionEnabled(deviceId: String): Boolean {
        return UKPlesseySettingDescriptors.spaceInsertion.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setAToXConversion(deviceId: String, enabled: Boolean): CommandResponse {
        val command = UKPlesseySettingDescriptors.aToXConversion.commandFor(enabled)
        Timber.d("Setting UK Plessey A to X conversion for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setAToXConversion(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAToXConversion(deviceId, enabled) }
    }

    override fun isAToXConversionEnabled(deviceId: String): Boolean {
        return UKPlesseySettingDescriptors.aToXConversion.valueFrom(settingsFor(deviceId))
    }
}
