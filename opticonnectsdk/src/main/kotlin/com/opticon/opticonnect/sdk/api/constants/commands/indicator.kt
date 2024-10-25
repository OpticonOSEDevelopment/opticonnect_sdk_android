package com.opticon.opticonnect.sdk.api.constants.commands

object IndicatorCommands {
    const val NON_PERSISTENT_SET_BUZZER = "[BQX"
    const val PERSISTENT_SET_BUZZER = "[BQY"

    const val NON_PERSISTENT_SET_LED = "[BMB"
    const val PERSISTENT_SET_LED = "[BMC"

    const val BUZZER_DISABLED = "W0"
    const val BUZZER_ENABLED = "W8"

    const val SINGLE_TONE_BUZZER = "W1"
    const val HIGH_LOW_BUZZER = "W2"
    const val LOW_HIGH_BUZZER = "W3"

    const val BUZZER_DURATION_50_MS = "W7"
    const val BUZZER_DURATION_75_MS = "[EFW"
    const val BUZZER_DURATION_100_MS = "W4"
    const val BUZZER_DURATION_200_MS = "W5"
    const val BUZZER_DURATION_400_MS = "W6"

    const val BUZZER_LOUDNESS_MINIMUM = "T3"
    const val BUZZER_LOUDNESS_NORMAL = "T2"
    const val BUZZER_LOUDNESS_LOUD = "T1"
    const val BUZZER_LOUDNESS_MAXIMUM = "T0"

    const val BUZZER_ON_KEYCLICK_ON = "WK"
    const val BUZZER_ON_KEYCLICK_OFF = "WL"

    const val GOOD_READ_LED_DURATION_DISABLED = "T4"
    const val GOOD_READ_LED_DURATION_10_MS = "[XT9"
    const val GOOD_READ_LED_DURATION_60_MS = "[XTH"
    const val GOOD_READ_LED_DURATION_100_MS = "[XT8"
    const val GOOD_READ_LED_DURATION_200_MS = "T5"
    const val GOOD_READ_LED_DURATION_400_MS = "T6"
    const val GOOD_READ_LED_DURATION_500_MS = "[XTI"
    const val GOOD_READ_LED_DURATION_800_MS = "T7"
    const val GOOD_READ_LED_DURATION_2000_MS = "[XTJ"

    const val VIBRATOR_ENABLED = "[EBI"
    const val VIBRATOR_DISABLED = "[EBH"

    const val VIBRATOR_DURATION_50_MS = "[EBJ"
    const val VIBRATOR_DURATION_100_MS = "[EBK"

    const val ENABLE_VIBRATION_ON_BUTTON_PRESS = "BO"
    const val DISABLE_VIBRATION_ON_BUTTON_PRESS = "BP"

    const val BATTERY_CHARGING_INDICATOR_ENABLED = "7G"
    const val BATTERY_CHARGING_INDICATOR_DISABLED = "8Z"

    const val GET_BATTERY_PERCENTAGE = "]BATT"
}
