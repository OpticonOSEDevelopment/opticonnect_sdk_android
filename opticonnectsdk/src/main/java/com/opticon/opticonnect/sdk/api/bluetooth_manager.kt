package com.opticon.opticonnect.sdk.api

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.internal.services.ble.BleConnectivityHandler
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.BleDevicesStreamsHandler
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Bluetooth operations such as device discovery, connection, disconnection,
 * and streaming data for BLE devices.
 *
 * This class is responsible for handling all the Bluetooth-related operations and
 * can be injected as a singleton using a dependency injection system.
 *
 * It is recommended that you access this class via the `OptiConnect` class
 * and avoid direct initialization.
 */
@Singleton
class BluetoothManager @Inject constructor(
    private val bleDevicesDiscoverer: BleDevicesDiscoverer,
    private val bleConnectivityHandler: BleConnectivityHandler,
    private val bleDevicesStreamsHandler: BleDevicesStreamsHandler
) : Closeable {

    /**
     * Starts the BLE device discovery process.
     *
     * This method begins the scanning process for nearby BLE devices.
     * It logs the start and handles any errors that may occur during the discovery process.
     */
    fun startDiscovery(context: Context) {
        try {
            bleDevicesDiscoverer.startDiscovery(context)
        } catch (e: Exception) {
            Timber.e(e, "Error starting discovery")
            throw e
        }
    }

    /**
     * Stops the BLE device discovery process.
     *
     * This method ends the BLE scanning process.
     */
    fun stopDiscovery() {
        try {
            bleDevicesDiscoverer.stopDiscovery()
        } catch (e: Exception) {
            Timber.e(e, "Error stopping discovery")
            throw e
        }
    }

    /**
     * A flow of [BleDiscoveredDevice] representing discovered BLE devices.
     *
     * @return A flow of BLE devices discovered during the discovery process.
     */
    val bleDiscoveredDevicesFlow: Flow<BleDiscoveredDevice>
        get() = bleDevicesDiscoverer.getDeviceDiscoveryFlow()

    /**
     * Connects to the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * Attempts to establish a connection to the BLE device.
     */
    suspend fun connect(deviceId: String) {
        try {
            bleConnectivityHandler.connect(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Error connecting to device $deviceId")
            throw e
        }
    }

    /**
     * Disconnects from the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * Disconnects the BLE device.
     */
    fun disconnect(deviceId: String) {
        try {
            bleConnectivityHandler.disconnect(deviceId)
        } catch (e: Exception) {
            Timber.e(e, "Error disconnecting from device $deviceId")
            throw e
        }
    }

    /**
     * Listens to the connection state of the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * @return A flow of [BleDeviceConnectionState] indicating the connection state.
     */
    fun listenToConnectionState(deviceId: String): Flow<BleDeviceConnectionState> {
        // Retrieve the existing flow or create a new one if it doesn't exist
        return bleConnectivityHandler.getConnectionStateFlow(deviceId)
    }

    /**
     * Subscribes to the barcode data stream from the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * @return A flow of [BarcodeData] received from the device.
     */
    suspend fun subscribeToBarcodeDataStream(deviceId: String): Flow<BarcodeData> {
        return bleDevicesStreamsHandler.dataHandler.getBarcodeDataStream(deviceId)
    }

    /**
     * Cleans up BLE-related resources when no longer needed.
     *
     * This method disposes of the device discoverer and connectivity handler.
     */
    override fun close() {
        try {
            bleConnectivityHandler.dispose()
            bleDevicesDiscoverer.close()
            bleDevicesStreamsHandler.close()
        } catch (e: Exception) {
            Timber.e(e, "Error disposing Bluetooth resources")
            throw e
        }
    }
}