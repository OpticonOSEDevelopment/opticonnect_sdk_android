package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.CRC16Handler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.DLE_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.STX_V
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.ETX_V
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpcCommandProtocolHandler @Inject constructor(
    private val crc16Handler: CRC16Handler // Injecting CRC16Handler for CRC computation
) : CommandBytesProvider {

    private var seqNr = 0

    private fun incrementSeqNr() {
        // Increment and wrap sequence number within 16-bit limit
        seqNr = (seqNr + 1) % (1 shl 16)
    }

    override fun getCommandBytes(command: Command): ByteArray {
        return try {
            val menuCommandType: UByte = 0x43u // 0x43: Menu command with sequence number
            val commandData = command.data
            val commandBytes = mutableListOf<UByte>()

            // Add start bytes
            addCommandStartBytes(commandBytes, menuCommandType, seqNr)

            // Add command data
            for (char in commandData) {
                addByteWithEscape(commandBytes, char.code.toUByte())
            }

            // Add end bytes
            addCommandEndBytes(commandBytes)

            // Finalize and return the command bytes
            val bytes = finalizeCommandBytes(commandBytes)
            return bytes.map { it.toByte() }.toByteArray()
        } catch (e: Exception) {
            Timber.e(e, "Error generating command bytes: ${e.message}")
            throw e
        }
    }

    private fun finalizeCommandBytes(commandBytes: MutableList<UByte>): List<UByte> {
        incrementSeqNr()
        return commandBytes
    }

    private fun intToTwoByteList(value: Int): List<UByte> {
        val highByte = (value shr 8).toUByte()
        val lowByte = (value and 0xFF).toUByte()
        return listOf(highByte, lowByte)
    }

    private fun addCommandStartBytes(commandBytes: MutableList<UByte>, type: UByte, seqNr: Int) {
        commandBytes.add(DLE_V.toUByte())
        commandBytes.add(STX_V.toUByte())
        commandBytes.add(type)

        // Add sequence number bytes with escape handling
        val seqNrBytes = intToTwoByteList(seqNr)
        seqNrBytes.forEach { addByteWithEscape(commandBytes, it) }
    }

    private fun addCommandEndBytes(commandBytes: MutableList<UByte>) {
        commandBytes.add(DLE_V.toUByte())
        commandBytes.add(ETX_V.toUByte())

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
        if (byte == DLE_V.toUByte()) {
            commandBytes.add(byte) // Escape DLE byte
        }
        commandBytes.add(byte)
    }
}
