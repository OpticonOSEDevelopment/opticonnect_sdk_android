package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.KoreanPostalAuthority
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.KoreanPostalAuthoritySettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class KoreanPostalAuthorityImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), KoreanPostalAuthority {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = KoreanPostalAuthoritySettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting Korean Postal transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return KoreanPostalAuthoritySettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitDash(deviceId: String, enabled: Boolean): CommandResponse {
        val command = KoreanPostalAuthoritySettingDescriptors.transmitDash.commandFor(enabled)
        Timber.d("Setting Korean Postal transmit dash for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitDash(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitDash(deviceId, enabled) }
    }

    override fun isTransmitDashEnabled(deviceId: String): Boolean {
        return KoreanPostalAuthoritySettingDescriptors.transmitDash.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setKoreanPostalOrientation(deviceId: String, setUpsideDown: Boolean): CommandResponse {
        val command = KoreanPostalAuthoritySettingDescriptors.orientationUpsideDown.commandFor(setUpsideDown)
        Timber.d("Setting Korean Postal orientation for deviceId $deviceId to ${if (setUpsideDown) "upside-down" else "normal"}")
        return sendCommand(deviceId, command)
    }

    override fun setKoreanPostalOrientation(deviceId: String, setUpsideDown: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setKoreanPostalOrientation(deviceId, setUpsideDown) }
    }

    override fun isKoreanPostalOrientationUpsideDown(deviceId: String): Boolean {
        return KoreanPostalAuthoritySettingDescriptors.orientationUpsideDown.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setUpsideDownReading(deviceId: String, enabled: Boolean): CommandResponse {
        val command = KoreanPostalAuthoritySettingDescriptors.upsideDownReading.commandFor(enabled)
        Timber.d("Setting Korean Postal upside-down reading for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setUpsideDownReading(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setUpsideDownReading(deviceId, enabled) }
    }

    override fun isUpsideDownReadingEnabled(deviceId: String): Boolean {
        return KoreanPostalAuthoritySettingDescriptors.upsideDownReading.valueFrom(settingsFor(deviceId))
    }
}
