package com.opticon.opticonnect.sdk.api.constants.commands

object ScanCommands {
    const val SINGLE_READ = "S0"
    const val MULTIPLE_READ = "S1"
    const val CONTINUOUS_READ = "S2"

    const val READ_TIME_0_SECONDS = "Y0"
    const val READ_TIME_1_SECOND = "Y1"
    const val READ_TIME_2_SECONDS = "Y2"
    const val READ_TIME_3_SECONDS = "Y3"
    const val READ_TIME_4_SECONDS = "Y4"
    const val READ_TIME_5_SECONDS = "Y5"
    const val READ_TIME_6_SECONDS = "Y6"
    const val READ_TIME_7_SECONDS = "Y7"
    const val READ_TIME_8_SECONDS = "Y8"
    const val READ_TIME_9_SECONDS = "Y9"
    const val READ_TIME_INDEFINITELY = "YM"

    const val POSITIVE_BARCODES = "V2"
    const val NEGATIVE_BARCODES = "V3"
    const val POSITIVE_AND_NEGATIVE_BARCODES = "V4"

    // Commands for configuring floodlight settings.
    const val ENABLE_FLOODLIGHT = "[D39"
    const val DISABLE_FLOODLIGHT = "[D3A"
    const val ALTERNATING_FLOODLIGHT = "[D3B"
    const val PREVENT_SPECULAR_REFLECTION = "[D3Q"

    // Commands for configuring aiming settings.
    const val AIMING_ENABLED = "[D3D"
    const val AIMING_DISABLED = "[D3E"

    // Commands for configuring trigger settings.
    const val TRIGGER_REPEAT_ENABLED = "/M"
    const val TRIGGER_REPEAT_DISABLED = "/K"

    const val ENABLE_TRIGGER = "S8"
    const val DISABLE_TRIGGER = "S7"

    const val DELETE_KEY_ENABLED = "]DELE"
    const val DELETE_KEY_DISABLED = "]DELD"

    const val READ_1_TIME_REDUNDANCY_0 = "X0"
    const val READ_2_TIMES_REDUNDANCY_1 = "X1"
    const val READ_3_TIMES_REDUNDANCY_2 = "X2"
    const val READ_4_TIMES_REDUNDANCY_3 = "X3"
    const val READ_5_TIMES_REDUNDANCY_4 = "BS"
    const val READ_6_TIMES_REDUNDANCY_5 = "BT"
    const val READ_7_TIMES_REDUNDANCY_6 = "BU"
    const val READ_8_TIMES_REDUNDANCY_7 = "BV"
    const val READ_9_TIMES_REDUNDANCY_8 = "BW"
}
