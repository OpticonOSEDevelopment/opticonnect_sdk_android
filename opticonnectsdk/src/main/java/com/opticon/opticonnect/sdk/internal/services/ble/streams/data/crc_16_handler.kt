package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ccittrevTable
import org.koin.core.annotation.Single

@Single
class CRC16Handler {
    fun update(data: Int, crc: Int): Int {
        var chksum = crc
        chksum = chksum.inv() and 0xFFFF
        chksum = (chksum shr 8) xor ccittrevTable[(chksum and 0xFF) xor data]
        chksum = chksum.inv() and 0xFFFF
        return chksum
    }

    fun compute(pData: List<Int>, pCrc: Int? = 0xFFFF): Int {
        var chksum = pCrc ?: 0xFFFF
        for (i in pData.indices) {
            chksum = (chksum shr 8) xor ccittrevTable[(chksum and 0xFF) xor pData[i]]
        }
        return chksum.inv() and 0xFFFF
    }
}