package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.Callback
import com.opticon.opticonnect.sdk.api.interfaces.ListenerSubscription
import com.opticon.opticonnect.sdk.internal.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.BleDevicesStreamsHandler
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

    private class JobListenerSubscription(private val job: Job) : ListenerSubscription {
        override val isClosed: Boolean
            get() = !job.isActive

        override fun close() {
            job.cancel()
        }
    }

    override fun initialize(context: Context) {
        Timber.d("Initializing BluetoothManager!")
        this.context = context.applicationContext
        Timber.d("BluetoothManager initialized with context")
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

    private fun <T> collectWithCallback(flow: Flow<T>, callback: Callback<T>): ListenerSubscription {
        val job = coroutineScope.launch {
            try {
                flow.collect { data ->
                    withContext(Dispatchers.Main) { callback.onSuccess(data) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) { callback.onError(e) }
            }
        }
        return JobListenerSubscription(job)
    }

    override fun listenToDiscoveredDevices(callback: Callback<BleDiscoveredDevice>): ListenerSubscription {
        return collectWithCallback(listenToDiscoveredDevices(), callback)
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

    override fun listenToConnectionState(
        deviceId: String,
        callback: Callback<BleDeviceConnectionState>
    ): ListenerSubscription {
        return collectWithCallback(listenToConnectionState(deviceId), callback)
    }

    override fun listenToBarcodeData(deviceId: String): Flow<BarcodeData> {
        return bleDevicesStreamsHandler.getOrCreateBarcodeStream(deviceId)
    }

    override fun listenToBarcodeData(deviceId: String, callback: Callback<BarcodeData>): ListenerSubscription {
        return collectWithCallback(listenToBarcodeData(deviceId), callback)
    }

    override fun getLatestBatteryPercentage(deviceId: String): Int? {
        return bleDevicesStreamsHandler.getLatestBatteryPercentage(deviceId)
    }

    override fun listenToBatteryPercentage(deviceId: String): Flow<Int> {
        return bleDevicesStreamsHandler.getOrCreateBatteryPercentageStream(deviceId)
    }

    override fun listenToBatteryPercentage(deviceId: String, callback: Callback<Int>): ListenerSubscription {
        return collectWithCallback(listenToBatteryPercentage(deviceId), callback)
    }

    override fun listenToBatteryStatus(deviceId: String): Flow<BatteryLevelStatus> {
        return bleDevicesStreamsHandler.getOrCreateBatteryStatusStream(deviceId)
    }

    override fun listenToBatteryStatus(deviceId: String, callback: Callback<BatteryLevelStatus>): ListenerSubscription {
        return collectWithCallback(listenToBatteryStatus(deviceId), callback)
    }

    override fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus? {
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
