package com.opticon.opticonnect.sdk.internal.services.ble.interfaces

internal interface BleDataWriter {
    suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray)
}