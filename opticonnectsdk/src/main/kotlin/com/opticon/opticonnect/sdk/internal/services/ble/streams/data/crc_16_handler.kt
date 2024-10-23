package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ccittrevTable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CRC16Handler @Inject constructor() {
    fun update(data: UByte, crc: Int): Int {
        var checksum = crc
        checksum = checksum.inv() and 0xFFFF
        checksum = (checksum shr 8) xor ccittrevTable[(checksum and 0xFF) xor data.toInt()]
        checksum = checksum.inv() and 0xFFFF
        return checksum
    }

    fun compute(pData: List<UByte>, pCrc: Int? = 0xFFFF): Int {
        var checksum = pCrc ?: 0xFFFF
        for (data in pData) {
            checksum = (checksum shr 8) xor ccittrevTable[(checksum and 0xFF) xor data.toInt()]
        }
        return checksum.inv() and 0xFFFF
    }
}
