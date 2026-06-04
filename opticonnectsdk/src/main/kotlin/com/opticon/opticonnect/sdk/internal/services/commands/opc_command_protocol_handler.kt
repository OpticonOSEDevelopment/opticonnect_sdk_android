package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.CommandPacket
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.CRC16Handler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.DLE_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.STX_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ETX_V
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OpcCommandProtocolHandler @Inject constructor(
    private val crc16Handler: CRC16Handler // Injecting CRC16Handler for CRC computation
) : CommandBytesProvider {
    private var seqNr = 0

    private fun nextSeqNr(): Int {
        val current = seqNr
        seqNr = (seqNr + 1) % (1 shl 16)
        return current
    }

    override fun getCommandPacket(command: Command): CommandPacket {
        return try {
            val menuCommandType: UByte = 0x43u // 0x43: Menu command with sequence number
            val currentSeqNr = nextSeqNr()
            val commandData = command.data
            val commandBytes = mutableListOf<UByte>()

            // Add start bytes
            addCommandStartBytes(commandBytes, menuCommandType, currentSeqNr)

            // Add command data
            for (char in commandData) {
                addByteWithEscape(commandBytes, char.code.toUByte())
            }

            // Add end bytes
            addCommandEndBytes(commandBytes)

            val bytes = commandBytes.map { it.toByte() }.toByteArray()
            CommandPacket(bytes, currentSeqNr)
        } catch (e: Exception) {
            Timber.e(e, "Error generating command bytes: ${e.message}")
            throw e
        }
    }

    private fun intToTwoByteList(value: Int): List<UByte> {
        val highByte = (value shr 8).toUByte()
        val lowByte = (value and 0xFF).toUByte()
        return listOf(highByte, lowByte)
    }

    private fun addCommandStartBytes(commandBytes: MutableList<UByte>, type: UByte, seqNr: Int) {
        commandBytes.add(DLE_V)
        commandBytes.add(STX_V)
        commandBytes.add(type)

        // Add sequence number bytes with escape handling
        val seqNrBytes = intToTwoByteList(seqNr)
        seqNrBytes.forEach { addByteWithEscape(commandBytes, it) }
    }

    private fun addCommandEndBytes(commandBytes: MutableList<UByte>) {
        commandBytes.add(DLE_V)
        commandBytes.add(ETX_V)

        // Calculate and append CRC
        val crc = crc16Handler.compute(commandBytes)
        val crcBytes = intToTwoUByteList(crc)
        commandBytes.addAll(crcBytes)
    }

    private fun intToTwoUByteList(value: Int): List<UByte> {
        val highByte = (value shr 8).toUByte()
        val lowByte = (value and 0xFF).toUByte()
        return listOf(highByte, lowByte)
    }

    private fun addByteWithEscape(commandBytes: MutableList<UByte>, byte: UByte) {
        if (byte == DLE_V) {
            commandBytes.add(byte) // Escape DLE byte
        }
        commandBytes.add(byte)
    }
}
