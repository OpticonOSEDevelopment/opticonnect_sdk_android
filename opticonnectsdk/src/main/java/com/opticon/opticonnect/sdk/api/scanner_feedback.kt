package com.opticon.opticonnect.sdk.api

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configures the feedback behavior of the scanner when executing commands.
 *
 * This class allows you to update the individual feedback settings for LED, buzzer,
 * and vibration. The feedback is applied when the scanner executes certain commands,
 * controlling the visual (LED), auditory (buzzer), and tactile (vibration) responses.
 */
@Singleton
class ScannerFeedback @Inject constructor() {
    private var _led: Boolean = true

    /**
     * Indicates whether LED feedback is enabled.
     *
     * When `true`, the scanner will emit a light (LED) as a form of feedback when executing commands.
     * Default value is `true`.
     */
    val led: Boolean
        get() = _led

    private var _buzzer: Boolean = true

    /**
     * Indicates whether buzzer feedback is enabled.
     *
     * When `true`, the scanner will emit a sound (buzzer) as a form of feedback when executing commands.
     * Default value is `true`.
     */
    val buzzer: Boolean
        get() = _buzzer

    private var _vibration: Boolean = true

    /**
     * Indicates whether vibration feedback is enabled.
     *
     * When `true`, the scanner will vibrate as a form of feedback when executing commands, if the scanner supports vibration.
     * Default value is `true`.
     */
    val vibration: Boolean
        get() = _vibration

    /**
     * Sets the feedback preferences for the scanner.
     *
     * Allows updating the individual feedback settings for LED, buzzer, and vibration.
     * These settings are applied to the commands sent to the scanner to adjust scanner feedback behavior.
     *
     * @param led Set this to `true` to enable LED feedback, or `false` to disable it. If `null`, the setting remains unchanged.
     * @param buzzer Set this to `true` to enable buzzer feedback, or `false` to disable it. If `null`, the setting remains unchanged.
     * @param vibration Set this to `true` to enable vibration feedback, or `false` to disable it. If `null`, the setting remains unchanged.
     */
    fun set(
        led: Boolean? = null,
        buzzer: Boolean? = null,
        vibration: Boolean? = null
    ) {
        led?.let { _led = it }
        buzzer?.let { _buzzer = it }
        vibration?.let { _vibration = it }
    }
}
