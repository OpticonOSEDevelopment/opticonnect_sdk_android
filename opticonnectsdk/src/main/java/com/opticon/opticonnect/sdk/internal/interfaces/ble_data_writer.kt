package com.opticon.opticonnect.sdk.internal.interfaces

interface BleDataWriter {
    suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray)
}