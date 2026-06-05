package com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors

import com.opticon.opticonnect.sdk.api.constants.commands.IndicatorCommands
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.GoodReadLedDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.VibratorDuration

internal object IndicatorSettingDescriptors {
    val buzzerEnabled = BooleanCommandSetting(
        group = "enableBuzzer",
        defaultValue = true,
        enabledCommand = IndicatorCommands.BUZZER_ENABLED,
        disabledCommand = IndicatorCommands.BUZZER_DISABLED
    )

    val buzzerVolume = ParameterSetting(
        group = "buzzerLoudness",
        command = IndicatorCommands.PERSISTENT_SET_BUZZER,
        defaultValue = 100,
        encode = { volume -> volume.toDirectInputDigits() },
        decode = { parameters -> parameters.toBuzzerVolumeOrNull() }
    )

    val ledColor = ParameterSetting(
        group = "persistentLed",
        command = IndicatorCommands.PERSISTENT_SET_LED,
        defaultValue = LEDColor(red = 0, green = 0, blue = 0),
        encode = { color -> color.toParameters() },
        decode = { parameters -> parameters.toLedColorOrNull() }
    )

    val buzzerType = EnumCommandSetting(
        group = "buzzerType",
        defaultValue = BuzzerType.HIGH_LOW_BUZZER,
        commandsByValue = mapOf(
            BuzzerType.SINGLE_TONE_BUZZER to IndicatorCommands.SINGLE_TONE_BUZZER,
            BuzzerType.HIGH_LOW_BUZZER to IndicatorCommands.HIGH_LOW_BUZZER,
            BuzzerType.LOW_HIGH_BUZZER to IndicatorCommands.LOW_HIGH_BUZZER
        )
    )

    val buzzerDuration = EnumCommandSetting(
        group = "buzzerDuration",
        defaultValue = BuzzerDuration.DURATION_200_MS,
        commandsByValue = mapOf(
            BuzzerDuration.DURATION_50_MS to IndicatorCommands.BUZZER_DURATION_50_MS,
            BuzzerDuration.DURATION_75_MS to IndicatorCommands.BUZZER_DURATION_75_MS,
            BuzzerDuration.DURATION_100_MS to IndicatorCommands.BUZZER_DURATION_100_MS,
            BuzzerDuration.DURATION_200_MS to IndicatorCommands.BUZZER_DURATION_200_MS,
            BuzzerDuration.DURATION_400_MS to IndicatorCommands.BUZZER_DURATION_400_MS
        )
    )

    val vibratorEnabled = BooleanCommandSetting(
        group = "vibrator",
        defaultValue = true,
        enabledCommand = IndicatorCommands.VIBRATOR_ENABLED,
        disabledCommand = IndicatorCommands.VIBRATOR_DISABLED
    )

    val vibratorDuration = EnumCommandSetting(
        group = "vibrationDuration",
        defaultValue = VibratorDuration.DURATION_100_MS,
        commandsByValue = mapOf(
            VibratorDuration.DURATION_50_MS to IndicatorCommands.VIBRATOR_DURATION_50_MS,
            VibratorDuration.DURATION_100_MS to IndicatorCommands.VIBRATOR_DURATION_100_MS
        )
    )

    val goodReadLedDuration = EnumCommandSetting(
        group = "goodReadLEDDuration",
        defaultValue = GoodReadLedDuration.DURATION_200_MS,
        commandsByValue = mapOf(
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
    )

    private fun String.toColorComponentOrNull(): Int? {
        return trimSettingsQuotes()
            .removePrefix("\$")
            .toIntOrNull(radix = 16)
            ?.takeIf { it in 0..255 }
    }

    private fun List<String>.toLedColorOrNull(): LEDColor? {
        if (size >= 6) {
            val hex = take(6).mapNotNull { parameter ->
                parameter.trimSettingsQuotes().directInputKeyToCharOrNull()
            }.joinToString(separator = "")

            if (hex.length == 6) {
                val red = hex.substring(0, 2).toIntOrNull(radix = 16)
                val green = hex.substring(2, 4).toIntOrNull(radix = 16)
                val blue = hex.substring(4, 6).toIntOrNull(radix = 16)

                if (red != null && green != null && blue != null) {
                    return LEDColor(red, green, blue)
                }
            }
        }

        if (size >= 3) {
            val red = this[0].toColorComponentOrNull()
            val green = this[1].toColorComponentOrNull()
            val blue = this[2].toColorComponentOrNull()

            if (red != null && green != null && blue != null) {
                return LEDColor(red, green, blue)
            }
        }

        return null
    }

    private fun Int.toDirectInputDigits(): List<String> {
        return toString().map { digit -> "Q$digit" }
    }

    private fun List<String>.toBuzzerVolumeOrNull(): Int? {
        val directInputDigits = map { parameter ->
            parameter.trimSettingsQuotes()
        }.takeIf { parameters ->
            parameters.isNotEmpty() && parameters.all { it.length == 2 && it.startsWith("Q") && it[1].isDigit() }
        }?.joinToString(separator = "") { parameter ->
            parameter[1].toString()
        }

        return directInputDigits?.toIntOrNull()
            ?: firstOrNull()?.trimSettingsQuotes()?.toIntOrNull()
    }

    private fun String.trimSettingsQuotes(): String {
        return trim('\'')
    }

    private fun String.directInputKeyToCharOrNull(): Char? {
        return when {
            length == 2 && startsWith("Q") && this[1].isDigit() -> this[1]
            length == 2 && startsWith("$") && this[1] in 'A'..'Z' -> this[1].lowercaseChar()
            length == 2 && startsWith("0") && this[1] in 'A'..'Z' -> this[1]
            else -> null
        }
    }
}
