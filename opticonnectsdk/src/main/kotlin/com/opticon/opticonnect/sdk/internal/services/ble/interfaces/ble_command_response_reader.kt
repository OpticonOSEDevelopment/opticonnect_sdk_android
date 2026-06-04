package com.opticon.opticonnect.sdk.internal.services.ble.interfaces

import com.opticon.opticonnect.sdk.internal.entities.CommandResponsePacket
import kotlinx.coroutines.flow.Flow

internal interface BleCommandResponseReader {
    suspend fun getCommandResponseStream(deviceId: String): Flow<CommandResponsePacket>
}
