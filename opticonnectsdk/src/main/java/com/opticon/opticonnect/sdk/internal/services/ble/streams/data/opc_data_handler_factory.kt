package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import javax.inject.Inject

internal class OpcDataHandlerFactory @Inject constructor(
    private val crc16Handler: CRC16Handler,
    private val symbologyHandler: SymbologyHandler
) {
    fun create(deviceId: String): OpcDataHandler {
        return OpcDataHandler(deviceId, crc16Handler, symbologyHandler)
    }
}
