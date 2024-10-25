package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.BleDevicesStreamsHandler
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the [BluetoothManager] interface.
 *
 * This class handles all BLE-related operations and is hidden from the SDK clients.
 * It is injected using a dependency injection system and managed internally.
 */
@Singleton
internal class BluetoothManagerImpl @Inject constructor(
    private val bleDevicesDiscoverer: BleDevicesDiscoverer,
    private val bleConnectivityHandler: BleConnectivityHandler,
    private val bleDevicesStreamsHandler: BleDevicesStreamsHandler
) : BluetoothManager {

    private lateinit var context: Context

    override fun initialize(context: Context) {
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

    override val bleDiscoveredDevicesFlow: Flow<BleDiscoveredDevice>
        get() = bleDevicesDiscoverer.getDeviceDiscoveryFlow()

    override suspend fun connect(deviceId: String) {
        try {
            bleConnectivityHandler.connect(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Error connecting to device $deviceId")
            throw e
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
        return bleConnectivityHandler.getConnectionStateFlow(deviceId)
    }

    override suspend fun subscribeToBarcodeDataStream(deviceId: String): Flow<BarcodeData> {
        return bleDevicesStreamsHandler.dataHandler.getBarcodeDataStream(deviceId)
    }

    override fun close() {
        try {
            bleConnectivityHandler.close()
            bleDevicesDiscoverer.close()
            bleDevicesStreamsHandler.close()
        } catch (e: Exception) {
            Timber.e(e, "Error disposing Bluetooth resources")
            throw e
        }
    }
}