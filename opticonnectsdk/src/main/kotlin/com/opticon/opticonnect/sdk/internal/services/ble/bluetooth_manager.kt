package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.Callback
import com.opticon.opticonnect.sdk.internal.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.BleDevicesStreamsHandler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BluetoothManagerImpl @Inject constructor(
    private val bleDevicesDiscoverer: BleDevicesDiscoverer,
    private val bleConnectivityHandler: BleConnectivityHandler,
    private val bleDevicesStreamsHandler: BleDevicesStreamsHandler
) : BluetoothManager, LifecycleHandler {

    private lateinit var context: Context

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun initialize(context: Context) {
        Timber.d("Initializing BluetoothManager!")
        this.context = context.applicationContext
        setUpRxJavaErrorHandler()
        Timber.d("BluetoothManager initialized with context")
    }

    private fun setUpRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is io.reactivex.rxjava3.exceptions.UndeliverableException) {
                Timber.e("Caught undeliverable exception: ${throwable.cause}")
            } else {
                Timber.e("Unexpected RxJava error: $throwable")
            }
        }
    }

    override fun startDiscovery() {
        try {
            bleDevicesDiscoverer.startDiscovery(context)
        } catch (e: Exception) {
            Timber.e(e, "Error starting discovery")
            throw e
        }
    }

    override fun stopDiscovery() {
        try {
            bleDevicesDiscoverer.stopDiscovery()
        } catch (e: Exception) {
            Timber.e(e, "Error stopping discovery")
            throw e
        }
    }

    override val isDiscovering: Boolean
        get() = bleDevicesDiscoverer.isDiscovering()

    override fun listenToDiscoveredDevices(): Flow<BleDiscoveredDevice> {
        return bleDevicesDiscoverer.getDeviceDiscoveryFlow()
    }

    override fun listenToDiscoveredDevices(callback: Callback<BleDiscoveredDevice>) {
        coroutineScope.launch {
            try {
                listenToDiscoveredDevices().collect { device ->
                    withContext(Dispatchers.Main) { callback.onSuccess(device) }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override suspend fun connect(deviceId: String) {
        try {
            bleConnectivityHandler.connect(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Error connecting to device $deviceId")
            throw e
        }
    }

    override fun connect(deviceId: String, callback: Callback<Unit>) {
        coroutineScope.launch {
            try {
                connect(deviceId)
                withContext(Dispatchers.Main) { callback.onSuccess(Unit) }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override fun disconnect(deviceId: String) {
        try {
            bleConnectivityHandler.disconnect(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Error disconnecting from device $deviceId")
            throw e
        }
    }

    override fun listenToConnectionState(deviceId: String): Flow<BleDeviceConnectionState> {
        return bleConnectivityHandler.getConnectionStateFlow(deviceId).distinctUntilChanged()
    }

    override fun listenToConnectionState(deviceId: String, callback: Callback<BleDeviceConnectionState>) {
        coroutineScope.launch {
            try {
                listenToConnectionState(deviceId).collect { state ->
                    withContext(Dispatchers.Main) { callback.onSuccess(state) }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override fun listenToBarcodeData(deviceId: String): Flow<BarcodeData> {
        return bleDevicesStreamsHandler.getOrCreateBarcodeStream(deviceId)
    }

    override fun listenToBarcodeData(deviceId: String, callback: Callback<BarcodeData>) {
        coroutineScope.launch {
            try {
                listenToBarcodeData(deviceId).collect { data ->
                    withContext(Dispatchers.Main) { callback.onSuccess(data) }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override fun getLatestBatteryPercentage(deviceId: String): Int {
        return bleDevicesStreamsHandler.getLatestBatteryPercentage(deviceId)
    }

    override fun listenToBatteryPercentage(deviceId: String): Flow<Int> {
        return bleDevicesStreamsHandler.getOrCreateBatteryPercentageStream(deviceId)
    }

    override fun listenToBatteryPercentage(deviceId: String, callback: Callback<Int>) {
        coroutineScope.launch {
            try {
                listenToBatteryPercentage(deviceId).collect { percentage ->
                    withContext(Dispatchers.Main) { callback.onSuccess(percentage) }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override fun listenToBatteryStatus(deviceId: String): Flow<BatteryLevelStatus> {
        return bleDevicesStreamsHandler.getOrCreateBatteryStatusStream(deviceId)
    }

    override fun listenToBatteryStatus(deviceId: String, callback: Callback<BatteryLevelStatus>) {
        coroutineScope.launch {
            try {
                listenToBatteryStatus(deviceId).collect { status ->
                    withContext(Dispatchers.Main) { callback.onSuccess(status) }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
    }

    override fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus {
        return bleDevicesStreamsHandler.getLatestBatteryStatus(deviceId)
    }

    override fun close() {
        try {
            bleConnectivityHandler.close()
            bleDevicesDiscoverer.close()
            coroutineScope.cancel() // Cancel all ongoing coroutines
        } catch (e: Exception) {
            Timber.e(e, "Error disposing Bluetooth resources")
            throw e
        }
    }
}
