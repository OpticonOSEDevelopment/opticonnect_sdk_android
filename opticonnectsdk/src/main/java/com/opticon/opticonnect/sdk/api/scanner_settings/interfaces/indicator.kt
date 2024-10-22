package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.VibratorDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.GoodReadLedDuration

/**
 * Interface that provides methods for controlling scanner indicator options.
 */
interface Indicator {

    /**
     * Toggles the buzzer on or off for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param enabled True to enable the buzzer, false to disable it.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun toggleBuzzer(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Tests the volume without storing it (non-persistent).
     * @param deviceId The identifier of the target device.
     * @param volume The volume value to be set (range: 0-100).
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun testBuzzerVolume(deviceId: String, volume: Int): CommandResponse

    /**
     * Sets the volume of the buzzer for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param volume The volume to be set (range: 0-100).
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setBuzzerVolume(deviceId: String, volume: Int): CommandResponse

    /**
     * Sets the duration of the buzzer sound for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param duration The duration of the buzzer, as defined by [BuzzerDuration].
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setBuzzerDuration(deviceId: String, duration: BuzzerDuration): CommandResponse

    /**
     * Sets the buzzer type for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param type The type of buzzer sound.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setBuzzerType(deviceId: String, type: BuzzerType): CommandResponse

    /**
     * Toggles the vibrator on or off for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param enabled True to enable the vibrator, false to disable it.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun toggleVibrator(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the duration of the vibration for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param duration The duration of the vibration, as defined by [VibratorDuration].
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setVibratorDuration(deviceId: String, duration: VibratorDuration): CommandResponse

    /**
     * Sets the LED color for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param color The LED color.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setLED(deviceId: String, color: LEDColor): CommandResponse

    /**
     * Tests the LED by setting the color temporarily (non-persistent).
     * @param deviceId The identifier of the target device.
     * @param color The [LEDColor] object that contains the red, green, and blue values.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun testLED(deviceId: String, color: LEDColor): CommandResponse

    /**
     * Sets the good read LED duration for the given [deviceId].
     * @param deviceId The identifier of the target device.
     * @param duration The duration of the LED.
     * @return A [CommandResponse] indicating the result.
     */
    suspend fun setGoodReadLedDuration(deviceId: String, duration: GoodReadLedDuration): CommandResponse
}
