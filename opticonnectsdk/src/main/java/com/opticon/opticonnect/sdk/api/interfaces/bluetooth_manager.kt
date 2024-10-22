package com.opticon.opticonnect.sdk.api.interfaces

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing Bluetooth operations such as device discovery, connection, disconnection,
 * and streaming data for BLE devices.
 *
 * Clients of the SDK should interact with this interface.
 */
interface BluetoothManager {

    /**
     * Starts the BLE device discovery process.
     *
     * This method begins the scanning process for nearby BLE devices.
     */
    fun startDiscovery(context: Context)

    /**
     * Stops the BLE device discovery process.
     *
     * This method ends the BLE scanning process.
     */
    fun stopDiscovery()

    /**
     * A flow of [BleDiscoveredDevice] representing discovered BLE devices.
     *
     * @return A flow of BLE devices discovered during the discovery process.
     */
    val bleDiscoveredDevicesFlow: Flow<BleDiscoveredDevice>

    /**
     * Connects to the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * Attempts to establish a connection to the BLE device.
     */
    suspend fun connect(deviceId: String)

    /**
     * Disconnects from the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * Disconnects the BLE device.
     */
    fun disconnect(deviceId: String)

    /**
     * Listens to the connection state of the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * @return A flow of [BleDeviceConnectionState] indicating the connection state.
     */
    fun listenToConnectionState(deviceId: String): Flow<BleDeviceConnectionState>

    /**
     * Subscribes to the barcode data stream from the BLE device with the given [deviceId].
     *
     * @param deviceId The identifier of the target device.
     * @return A flow of [BarcodeData] received from the device.
     */
    suspend fun subscribeToBarcodeDataStream(deviceId: String): Flow<BarcodeData>
}
