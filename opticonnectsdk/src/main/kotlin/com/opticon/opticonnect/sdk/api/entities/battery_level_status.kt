package com.opticon.opticonnect.sdk.api.entities

data class BatteryLevelStatus(
    val isBatteryPresent: Boolean,
    val isWirelessCharging: Boolean,
    val isWiredCharging: Boolean,
    val isCharging: Boolean,
    val isBatteryFaulty: Boolean,
    val percentage: Int // -1 if not available
)
