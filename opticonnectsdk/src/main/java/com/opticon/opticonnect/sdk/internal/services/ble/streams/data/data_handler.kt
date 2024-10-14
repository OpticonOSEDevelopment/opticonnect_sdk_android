package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants
import com.opticon.opticonnect.sdk.public.entities.BarcodeData
import com.polidea.rxandroidble3.RxBleConnection
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Single
import timber.log.Timber
import java.io.Closeable

@Single
class DataHandler : BleDataWriter, BleCommandResponseReader, Closeable {

    private val dataProcessors = mutableMapOf<String, DataProcessor>()
    private val mutex = Mutex()

    suspend fun getStream(deviceId: String): kotlinx.coroutines.flow.Flow<BarcodeData> {
        val dataProcessor = getDataProcessor(deviceId)
        return dataProcessor.barcodeDataStream
    }

    override suspend fun getCommandResponseStream(deviceId: String): kotlinx.coroutines.flow.Flow<String> {
        val dataProcessor = getDataProcessor(deviceId)
        return dataProcessor.commandStream
    }

    override suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray) {
        try {
            val dataProcessor = getDataProcessor(deviceId)
            dataProcessor.writeData(dataBytes)
        } catch (e: Exception) {
            Timber.e(e, "Failed to write data")
        }
    }

    private suspend fun getDataProcessor(deviceId: String): DataProcessor {
        return mutex.withLock {
            dataProcessors[deviceId] ?: throw Exception("Data processor not found for device: $deviceId")
        }
    }

    suspend fun addDataProcessor(deviceId: String, connection: RxBleConnection): DataProcessor {
        return mutex.withLock {
            val dataProcessor = DataProcessor(
                deviceId = deviceId,
                readCharacteristic = UuidConstants.OPC_SERVICE_UUID,
                writeCharacteristic = UuidConstants.OPC_SERVICE_UUID,
                opcDataHandler = OpcDataHandler(deviceId),
                connection = connection,
            )

            dataProcessors[deviceId] = dataProcessor

            return dataProcessor
        }
    }

    fun removeDataProcessor(deviceId: String) {
        dataProcessors.remove(deviceId)
    }

    override fun close() {
        // Close all processors when closing DataHandler
        dataProcessors.values.forEach { it.close() }
        dataProcessors.clear()
        Timber.d("All data processors closed and cleared.")
    }

    fun closeForDevice(deviceId: String) {
        dataProcessors[deviceId]?.close() // Close the processor for the specific device
        dataProcessors.remove(deviceId)
        Timber.d("Closed and removed data processor for device: $deviceId")
    }
}
