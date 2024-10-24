package com.opticon.opticonnect.sdk.internal.services.ble.interfaces

import kotlinx.coroutines.flow.Flow

internal interface BleCommandResponseReader {
    suspend fun getCommandResponseStream(deviceId: String): Flow<String>
}