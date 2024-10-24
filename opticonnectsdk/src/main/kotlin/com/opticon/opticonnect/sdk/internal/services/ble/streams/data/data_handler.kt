package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.polidea.rxandroidble3.RxBleConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.rx3.await

@Singleton
internal class DataHandler @Inject constructor(
    private val opcDataHandlerFactory: OpcDataHandlerFactory // Injecting OpcDataHandlerFactory
) : BleDataWriter, BleCommandResponseReader, Closeable {

    private val dataProcessors = mutableMapOf<String, DataProcessor>()
    private val mutex = Mutex()

    fun getBarcodeDataStream(deviceId: String): Flow<BarcodeData> {
        val dataProcessor = getDataProcessor(deviceId)
        return dataProcessor.barcodeDataStream
    }

    override suspend fun getCommandResponseStream(deviceId: String): Flow<String> {
        val dataProcessor = getDataProcessor(deviceId)
        return dataProcessor.commandStream
    }

    override suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray) {
        try {
            Timber.d("Writing data to device: $deviceId")
            val dataProcessor = getDataProcessor(deviceId)
            dataProcessor.writeData(dataBytes)
        } catch (e: Exception) {
            Timber.e(e, "Failed to write data")
        }
    }

    private fun getDataProcessor(deviceId: String): DataProcessor {
        return dataProcessors[deviceId] ?: throw Exception("Data processor not found for device: $deviceId")
    }

    suspend fun addDataProcessor(deviceId: String, connection: RxBleConnection): DataProcessor {
        return mutex.withLock {
            val services = connection.discoverServices().await()

            val readCharacteristic = services.getCharacteristic(UuidConstants.OPC_READ_CHARACTERISTIC_UUID)?.await()
            val writeCharacteristic = services.getCharacteristic(UuidConstants.OPC_WRITE_CHARACTERISTIC_UUID)?.await()

            if (readCharacteristic == null || writeCharacteristic == null) {
                Timber.e("Required OPC characteristics not found for device: $deviceId")
                throw Exception("Required OPC characteristics not found")
            }

            val dataProcessor = DataProcessor(
                deviceId = deviceId,
                readCharacteristic = readCharacteristic.uuid,
                writeCharacteristic = writeCharacteristic.uuid,
                opcDataHandler = opcDataHandlerFactory.create(deviceId),
                connection = connection
            )

            dataProcessors[deviceId] = dataProcessor

            dataProcessor.initializeStreams()

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

    fun close(deviceId: String) {
        dataProcessors[deviceId]?.close() // Close the processor for the specific device
        dataProcessors.remove(deviceId)
        Timber.d("Closed and removed data processor for device: $deviceId")
    }
}
