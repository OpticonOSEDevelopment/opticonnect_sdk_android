package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CRC16HandlerTest {

    @Test
    fun computesMenuCommandFrameCrc() {
        assertComputedCrc(
            packetWithoutCrc = "10 02 43 00 00 5A 31 10 03",
            expectedCrc = 0x04C5
        )
    }

    @Test
    fun computesCapturedCommandAckCrc() {
        assertComputedCrc(
            packetWithoutCrc = "10 02 64 00 05 00 00 06 10 03",
            expectedCrc = 0x433D
        )
    }

    @Test
    fun computesCapturedBarcodeWithTimestampCrc() {
        assertComputedCrc(
            packetWithoutCrc = """
                10 02 A2 00 01 01 00 01 A0 65 11 9A 00 00 00 00
                00 00 00 32 36 32 30 37 33 34 35 37 30 39 31 34
                0D 10 03
            """.trimIndent(),
            expectedCrc = 0x41A8
        )
    }

    private fun assertComputedCrc(packetWithoutCrc: String, expectedCrc: Int) {
        val computedCrc = CRC16Handler().compute(hexToUBytes(packetWithoutCrc))

        assertEquals(expectedCrc, computedCrc)
    }

    private fun hexToUBytes(hex: String): List<UByte> =
        hex.replace(Regex("[^0-9A-Fa-f]"), "")
            .chunked(2)
            .map { it.toInt(16).toUByte() }
}
