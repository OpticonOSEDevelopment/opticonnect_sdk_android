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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.BatteryHandler
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import com.opticon.opticonnect.sdk.internal.services.core.DevicesInfoManager
import com.polidea.rxandroidble3.RxBleDevice
import kotlinx.coroutines.flow.MutableSharedFlow
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.Closeable

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BleConnectivityHandler @Inject constructor(
    private val bleClient: RxBleClient,
    private val dataHandler: DataHandler,
    private val batteryHandler: BatteryHandler,
    private val commandExecutorsManager: CommandExecutorsManager,
    private val devicesInfoManager: DevicesInfoManager
) : Closeable {

    private val compositeDisposable = CompositeDisposable()
    private val connectionMutexes = mutableMapOf<String, Mutex>()
    private val connectionStateSubscriptions = mutableMapOf<String, CompositeDisposable>()
    private val connectionStateFlows = mutableMapOf<String, MutableSharedFlow<BleDeviceConnectionState>>()
    private val connectionDisposables = mutableMapOf<String, Disposable>()
    
    private val scope = CoroutineScope(Dispatchers.IO)

    fun getConnectionStateFlow(deviceId: String): MutableSharedFlow<BleDeviceConnectionState> {
        return connectionStateFlows.getOrPut(deviceId) {
            MutableSharedFlow(replay = 1)
        }
    }

    suspend fun connect(deviceId: String) {
        connectionMutexes.putIfAbsent(deviceId, Mutex())

        connectionMutexes[deviceId]?.withLock {
            val bleDevice = bleClient.getBleDevice(deviceId)

            connectionStateFlows.putIfAbsent(deviceId, MutableSharedFlow(replay = 1))
            connectionStateFlows[deviceId]?.emit(BleDeviceConnectionState.CONNECTING)

            // Check and disconnect if already connected
            if (bleDevice.connectionState == RxBleConnection.RxBleConnectionState.CONNECTED) {
                Timber.d("Device $deviceId is already connected. Disconnecting first.")
                disconnect(deviceId)
            }

            val maxRetries = 3
            val retryDelayMillis = 500L
            val connectionTimeoutMillis = 5_000L
            var retryCount = 0

            while (true) {
                try {
                    Timber.d("Attempting to connect to device: $deviceId")
                    withTimeout(connectionTimeoutMillis) {
                        establishConnection(bleDevice)
                    }
                    break
                } catch (e: Exception) {
                    retryCount++
                    Timber.w("Connection attempt failed ($retryCount/$maxRetries): $e")
                    if (retryCount >= maxRetries) {
                        Timber.e("Failed to connect after $retryCount retries.")
                        connectionStateFlows[deviceId]?.emit(BleDeviceConnectionState.DISCONNECTED)
                        break
                    }
                    delayForRetry(retryDelayMillis)
                }
            }
        }
    }

    private fun establishConnection(bleDevice: RxBleDevice) {
        connectionDisposables[bleDevice.macAddress] = bleDevice.establishConnection(false)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSubscribe {
                Timber.d("Establishing connection to device: ${bleDevice.macAddress}")
            }
            .subscribe(
                { connection ->
                    Timber.d("Connected to device: ${bleDevice.macAddress}")
                    // Emit the CONNECTED state
                    scope.launch {
                        // Initialize the device after establishing the connection
                        if (initializeDevice(bleDevice.macAddress, connection)) {
                            connectionStateFlows[bleDevice.macAddress]?.emit(BleDeviceConnectionState.CONNECTED)
                            listenToConnectionStateUpdates(bleDevice)
                        }
                    }
                },
                { error ->
                    Timber.e(error, "Connection failed for device: ${bleDevice.macAddress}")
                    scope.launch {
                        connectionStateFlows[bleDevice.macAddress]?.emit(BleDeviceConnectionState.DISCONNECTED)
                    }
                    throw(error)
                }
            ).addTo(compositeDisposable)
    }

    private fun listenToConnectionStateUpdates(bleDevice: RxBleDevice) {
        val deviceId = bleDevice.macAddress

        connectionStateSubscriptions[deviceId]?.dispose()

        bleDevice.observeConnectionStateChanges()
            .subscribe { state ->
                when (state) {
                    RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                        scope.launch {
                            processDisconnect(deviceId)
                        }
                    }
                    else -> Unit
                }
            }.addTo(CompositeDisposable().also {
                connectionStateSubscriptions[deviceId] = it
            })
    }

    private suspend fun initializeDevice(deviceId: String, connection: RxBleConnection): Boolean {
        return try {
            Timber.d("Initializing services for device: $deviceId")

            //Add the data processor to process reads from and writes to the device.
            dataHandler.addDataProcessor(deviceId, connection)
            batteryHandler.addBatteryListener(deviceId, connection)

            //Add the command executor to send commands to the device and receive feedback for sent commands.
            commandExecutorsManager.createCommandExecutor(deviceId)

            devicesInfoManager.fetchInfo(deviceId)

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
        try {
            // Dispose of any active connection streams
            connectionStateSubscriptions[deviceId]?.dispose()
            connectionStateSubscriptions.remove(deviceId)

            // Now disconnect the BLE device if it's connected
            if (bleDevice.connectionState == RxBleConnection.RxBleConnectionState.CONNECTED) {
                processDisconnect(deviceId)
                Timber.i("Disconnected from device: $deviceId")
            } else {
                Timber.i("Device: $deviceId is already disconnected")
                processDisconnect(deviceId)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during disconnect for device: $deviceId")
        }
    }

    private fun processDisconnect(deviceId: String) {
        dataHandler.close(deviceId)
        batteryHandler.close(deviceId)
        connectionDisposables[deviceId]?.dispose()
        connectionDisposables.remove(deviceId)
        connectionStateSubscriptions[deviceId]?.dispose()
        connectionStateSubscriptions.remove(deviceId)
        scope.launch {
            connectionStateFlows[deviceId]?.emit(BleDeviceConnectionState.DISCONNECTED)
        }
    }

    private suspend fun delayForRetry(retryDelayMillis: Long) {
        delay(retryDelayMillis)
    }

    override fun close() {
        compositeDisposable.dispose()
        scope.cancel()
        connectionStateSubscriptions.keys.toList().forEach { disconnect(it) }
        connectionStateSubscriptions.clear()
    }
}