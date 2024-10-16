package com.opticon.opticonnect.sdk.internal.services.ble

import com.polidea.rxandroidble3.RxBleClient
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import com.polidea.rxandroidble3.RxBleConnection
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleConnectivityHandler @Inject constructor(
    private val bleClient: RxBleClient,
    private val dataHandler: DataHandler,
) {

    private val compositeDisposable = CompositeDisposable()
    private val connectionMutexes = mutableMapOf<String, Mutex>()
    private val connectionStateSubscriptions = mutableMapOf<String, CompositeDisposable>()

    suspend fun connect(deviceId: String) {
        connectionMutexes.putIfAbsent(deviceId, Mutex())

        connectionMutexes[deviceId]?.withLock {
            val bleDevice = bleClient.getBleDevice(deviceId)

            // Disconnect if already connected
            if (bleDevice.connectionState == RxBleConnection.RxBleConnectionState.CONNECTED) {
                try {
                    bleDevice.establishConnection(false)
                        .timeout(10, TimeUnit.SECONDS)
                        .doFinally { Timber.d("Disconnected from device: $deviceId") }
                        .subscribe({}, { Timber.e("Failed to disconnect: $it") })
                        .addTo(compositeDisposable)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to disconnect from device: $deviceId")
                    throw e
                }
            }

            val maxRetries = 3
            var retryCount = 0

            while (true) {
                try {
                    // Notify connecting state
                    Timber.i("Connecting to device: $deviceId")
                    establishConnection(bleDevice)
                    listenToConnectionStateUpdates(bleDevice)
                    break
                } catch (e: Exception) {
                    retryCount++
                    Timber.w("Connection attempt failed ($retryCount/$maxRetries): $e")
                    if (retryCount >= maxRetries) {
                        Timber.e("Failed to connect after $retryCount retries.")
                        break
                    }
                    delayForRetry()
                }
            }
        }
    }

    private fun establishConnection(bleDevice: com.polidea.rxandroidble3.RxBleDevice) {
        bleDevice.establishConnection(false)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { connection ->
                    Timber.i("Connected to device: ${bleDevice.macAddress}")
                    CoroutineScope(Dispatchers.IO).launch {
                        initializeDevice(bleDevice.macAddress)
                    }
                },
                { error ->
                    Timber.e("Connection failed: ${error.localizedMessage}")
                }
            ).addTo(compositeDisposable)
    }

    private fun listenToConnectionStateUpdates(bleDevice: com.polidea.rxandroidble3.RxBleDevice) {
        val deviceId = bleDevice.macAddress

        connectionStateSubscriptions[deviceId]?.dispose()

        bleDevice.observeConnectionStateChanges()
            .subscribe { state ->
                when (state) {
                    RxBleConnection.RxBleConnectionState.CONNECTED -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (!initializeDevice(deviceId)) {
                                Timber.e("Failed to initialize device: $deviceId")
                            }
                        }
                    }
                    RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            processDisconnect(deviceId)
                        }
                    }
                    else -> Unit
                }
            }.addTo(CompositeDisposable().also {
                connectionStateSubscriptions[deviceId] = it
            })
    }

    private suspend fun initializeDevice(deviceId: String): Boolean {
        return try {
            val bleDevice = bleClient.getBleDevice(deviceId)

            // Establish connection
            val connection = bleDevice.establishConnection(false)
                .timeout(10, TimeUnit.SECONDS)
                .firstOrError()
                .await() // This should now properly work within coroutines

            // Discover services
            val services = connection.discoverServices().await()

            // If you have some service-specific logic, add it here

            // Directly pass the RxBleConnection to the DataHandler
            dataHandler.addDataProcessor(deviceId, connection)

            Timber.i("Successfully initialized services for device: $deviceId")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize device: $deviceId")
            disconnect(deviceId) // Handle disconnection on failure
            false
        }
    }

    fun disconnect(deviceId: String) {
        val bleDevice = bleClient.getBleDevice(deviceId)

        if (bleDevice.connectionState != RxBleConnection.RxBleConnectionState.DISCONNECTED) {
            bleDevice.establishConnection(false)
                .doOnDispose { Timber.i("Disconnected from device: $deviceId") }
                .subscribe({}, { Timber.e("Failed to disconnect: $it") })
                .addTo(compositeDisposable)
        }

        processDisconnect(deviceId)
    }

    private fun processDisconnect(deviceId: String) {
        dataHandler.closeForDevice(deviceId)
        connectionStateSubscriptions[deviceId]?.dispose()
        connectionStateSubscriptions.remove(deviceId)
    }

    private fun delayForRetry() {
        Thread.sleep(500)  // Retry delay of 500ms
    }

    fun dispose() {
        compositeDisposable.dispose()
        connectionStateSubscriptions.keys.toList().forEach { disconnect(it) }
        connectionStateSubscriptions.clear()
    }
}