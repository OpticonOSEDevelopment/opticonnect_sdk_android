package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.IndicatorCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.*
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Indicator
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class IndicatorImpl @Inject constructor() : Indicator, SettingsBase() {

    private val buzzerTypeCommands = mapOf(
        BuzzerType.SINGLE_TONE_BUZZER to IndicatorCommands.SINGLE_TONE_BUZZER,
        BuzzerType.HIGH_LOW_BUZZER to IndicatorCommands.HIGH_LOW_BUZZER,
        BuzzerType.LOW_HIGH_BUZZER to IndicatorCommands.LOW_HIGH_BUZZER
    )

    private val buzzerDurationCommands = mapOf(
        BuzzerDuration.DURATION_50_MS to IndicatorCommands.BUZZER_DURATION_50_MS,
        BuzzerDuration.DURATION_75_MS to IndicatorCommands.BUZZER_DURATION_75_MS,
        BuzzerDuration.DURATION_100_MS to IndicatorCommands.BUZZER_DURATION_100_MS,
        BuzzerDuration.DURATION_200_MS to IndicatorCommands.BUZZER_DURATION_200_MS,
        BuzzerDuration.DURATION_400_MS to IndicatorCommands.BUZZER_DURATION_400_MS
    )

    private val vibratorDurationCommands = mapOf(
        VibratorDuration.DURATION_50_MS to IndicatorCommands.VIBRATOR_DURATION_50_MS,
        VibratorDuration.DURATION_100_MS to IndicatorCommands.VIBRATOR_DURATION_100_MS
    )

    private val goodReadLedDurationCommands = mapOf(
        GoodReadLedDuration.DISABLED to IndicatorCommands.GOOD_READ_LED_DURATION_DISABLED,
        GoodReadLedDuration.DURATION_10_MS to IndicatorCommands.GOOD_READ_LED_DURATION_10_MS,
        GoodReadLedDuration.DURATION_60_MS to IndicatorCommands.GOOD_READ_LED_DURATION_60_MS,
        GoodReadLedDuration.DURATION_100_MS to IndicatorCommands.GOOD_READ_LED_DURATION_100_MS,
        GoodReadLedDuration.DURATION_200_MS to IndicatorCommands.GOOD_READ_LED_DURATION_200_MS,
        GoodReadLedDuration.DURATION_400_MS to IndicatorCommands.GOOD_READ_LED_DURATION_400_MS,
        GoodReadLedDuration.DURATION_500_MS to IndicatorCommands.GOOD_READ_LED_DURATION_500_MS,
        GoodReadLedDuration.DURATION_800_MS to IndicatorCommands.GOOD_READ_LED_DURATION_800_MS,
        GoodReadLedDuration.DURATION_2000_MS to IndicatorCommands.GOOD_READ_LED_DURATION_2000_MS
    )

    companion object {
        const val maxPreambleChars = 8
        const val maxSuffixChars = 4
        const val maxPrefixChars = 4
        const val maxPostambleChars = 8
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun toggleBuzzer(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) IndicatorCommands.BUZZER_ENABLED else IndicatorCommands.BUZZER_DISABLED
        Timber.d("Toggling buzzer for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun toggleBuzzer(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { toggleBuzzer(deviceId, enabled) }
    }

    override suspend fun testBuzzerVolume(deviceId: String, volume: Int): CommandResponse {
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        Timber.d("Testing buzzer volume for deviceId $deviceId with volume $volume")
        return sendCommand(deviceId, IndicatorCommands.NON_PERSISTENT_SET_BUZZER, parameters = listOf(volume.toString()))
    }

    override fun testBuzzerVolume(
        deviceId: String,
        volume: Int,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { testBuzzerVolume(deviceId, volume) }
    }

    override suspend fun setBuzzerVolume(deviceId: String, volume: Int): CommandResponse {
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        Timber.d("Setting buzzer volume for deviceId $deviceId to $volume")
        return sendCommand(deviceId, IndicatorCommands.PERSISTENT_SET_BUZZER, parameters = listOf(volume.toString()))
    }

    override fun setBuzzerVolume(
        deviceId: String,
        volume: Int,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerVolume(deviceId, volume) }
    }

    override suspend fun setBuzzerDuration(deviceId: String, duration: BuzzerDuration): CommandResponse {
        val command = buzzerDurationCommands[duration]
        Timber.d("Setting buzzer duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Buzzer Duration")
    }

    override fun setBuzzerDuration(
        deviceId: String,
        duration: BuzzerDuration,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerDuration(deviceId, duration) }
    }

    override suspend fun setBuzzerType(deviceId: String, type: BuzzerType): CommandResponse {
        val command = buzzerTypeCommands[type]
        Timber.d("Setting buzzer type for deviceId $deviceId to $type")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Buzzer Type")
    }

    override fun setBuzzerType(
        deviceId: String,
        type: BuzzerType,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setBuzzerType(deviceId, type) }
    }

    override suspend fun toggleVibrator(deviceId: String, enabled: Boolean): CommandResponse {
        val command = if (enabled) IndicatorCommands.VIBRATOR_ENABLED else IndicatorCommands.VIBRATOR_DISABLED
        Timber.d("Toggling vibrator for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun toggleVibrator(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { toggleVibrator(deviceId, enabled) }
    }

    override suspend fun setVibratorDuration(deviceId: String, duration: VibratorDuration): CommandResponse {
        val command = vibratorDurationCommands[duration]
        Timber.d("Setting vibrator duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid Vibrator Duration")
    }

    override fun setVibratorDuration(
        deviceId: String,
        duration: VibratorDuration,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setVibratorDuration(deviceId, duration) }
    }

    override suspend fun setLED(deviceId: String, color: LEDColor): CommandResponse {
        Timber.d("Setting LED color for deviceId $deviceId to $color")
        return sendCommand(
            deviceId,
            IndicatorCommands.PERSISTENT_SET_LED,
            parameters = color.toParameters(),
            sendFeedback = false
        )
    }

    override fun setLED(
        deviceId: String,
        color: LEDColor,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setLED(deviceId, color) }
    }

    override suspend fun testLED(deviceId: String, color: LEDColor): CommandResponse {
        Timber.d("Testing LED color for deviceId $deviceId with color $color")
        return sendCommand(
            deviceId,
            IndicatorCommands.NON_PERSISTENT_SET_LED,
            parameters = color.toParameters(),
            sendFeedback = false
        )
    }

    override fun testLED(
        deviceId: String,
        color: LEDColor,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { testLED(deviceId, color) }
    }

    override suspend fun setGoodReadLedDuration(deviceId: String, duration: GoodReadLedDuration): CommandResponse {
        val command = goodReadLedDurationCommands[duration]
        Timber.d("Setting good read LED duration for deviceId $deviceId to $duration")
        return command?.let { sendCommand(deviceId, it) } ?: CommandResponse.failed("Invalid LED Duration")
    }

    override fun setGoodReadLedDuration(
        deviceId: String,
        duration: GoodReadLedDuration,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setGoodReadLedDuration(deviceId, duration) }
    }

    private fun LEDColor.toParameters(): List<String> {
        return listOf(red.toString(), green.toString(), blue.toString())
    }
}
