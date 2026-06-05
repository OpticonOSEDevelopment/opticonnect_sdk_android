package com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors

import com.opticon.opticonnect.sdk.api.constants.commands.ScanCommands
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.PositiveAndNegativeBarcodesMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime

internal object ReadOptionsSettingDescriptors {
    val positiveAndNegativeBarcodesMode = EnumCommandSetting(
        group = "positiveNegativeBarcodes",
        defaultValue = PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES,
        commandsByValue = mapOf(
            PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES to ScanCommands.POSITIVE_BARCODES,
            PositiveAndNegativeBarcodesMode.NEGATIVE_BARCODES to ScanCommands.NEGATIVE_BARCODES,
            PositiveAndNegativeBarcodesMode.POSITIVE_AND_NEGATIVE_BARCODES to ScanCommands.POSITIVE_AND_NEGATIVE_BARCODES
        )
    )

    val readMode = EnumCommandSetting(
        group = "readMode",
        defaultValue = ReadMode.SINGLE_READ,
        commandsByValue = mapOf(
            ReadMode.SINGLE_READ to ScanCommands.SINGLE_READ,
            ReadMode.MULTIPLE_READ to ScanCommands.MULTIPLE_READ,
            ReadMode.CONTINUOUS_READ to ScanCommands.CONTINUOUS_READ
        )
    )

    val readTime = EnumCommandSetting(
        group = "readTime",
        defaultValue = ReadTime.TWO_SECONDS,
        commandsByValue = mapOf(
            ReadTime.ZERO_SECONDS to ScanCommands.READ_TIME_0_SECONDS,
            ReadTime.ONE_SECOND to ScanCommands.READ_TIME_1_SECOND,
            ReadTime.TWO_SECONDS to ScanCommands.READ_TIME_2_SECONDS,
            ReadTime.THREE_SECONDS to ScanCommands.READ_TIME_3_SECONDS,
            ReadTime.FOUR_SECONDS to ScanCommands.READ_TIME_4_SECONDS,
            ReadTime.FIVE_SECONDS to ScanCommands.READ_TIME_5_SECONDS,
            ReadTime.SIX_SECONDS to ScanCommands.READ_TIME_6_SECONDS,
            ReadTime.SEVEN_SECONDS to ScanCommands.READ_TIME_7_SECONDS,
            ReadTime.EIGHT_SECONDS to ScanCommands.READ_TIME_8_SECONDS,
            ReadTime.NINE_SECONDS to ScanCommands.READ_TIME_9_SECONDS,
            ReadTime.INDEFINITE to ScanCommands.READ_TIME_INDEFINITELY
        )
    )

    val illuminationMode = EnumCommandSetting(
        group = "illuminationMode",
        defaultValue = IlluminationMode.ENABLE_FLOODLIGHT,
        commandsByValue = mapOf(
            IlluminationMode.ENABLE_FLOODLIGHT to ScanCommands.ENABLE_FLOODLIGHT,
            IlluminationMode.DISABLE_FLOODLIGHT to ScanCommands.DISABLE_FLOODLIGHT,
            IlluminationMode.ALTERNATING_FLOODLIGHT to ScanCommands.ALTERNATING_FLOODLIGHT,
            IlluminationMode.PREVENT_SPECULAR_REFLECTION to ScanCommands.PREVENT_SPECULAR_REFLECTION
        )
    )

    val aiming = BooleanCommandSetting(
        group = "aimingMode",
        defaultValue = true,
        enabledCommand = ScanCommands.AIMING_ENABLED,
        disabledCommand = ScanCommands.AIMING_DISABLED
    )

    val triggerRepeat = BooleanCommandSetting(
        group = "triggerRepeat",
        defaultValue = false,
        enabledCommand = ScanCommands.TRIGGER_REPEAT_ENABLED,
        disabledCommand = ScanCommands.TRIGGER_REPEAT_DISABLED
    )

    val deleteKey = BooleanCommandSetting(
        group = "deleteKey",
        defaultValue = true,
        enabledCommand = ScanCommands.DELETE_KEY_ENABLED,
        disabledCommand = ScanCommands.DELETE_KEY_DISABLED
    )
}
