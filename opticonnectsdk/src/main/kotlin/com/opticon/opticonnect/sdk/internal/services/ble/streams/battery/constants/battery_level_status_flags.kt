package com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.constants

internal object BatteryLevelStatusFlags {
    const val BLE_BAS_BATTERY_LEVEL_FLAG = 0x02

    const val BLE_BAS_BATTERY_PRESENT_FLAG = 0x0001
    const val BLE_BAS_WIRELESS_CHARGING_FLAG = 0x0004
    const val BLE_BAS_WIRED_CHARGING_FLAG = 0x0010
    const val BLE_BAS_IS_CHARGING_FLAG = 0x0020
    const val BLE_BAS_CONSTANT_CURRENT_FLAG = 0x0200
    const val BLE_BAS_CONSTANT_VOLTAGE_FLAG = 0x0400
    const val BLE_BAS_TRICKLE_CHARGING_FLAG = 0x0600
    const val BLE_BAS_CHARGING_MASK = 0x0600
    const val BLE_BAS_BATTERY_FAULT_FLAG = 0x1000
}
