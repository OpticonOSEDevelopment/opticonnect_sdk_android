package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.internal.services.ble.streams.BleDevicesStreamsHandler
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDeviceServices
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
    private val bleDevicesStreamsHandler: BleDevicesStreamsHandler,
    private val opcDataHandlerFactory: OpcDataHandlerFactory
) : BleDataWriter, BleCommandResponseReader, Closeable {

    private val dataProcessors = mutableMapOf<String, DataProcessor>()
    private val mutex = Mutex()

    override suspend fun getCommandResponseStream(deviceId: String): Flow<String> {
        return bleDevicesStreamsHandler.getOrCreateCommandStream(deviceId)
    }

    override suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray) {
        try {
            val dataProcessor = getDataProcessor(deviceId)
            dataProcessor.writeData(dataBytes)
        } catch (e: Exception) {
            Timber.e(e, "Failed to write data. The device may not be connected anymore.")
        }
    }

    private fun getDataProcessor(deviceId: String): DataProcessor {
        return dataProcessors[deviceId] ?: throw Exception("Data processor not found for device: $deviceId")
    }

    suspend fun addDataProcessor(deviceId: String, connection: RxBleConnection): DataProcessor {
        return mutex.withLock {
            val dataProcessor = DataProcessor(
                deviceId = deviceId,
                readCharacteristic = UuidConstants.OPC_READ_CHARACTERISTIC_UUID,
                writeCharacteristic = UuidConstants.OPC_WRITE_CHARACTERISTIC_UUID,
                opcDataHandler = opcDataHandlerFactory.create(deviceId),
                connection = connection,
                bleDeviceStreamManager = bleDevicesStreamsHandler
            )

            dataProcessors[deviceId] = dataProcessor

            dataProcessor.initializeStreams()

            return dataProcessor
        }
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
