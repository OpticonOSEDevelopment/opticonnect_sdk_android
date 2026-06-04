package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.CRC16Handler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.DLE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpcCommandProtocolHandlerTest {

    @Test
    fun buildsMenuCommandFrameWithSequenceAndValidCrc() {
        val packet = OpcCommandProtocolHandler(CRC16Handler())
            .getCommandPacket(Command("Z1"))
        val frame = packet.bytes.toUBytes()

        assertEquals(0, packet.sequenceNumber)
        assertEquals(
            uBytes(0x10, 0x02, 0x43, 0x00, 0x00, 0x5A, 0x31, 0x10, 0x03),
            frame.dropLast(2)
        )
        assertFrameHasValidCrc(frame)
    }

    @Test
    fun incrementsSequenceNumberBetweenCommands() {
        val handler = OpcCommandProtocolHandler(CRC16Handler())

        val firstPacket = handler.getCommandPacket(Command("Z1"))
        val secondPacket = handler.getCommandPacket(Command("Z1"))
        val firstFrame = firstPacket.bytes.toUBytes()
        val secondFrame = secondPacket.bytes.toUBytes()

        assertEquals(0, firstPacket.sequenceNumber)
        assertEquals(1, secondPacket.sequenceNumber)
        assertEquals(uBytes(0x00, 0x00), firstFrame.slice(3..4))
        assertEquals(uBytes(0x00, 0x01), secondFrame.slice(3..4))
        assertFrameHasValidCrc(firstFrame)
        assertFrameHasValidCrc(secondFrame)
    }

    @Test
    fun byteStuffsDleBytesInCommandData() {
        val frame = OpcCommandProtocolHandler(CRC16Handler())
            .getCommandPacket(Command("A${DLE}B"))
            .bytes
            .toUBytes()

        assertTrue(frame.windowed(4).contains(uBytes(0x41, 0x10, 0x10, 0x42)))
        assertFrameHasValidCrc(frame)
    }

    private fun ByteArray.toUBytes(): List<UByte> =
        map { it.toUByte() }

    private fun assertFrameHasValidCrc(frame: List<UByte>) {
        val expectedCrc = CRC16Handler().compute(frame.dropLast(2))
        val actualCrc = (frame[frame.lastIndex - 1].toInt() shl 8) or frame.last().toInt()

        assertEquals(expectedCrc, actualCrc)
    }

    private fun uBytes(vararg bytes: Int): List<UByte> =
        bytes.map { it.toUByte() }
}
