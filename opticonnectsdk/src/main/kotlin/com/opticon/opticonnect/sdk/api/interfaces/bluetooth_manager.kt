package com.opticon.opticonnect.sdk.api.interfaces

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import kotlinx.coroutines.flow.Flow
import java.io.Closeable

/**
 * Interface for managing Bluetooth operations with Opticon BLE scanners.
 *
 * This interface helps you handle tasks such as discovering devices, connecting and disconnecting,
 * and listening to data streams from your Opticon BLE scanners.
 */
interface BluetoothManager {
    /**
     * Starts discovering Opticon BLE scanners nearby.
     *
     * Use this method to initiate a scan for nearby Opticon BLE scanners.
     */
    fun startDiscovery()

    /**
     * Stops discovering Opticon BLE scanners.
     *
     * Use this method to halt the scanning process for nearby devices.
     */
    fun stopDiscovery()

    /**
     * A flow of [BleDiscoveredDevice] representing Opticon BLE scanners discovered during scanning.
     *
     * @return A flow of BLE devices that you have discovered during the discovery process.
     */
    val bleDiscoveredDevicesFlow: Flow<BleDiscoveredDevice>

    /**
     * Connects to a specific Opticon BLE scanner using its [deviceId].
     *
     * Use this method to attempt a connection with the Opticon BLE scanner
     * specified by the [deviceId].
     *
     * @param deviceId The identifier of the target scanner.
     */
    suspend fun connect(deviceId: String)

    /**
     * Disconnects from a specific Opticon BLE scanner using its [deviceId].
     *
     * Use this method to disconnect from the specified Opticon BLE scanner.
     *
     * @param deviceId The identifier of the target scanner.
     */
    fun disconnect(deviceId: String)

    /**
     * Listens to the connection state of a specific Opticon BLE scanner.
     *
     * Use this method to get updates about the connection state of the scanner specified
     * by the [deviceId]. You will receive updates as a flow of [BleDeviceConnectionState].
     *
     * @param deviceId The identifier of the target scanner.
     * @return A flow of [BleDeviceConnectionState] indicating the current connection state.
     */
    fun listenToConnectionState(deviceId: String): Flow<BleDeviceConnectionState>

    /**
     * Listens to the barcode data stream from a specific Opticon BLE scanner.
     *
     * Use this method to receive barcode data from the scanner specified by the [deviceId].
     * You will get data as a flow of [BarcodeData] whenever a barcode is scanned.
     *
     * @param deviceId The identifier of the target scanner.
     * @return A flow of [BarcodeData] received from the scanner.
     */
    fun listenToBarcodeData(deviceId: String): Flow<BarcodeData>
}
