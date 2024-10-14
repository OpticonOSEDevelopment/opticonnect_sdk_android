package com.opticon.opticonnect.sdk.internal.services.ble.constants

import java.util.UUID

object UuidConstants {
    val SCANNER_SERVICE_UUID: UUID = UUID.fromString("46409be5-6967-4557-8e70-784e1e55263b")
    val BATTERY_SERVICE_UUID: UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    val BATTERY_LEVEL_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    val BATTERY_LEVEL_STATUS_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002bed-0000-1000-8000-00805f9b34fb")
    val OPC_SERVICE_UUID: UUID = UUID.fromString("6e400000-b5a3-f393-e0a9-e50e24dcca9e")
}