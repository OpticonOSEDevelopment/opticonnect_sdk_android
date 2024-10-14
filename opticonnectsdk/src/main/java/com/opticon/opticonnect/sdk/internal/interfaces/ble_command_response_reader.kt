package com.opticon.opticonnect.sdk.internal.interfaces

interface BleCommandResponseReader {
    suspend fun getCommandResponseStream(deviceId: String): kotlinx.coroutines.flow.Flow<String>
}