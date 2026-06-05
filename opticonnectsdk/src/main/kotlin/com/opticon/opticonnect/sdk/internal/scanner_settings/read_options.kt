package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.*
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ReadOptions
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.ReadOptionsSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ReadOptionsImpl @Inject constructor(
    private val scannerSettingsStateStore: ScannerSettingsStateStore
) : ReadOptions, SettingsBase() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setPositiveAndNegativeBarcodesMode(deviceId: String, mode: PositiveAndNegativeBarcodesMode): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.positiveAndNegativeBarcodesMode.commandFor(mode)
            ?: throw IllegalArgumentException("Invalid PositiveAndNegativeBarcodesMode"))
    }

    override fun setPositiveAndNegativeBarcodesMode(
        deviceId: String,
        mode: PositiveAndNegativeBarcodesMode,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPositiveAndNegativeBarcodesMode(deviceId, mode) }
    }

    override fun getPositiveAndNegativeBarcodesMode(deviceId: String): PositiveAndNegativeBarcodesMode {
        return ReadOptionsSettingDescriptors.positiveAndNegativeBarcodesMode.valueFrom(
            scannerSettingsStateStore.settingsFor(deviceId)
        )
    }

    override suspend fun setReadMode(deviceId: String, mode: ReadMode): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.readMode.commandFor(mode)
            ?: throw IllegalArgumentException("Invalid ReadMode"))
    }

    override fun setReadMode(
        deviceId: String,
        mode: ReadMode,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setReadMode(deviceId, mode) }
    }

    override fun getReadMode(deviceId: String): ReadMode {
        return ReadOptionsSettingDescriptors.readMode.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setReadTime(deviceId: String, time: ReadTime): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.readTime.commandFor(time)
            ?: throw IllegalArgumentException("Invalid ReadTime"))
    }

    override fun setReadTime(
        deviceId: String,
        time: ReadTime,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setReadTime(deviceId, time) }
    }

    override fun getReadTime(deviceId: String): ReadTime {
        return ReadOptionsSettingDescriptors.readTime.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setIlluminationMode(deviceId: String, mode: IlluminationMode): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.illuminationMode.commandFor(mode)
            ?: throw IllegalArgumentException("Invalid IlluminationMode"))
    }

    override fun setIlluminationMode(
        deviceId: String,
        mode: IlluminationMode,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setIlluminationMode(deviceId, mode) }
    }

    override fun getIlluminationMode(deviceId: String): IlluminationMode {
        return ReadOptionsSettingDescriptors.illuminationMode.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setAiming(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.aiming.commandFor(enabled))
    }

    override fun setAiming(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setAiming(deviceId, enabled) }
    }

    override fun isAimingEnabled(deviceId: String): Boolean {
        return ReadOptionsSettingDescriptors.aiming.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setTriggerRepeat(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.triggerRepeat.commandFor(enabled))
    }

    override fun setTriggerRepeat(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTriggerRepeat(deviceId, enabled) }
    }

    override fun isTriggerRepeatEnabled(deviceId: String): Boolean {
        return ReadOptionsSettingDescriptors.triggerRepeat.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setDeleteKey(deviceId: String, enabled: Boolean): CommandResponse {
        return sendCommand(deviceId, ReadOptionsSettingDescriptors.deleteKey.commandFor(enabled))
    }

    override fun setDeleteKey(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setDeleteKey(deviceId, enabled) }
    }

    override fun isDeleteKeyEnabled(deviceId: String): Boolean {
        return ReadOptionsSettingDescriptors.deleteKey.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }
}
