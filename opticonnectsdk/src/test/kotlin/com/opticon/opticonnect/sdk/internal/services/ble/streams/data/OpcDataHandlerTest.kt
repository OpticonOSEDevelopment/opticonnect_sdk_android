package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.constants.Symbologies
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ACK
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.DLE
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.DLE_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ETX_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.STX_V
import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
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
                assertEquals(1, barcode.sequenceNumber)
                assertTrue(barcode.timeOfScan.isNotBlank())
            }
        }
    }

    @Test
    fun parsesCapturedBarcodePacketSplitAcrossNotifications() {
        runBlocking {
            createHandler().use { handler ->
                val barcodeResult = async {
                    withTimeout(1000) {
                        handler.barcodeDataStream.first()
                    }
                }
                yield()

                hexToUBytes(CAPTURED_EAN_13_WITH_TIMESTAMP_PACKET)
                    .chunked(7)
                    .forEach { chunk -> handler.processData(chunk) }

                assertEquals("2620734570914\r", barcodeResult.await().data)
            }
        }
    }

    @Test
    fun emitsRepeatedBarcodePacketWithSequenceNumberForClientDeduplication() {
        runBlocking {
            createHandler().use { handler ->
                val barcodeResults = async {
                    withTimeout(1000) {
                        handler.barcodeDataStream.take(2).toList()
                    }
                }
                yield()

                handler.processData(hexToUBytes(CAPTURED_EAN_13_WITH_TIMESTAMP_PACKET))
                handler.processData(hexToUBytes(CAPTURED_EAN_13_WITH_TIMESTAMP_PACKET))

                val barcodes = barcodeResults.await()
                assertEquals(listOf("2620734570914\r", "2620734570914\r"), barcodes.map { it.data })
                assertEquals(listOf(1, 1), barcodes.map { it.sequenceNumber })
            }
        }
    }

    @Test
    fun emitsSameBarcodeDataWhenSequenceNumberDiffers() {
        runBlocking {
            createHandler().use { handler ->
                val barcodeResults = async {
                    withTimeout(1000) {
                        handler.barcodeDataStream.take(2).toList()
                    }
                }
                yield()

                handler.processData(buildBarcodeWithTimestampFrame(sequenceNumber = 1, data = "2620734570914\r"))
                handler.processData(buildBarcodeWithTimestampFrame(sequenceNumber = 2, data = "2620734570914\r"))

                val barcodes = barcodeResults.await()
                assertEquals(listOf("2620734570914\r", "2620734570914\r"), barcodes.map { it.data })
                assertEquals(listOf(1, 2), barcodes.map { it.sequenceNumber })
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

                val commandResponse = commandResponseResult.await()
                assertEquals(ACK.toString(), commandResponse.data)
                assertEquals(5, commandResponse.sequenceNumber)
            }
        }
    }

    @Test
    fun parsesCommandResponseWithStuffedDleInPayload() {
        runBlocking {
            createHandler().use { handler ->
                val commandResponseResult = async {
                    withTimeout(1000) {
                        handler.commandDataStream.first()
                    }
                }
                yield()

                handler.processData(
                    buildFrame(
                        type = 0x64.toUByte(),
                        header = uBytes(0x00, 0x0C, 0x00, 0x00),
                        data = uBytes(0x41, 0x10, 0x42)
                    )
                )

                val commandResponse = commandResponseResult.await()
                assertEquals("A${DLE}B", commandResponse.data)
                assertEquals(12, commandResponse.sequenceNumber)
            }
        }
    }

    @Test
    fun ignoresCommandResponseWithInvalidCrc() {
        runBlocking {
            createHandler().use { handler ->
                val commandResponseResult = async {
                    withTimeoutOrNull(500) {
                        handler.commandDataStream.first()
                    }
                }
                yield()

                val packetWithBadCrc = hexToUBytes(CAPTURED_COMMAND_ACK_PACKET).toMutableList()
                packetWithBadCrc[packetWithBadCrc.lastIndex] = 0x00.toUByte()
                handler.processData(packetWithBadCrc)

                assertEquals(null, commandResponseResult.await())
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

                val commandResponses = commandResponsesResult.await()
                assertEquals(
                    listOf("]EBLE[BCDJUW28D", "", ACK.toString()),
                    commandResponses.map { it.data }
                )
                assertEquals(
                    listOf(11, 11, 11),
                    commandResponses.map { it.sequenceNumber }
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

    private fun buildFrame(type: UByte, header: List<UByte>, data: List<UByte>): List<UByte> {
        val body = mutableListOf(DLE_V, STX_V, type)
        (header + data).forEach { byte ->
            if (byte == DLE_V) {
                body.add(DLE_V)
            }
            body.add(byte)
        }
        body.add(DLE_V)
        body.add(ETX_V)

        val crc = CRC16Handler().compute(body)
        body.add((crc shr 8).toUByte())
        body.add((crc and 0xFF).toUByte())
        return body
    }

    private fun buildBarcodeWithTimestampFrame(sequenceNumber: Int, data: String): List<UByte> {
        val header = listOf(
            (sequenceNumber shr 8) and 0xFF,
            sequenceNumber and 0xFF,
            0x01,
            0x00,
            0x01,
            0xA0,
            0x65,
            0x11,
            0x9A,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00
        )

        return buildFrame(
            type = 0xA2.toUByte(),
            header = header.map { it.toUByte() },
            data = data.toByteArray(Charsets.UTF_8).map { it.toUByte() }
        )
    }

    private fun uBytes(vararg bytes: Int): List<UByte> =
        bytes.map { it.toUByte() }

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
