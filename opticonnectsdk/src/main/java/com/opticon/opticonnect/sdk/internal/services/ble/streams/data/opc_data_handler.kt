package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants.*
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject
import kotlin.math.pow

enum class OpcRxState {
    Idle, ReceivingType, ReceivingData, ReceivingCrcHigh, ReceivingCrcLow
}

class OpcDataHandler @Inject constructor(
    private val deviceId: String,
    private val crc16Handler: CRC16Handler, // Injecting CRC16Handler
    private val symbologyHandler: SymbologyHandler // Injecting SymbologyHandler
) : Closeable {

    private val mutex = Mutex()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _commandDataStream = MutableSharedFlow<String>(replay = 0)
    private val _barcodeDataStream = MutableSharedFlow<BarcodeData>(replay = 0)

    // Expose flows as immutable SharedFlow
    val commandDataStream: SharedFlow<String> get() = _commandDataStream
    val barcodeDataStream: SharedFlow<BarcodeData> get() = _barcodeDataStream

    private val BARCODE_TYPE = 0x82
    private val BARCODE_WITH_TIME_TYPE = 0xA2
    private val MENU_COMMAND_RSP_TYPE = 0x64

    private val CODE_ID_BYTE_INDEX = 2
    private val QUANTITY_HIGH_BYTE_INDEX = 3
    private val QUANTITY_LOW_BYTE_INDEX = 4
    private val TIME_STAMP_BYTE_INDEX = 5

    private var state = OpcRxState.Idle
    private var opcDleFlag = false
    private var opcCrc = 0
    private var opcRxCrc = 0
    private var type = 0
    private var headerLen = 0
    private var dataOffset = 0
    private val headerBytes = mutableListOf<Int>()
    private val dataBytes = mutableListOf<Int>()

    private var previousSeqNr = -1

    private val OPC_DLE_STX_CRC = 0x4e72

    suspend fun processData(data: List<UByte>, shouldReturnResult: Boolean = false): String {
        var result = ""
        mutex.withLock {
            try {
                for (byte in data) {
                    when (state) {
                        OpcRxState.Idle -> {
                            if (byte == DLE_V) {
                                opcDleFlag = !opcDleFlag
                                continue
                            }

                            if (opcDleFlag) {
                                opcDleFlag = false
                                if (byte == STX_V) {
                                    state = OpcRxState.ReceivingType
                                    opcCrc = OPC_DLE_STX_CRC
                                }
                            }
                        }

                        OpcRxState.ReceivingType -> {
                            if (byte == DLE_V) {
                                opcDleFlag = true
                                state = OpcRxState.Idle
                                continue
                            }
                            type = byte.toInt()
                            headerLen = if ((type shr 5) != 0) (2.0.pow((type shr 5) - 1)).toInt() else 0
                            state = OpcRxState.ReceivingData
                            dataOffset = 0
                            headerBytes.clear()
                            dataBytes.clear()
                            opcCrc = crc16Handler.update(byte, opcCrc)
                        }

                        OpcRxState.ReceivingData -> {
                            opcCrc = crc16Handler.update(byte, opcCrc)
                            if (byte == DLE_V) {
                                if (!opcDleFlag) {
                                    opcDleFlag = true
                                    continue
                                }
                                opcDleFlag = false
                            } else {
                                if (opcDleFlag) {
                                    opcDleFlag = false
                                    when (byte) {
                                        STX_V -> {
                                            state = OpcRxState.ReceivingType
                                            opcCrc = OPC_DLE_STX_CRC
                                        }
                                        ETX_V -> state = OpcRxState.ReceivingCrcHigh
                                        else -> state = OpcRxState.Idle
                                    }
                                    continue
                                }
                            }

                            if (dataOffset < headerLen) {
                                headerBytes.add(byte.toInt())
                            } else {
                                dataBytes.add(byte.toInt())
                            }
                            dataOffset++
                        }

                        OpcRxState.ReceivingCrcHigh -> {
                            opcRxCrc = byte.toInt() shl 8
                            state = OpcRxState.ReceivingCrcLow
                        }

                        OpcRxState.ReceivingCrcLow -> {
                            opcRxCrc = opcRxCrc or byte.toInt()
                            state = OpcRxState.Idle
                            if (opcRxCrc != opcCrc && opcRxCrc != 0) {
                                opcDleFlag = (byte == DLE_V)
                            } else {
                                val dataBytesArray = dataBytes.map { it.toByte() }.toByteArray()
                                val dataString = String(dataBytesArray, Charsets.UTF_8)
                                Timber.d("Barcode Data Processed: $dataString")
                                if (shouldReturnResult) {
                                    result = dataString
                                    return result
                                } else if (type == BARCODE_TYPE || type == BARCODE_WITH_TIME_TYPE) {
                                    try {
                                        val sequenceNumber = extractSequenceNumber(headerBytes)
                                        if (sequenceNumber == previousSeqNr) {
                                            return result
                                        }
                                        previousSeqNr = sequenceNumber
                                    } catch (e: Exception) {
                                        Timber.e(e, "Error extracting sequence number")
                                    }
                                    postProcessAndSendBarcodeData(dataString, dataBytes)
                                } else if (type == MENU_COMMAND_RSP_TYPE) {
                                    _commandDataStream.emit(dataString)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error processing data")
            }
        }
        return result
    }

    private fun extractSequenceNumber(headerBytes: List<Int>): Int {
        if (headerBytes.size < 2) {
            val errorMsg = "Header too short to contain a sequence number"
            Timber.e(errorMsg)
            throw IllegalArgumentException(errorMsg)
        }
        return (headerBytes[0] shl 8) or headerBytes[1]
    }

    private fun postProcessAndSendBarcodeData(data: String, dataBytes: List<Int>) {
        var quantity = 1
        var symbologyId = 0
        var symbology = ""
        var timeOfScanMillis = System.currentTimeMillis()

        try {
            quantity = parseHexBytesToInteger(
                headerBytes[QUANTITY_HIGH_BYTE_INDEX],
                headerBytes[QUANTITY_LOW_BYTE_INDEX]
            )
        } catch (e: Exception) {
            Timber.e(e, "Error parsing quantity")
        }

        if (headerBytes.size > 8) {
            try {
                timeOfScanMillis = timeStampToMillis(headerBytes, TIME_STAMP_BYTE_INDEX)
            } catch (e: Exception) {
                Timber.e(e, "Error parsing timestamp")
            }
        }

        try {
            symbologyId = getSymbologyId(headerBytes[CODE_ID_BYTE_INDEX])
            symbology = symbologyHandler.getSymbologyNameById(symbologyId)
        } catch (e: Exception) {
            Timber.e(e, "Error parsing symbology id")
        }

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val timeOfScan = sdf.format(java.util.Date(timeOfScanMillis))

        val barcodeData = BarcodeData(
            data = data,
            dataBytes = dataBytes.map { it.toByte() }.toByteArray(),
            quantity = quantity,
            symbologyId = symbologyId,
            symbology = symbology,
            timeOfScan = timeOfScan,
            deviceId = deviceId
        )

        scope.launch {
            _barcodeDataStream.emit(barcodeData)
        }
    }

    private fun parseHexBytesToInteger(hexMostSignificant: Int, hexLeastSignificant: Int): Int {
        return try {
            val result = (hexMostSignificant shl 8) or hexLeastSignificant
            if ((result and 0x8000) != 0) {
                -((result xor 0xFFFF) + 1)
            } else {
                result
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing hex bytes to integer")
            throw e
        }
    }

    private fun timeStampToMillis(timestamp: List<Int>, offset: Int): Long {
        return try {
            var data = 0
            data = (data or (timestamp[offset] shl 24))
            data = (data or (timestamp[offset + 1] shl 16))
            data = (data or (timestamp[offset + 2] shl 8))
            data = (data or timestamp[offset + 3])

            val year = 2000 + (data and 0x3F)
            val month = (data shr 6) and 0x0F
            val day = (data shr 10) and 0x1F
            val hour = (data shr 15) and 0x1F
            val minute = (data shr 20) and 0x3F
            val second = (data shr 26) and 0x3F

            val calendar = java.util.Calendar.getInstance()
            calendar.set(year, month - 1, day, hour, minute, second)
            calendar.timeInMillis
        } catch (e: Exception) {
            Timber.e(e, "Error converting timestamp to milliseconds")
            throw e
        }
    }

    private fun getSymbologyId(codeId: Int): Int {
        return try {
            symbologyHandler.getSymbologyIdByCodeId(codeId)
        } catch (e: Exception) {
            Timber.e(e, "Error getting symbology id")
            throw e
        }
    }

    override fun close() {
        job.cancel()
    }
}
