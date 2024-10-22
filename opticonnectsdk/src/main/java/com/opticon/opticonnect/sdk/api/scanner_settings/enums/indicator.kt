package com.opticon.opticonnect.sdk.api.scanner_settings.enums

/**
 * Enum representing the different types of buzzer sounds.
 */
enum class BuzzerType {
    SINGLE_TONE_BUZZER,
    HIGH_LOW_BUZZER,
    LOW_HIGH_BUZZER
}

/**
 * Enum representing different durations for the buzzer sound.
 */
enum class BuzzerDuration {
    DURATION_50_MS,
    DURATION_75_MS,
    DURATION_100_MS,
    DURATION_200_MS,
    DURATION_400_MS
}

/**
 * Enum representing different durations for the vibration.
 */
enum class VibratorDuration {
    DURATION_50_MS,
    DURATION_100_MS
}

/**
 * Enum representing different durations for the good read LED.
 */
enum class GoodReadLedDuration {
    DISABLED,
    DURATION_10_MS,
    DURATION_60_MS,
    DURATION_100_MS,
    DURATION_200_MS,
    DURATION_400_MS,
    DURATION_500_MS,
    DURATION_800_MS,
    DURATION_2000_MS
}