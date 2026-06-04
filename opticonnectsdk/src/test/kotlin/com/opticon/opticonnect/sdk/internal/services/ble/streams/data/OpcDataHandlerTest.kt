package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.constants.Symbologies
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ACK
import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpcDataHandlerTest {

    @Test
    fun parsesCapturedBarcodeWithTimestampPacket() {
        runBlocking {
            createHandler().use { handler ->
                val barcodeResult = async {
                    withTimeout(1000) {
                        handler.barcodeDataStream.first()
                    }
                }
                yield()

                handler.processData(hexToUBytes(CAPTURED_EAN_13_WITH_TIMESTAMP_PACKET))

                val barcode = barcodeResult.await()
                val expectedData = "2620734570914\r"
                assertEquals(expectedData, barcode.data)
                assertArrayEquals(expectedData.toByteArray(Charsets.UTF_8), barcode.dataBytes)
                assertEquals(1, barcode.quantity)
                assertEquals(Symbologies.EAN_13, barcode.symbologyId)
                assertEquals("EAN-13", barcode.symbology)
                assertEquals(TEST_DEVICE_ID, barcode.deviceId)
                assertTrue(barcode.timeOfScan.isNotBlank())
            }
        }
    }

    @Test
    fun parsesCapturedCommandAckPacket() {
        runBlocking {
            createHandler().use { handler ->
                val commandResponseResult = async {
                    withTimeout(1000) {
                        handler.commandDataStream.first()
                    }
                }
                yield()

                handler.processData(hexToUBytes(CAPTURED_COMMAND_ACK_PACKET))

                assertEquals(ACK.toString(), commandResponseResult.await())
            }
        }
    }

    @Test
    fun parsesCapturedGetSettingsResponsePackets() {
        runBlocking {
            createHandler().use { handler ->
                val commandResponsesResult = async {
                    withTimeout(1000) {
                        handler.commandDataStream.take(3).toList()
                    }
                }
                yield()

                CAPTURED_GET_SETTINGS_RESPONSE_PACKETS.forEach { packet ->
                    handler.processData(hexToUBytes(packet))
                }

                assertEquals(
                    listOf("]EBLE[BCDJUW28D", "", ACK.toString()),
                    commandResponsesResult.await()
                )
            }
        }
    }

    private fun createHandler(): OpcDataHandler =
        OpcDataHandler(
            deviceId = TEST_DEVICE_ID,
            crc16Handler = CRC16Handler(),
            symbologyHandler = SymbologyHandler()
        )

    private fun hexToUBytes(hex: String): List<UByte> =
        hex.replace(Regex("[^0-9A-Fa-f]"), "")
            .chunked(2)
            .map { it.toInt(16).toUByte() }

    private companion object {
        const val TEST_DEVICE_ID = "38:89:DC:0E:00:0F"

        val CAPTURED_EAN_13_WITH_TIMESTAMP_PACKET = """
            10 02 A2 00 01 01 00 01 A0 65 11 9A 00 00 00 00
            00 00 00 32 36 32 30 37 33 34 35 37 30 39 31 34
            0D 10 03 41 A8
        """.trimIndent()

        val CAPTURED_COMMAND_ACK_PACKET = """
            10 02 64 00 05 00 00 06 10 03 43 3D
        """.trimIndent()

        val CAPTURED_GET_SETTINGS_RESPONSE_PACKETS = listOf(
            """
                10 02 64 00 0B 00 00 5D 45 42 4C 45 5B 42 43
                44 4A 55 57 32 38 44 10 03 7D 4F
            """.trimIndent(),
            """
                10 02 64 00 0B 01 00 10 03 2C 75
            """.trimIndent(),
            """
                10 02 64 00 0B 02 00 06 10 03 AD 45
            """.trimIndent()
        )
    }
}
