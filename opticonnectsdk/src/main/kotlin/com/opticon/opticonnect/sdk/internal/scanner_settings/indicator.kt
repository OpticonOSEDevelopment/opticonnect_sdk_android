package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.constants.commands.IndicatorCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.*
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Indicator
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.IndicatorSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class IndicatorImpl @Inject constructor(
    private val scannerSettingsStateStore: ScannerSettingsStateStore
) : Indicator, SettingsBase() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun toggleBuzzer(deviceId: String, enabled: Boolean): CommandResponse {
        val command = IndicatorSettingDescriptors.buzzerEnabled.commandFor(enabled)
        Timber.d("Toggling buzzer for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun toggleBuzzer(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { toggleBuzzer(deviceId, enabled) }
    }

    override fun isBuzzerEnabled(deviceId: String): Boolean {
        return IndicatorSettingDescriptors.buzzerEnabled.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun testBuzzerVolume(deviceId: String, volume: Int): CommandResponse {
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        Timber.d("Testing buzzer volume for deviceId $deviceId with volume $volume")
        return sendCommand(
            deviceId,
            IndicatorCommands.NON_PERSISTENT_SET_BUZZER,
            parameters = IndicatorSettingDescriptors.buzzerVolume.parametersFor(volume)
        )
    }

    override fun testBuzzerVolume(
        deviceId: String,
        volume: Int,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { testBuzzerVolume(deviceId, volume) }
    }

    override suspend fun setBuzzerVolume(deviceId: String, volume: Int): CommandResponse {
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        Timber.d("Setting buzzer volume for deviceId $deviceId to $volume")
        return sendCommand(
            deviceId,
            IndicatorSettingDescriptors.buzzerVolume.command,
            parameters = IndicatorSettingDescriptors.buzzerVolume.parametersFor(volume)
        )
    }

    override fun setBuzzerVolume(
        deviceId: String,
        volume: Int,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerVolume(deviceId, volume) }
    }

    override fun getBuzzerVolume(deviceId: String): Int {
        return IndicatorSettingDescriptors.buzzerVolume.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setBuzzerDuration(deviceId: String, duration: BuzzerDuration): CommandResponse {
        val command = IndicatorSettingDescriptors.buzzerDuration.commandFor(duration)
        Timber.d("Setting buzzer duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Buzzer Duration")
    }

    override fun setBuzzerDuration(
        deviceId: String,
        duration: BuzzerDuration,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerDuration(deviceId, duration) }
    }

    override fun getBuzzerDuration(deviceId: String): BuzzerDuration {
        return IndicatorSettingDescriptors.buzzerDuration.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setBuzzerType(deviceId: String, type: BuzzerType): CommandResponse {
        val command = IndicatorSettingDescriptors.buzzerType.commandFor(type)
        Timber.d("Setting buzzer type for deviceId $deviceId to $type")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Buzzer Type")
    }

    override fun setBuzzerType(
        deviceId: String,
        type: BuzzerType,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerType(deviceId, type) }
    }

    override fun getBuzzerType(deviceId: String): BuzzerType {
        return IndicatorSettingDescriptors.buzzerType.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun toggleVibrator(deviceId: String, enabled: Boolean): CommandResponse {
        val command = IndicatorSettingDescriptors.vibratorEnabled.commandFor(enabled)
        Timber.d("Toggling vibrator for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun toggleVibrator(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { toggleVibrator(deviceId, enabled) }
    }

    override fun isVibratorEnabled(deviceId: String): Boolean {
        return IndicatorSettingDescriptors.vibratorEnabled.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setVibratorDuration(deviceId: String, duration: VibratorDuration): CommandResponse {
        val command = IndicatorSettingDescriptors.vibratorDuration.commandFor(duration)
        Timber.d("Setting vibrator duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Vibrator Duration")
    }

    override fun setVibratorDuration(
        deviceId: String,
        duration: VibratorDuration,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setVibratorDuration(deviceId, duration) }
    }

    override fun getVibratorDuration(deviceId: String): VibratorDuration {
        return IndicatorSettingDescriptors.vibratorDuration.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun setLED(deviceId: String, color: LEDColor): CommandResponse {
        Timber.d("Setting LED color for deviceId $deviceId to $color")
        return sendCommand(
            deviceId,
            IndicatorSettingDescriptors.ledColor.command,
            parameters = IndicatorSettingDescriptors.ledColor.parametersFor(color)
        )
    }

    override fun setLED(
        deviceId: String,
        color: LEDColor,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLED(deviceId, color) }
    }

    override fun getLED(deviceId: String): LEDColor {
        return IndicatorSettingDescriptors.ledColor.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }

    override suspend fun testLED(deviceId: String, color: LEDColor): CommandResponse {
        Timber.d("Testing LED color for deviceId $deviceId with color $color")
        return sendCommand(
            deviceId,
            IndicatorCommands.NON_PERSISTENT_SET_LED,
            parameters = IndicatorSettingDescriptors.ledColor.parametersFor(color),
            sendFeedback = false
        )
    }

    override fun testLED(
        deviceId: String,
        color: LEDColor,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { testLED(deviceId, color) }
    }

    override suspend fun setGoodReadLedDuration(deviceId: String, duration: GoodReadLedDuration): CommandResponse {
        val command = IndicatorSettingDescriptors.goodReadLedDuration.commandFor(duration)
        Timber.d("Setting good read LED duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid LED Duration")
    }

    override fun setGoodReadLedDuration(
        deviceId: String,
        duration: GoodReadLedDuration,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setGoodReadLedDuration(deviceId, duration) }
    }

    override fun getGoodReadLedDuration(deviceId: String): GoodReadLedDuration {
        return IndicatorSettingDescriptors.goodReadLedDuration.valueFrom(scannerSettingsStateStore.settingsFor(deviceId))
    }
}
