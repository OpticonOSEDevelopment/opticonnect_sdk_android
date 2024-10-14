package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import com.opticon.opticonnect.sdk.public.entities.BarcodeData
import com.polidea.rxandroidble3.RxBleConnection
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import java.io.Closeable

class DataProcessor(
    private val deviceId: String,
    private val readCharacteristic: UUID,
    private val writeCharacteristic: UUID,
    private val opcDataHandler: OpcDataHandler,
    private val connection: RxBleConnection
) : Closeable {
    private val compositeDisposable = CompositeDisposable()

    // Coroutine job and scope for managing the streams
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    // SharedFlows for broadcasting data to multiple subscribers
    private val _commandStream = MutableSharedFlow<String>(replay = 0) // Command stream
    private val _barcodeDataStream = MutableSharedFlow<BarcodeData>(replay = 0) // Barcode data stream

    // Exposing the SharedFlows as immutable flows
    val commandStream: SharedFlow<String> get() = _commandStream
    val barcodeDataStream: SharedFlow<BarcodeData> get() = _barcodeDataStream

    init {
        initializeStreams()
    }

    fun writeData(data: ByteArray) {
        connection.writeCharacteristic(writeCharacteristic, data)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    Timber.d("Data written successfully: ${data.joinToString(",")}")
                },
                { error ->
                    Timber.e(error, "Failed to write data")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun initializeStreams() {
        connection.setupNotification(readCharacteristic)
            .flatMap { it }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { data ->
                    scope.launch {
                        try {
                            // Process received BLE data bytes
                            opcDataHandler.processData(data.map { it.toInt() })
                            Timber.d("Data received and processed: ${data.joinToString(",")} for device: $deviceId")
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing data for device: $deviceId")
                        }
                    }
                },
                { error ->
                    Timber.e(error, "Error setting up notification for readCharacteristic: $readCharacteristic on device: $deviceId")
                }
            ).addTo(compositeDisposable)

        // Forward data from OpcDataHandler's command and barcode streams to DataProcessor's own streams
        scope.launch {
            try {
                opcDataHandler.commandDataStream.collect { command ->
                    _commandStream.emit(command) // Emit the command data into the DataProcessor's command stream
                    Timber.d("Command received: $command for device: $deviceId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error collecting command data stream for device: $deviceId")
            }
        }

        scope.launch {
            try {
                opcDataHandler.barcodeDataStream.collect { barcodeData ->
                    _barcodeDataStream.emit(barcodeData) // Emit the barcode data into the DataProcessor's barcode stream
                    Timber.d("Barcode data received: $barcodeData for device: $deviceId")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error collecting barcode data stream for device: $deviceId")
            }
        }
    }

    override fun close() {
        compositeDisposable.clear()
        job.cancel()
        Timber.d("Closed resources for device: $deviceId")
    }
}
