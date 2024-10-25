package com.opticon.opticonnect.sdk.api.interfaces

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
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
     * Indicates whether the discovery process is currently active.
     *
     * This property helps you check if the system is currently discovering BLE scanners.
     *
     * @return A boolean indicating if the discovery process is ongoing.
     */
    val isDiscovering: Boolean

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

    /**
     * Listens to the battery percentage of a specific Opticon BLE scanner.
     *
     * Use this method to get updates on the battery percentage of the scanner specified
     * by the [deviceId]. The percentage will be provided as an integer, typically ranging
     * from 0 to 100.
     *
     * @param deviceId The identifier of the target scanner.
     * @return A flow of integers representing the current battery percentage.
     */
    fun listenToBatteryPercentage(deviceId: String): Flow<Int>

    /**
     * Retrieves the latest battery percentage of a specific Opticon BLE scanner.
     *
     * Use this method to obtain the most recent battery percentage of the scanner specified
     * by the [deviceId]. The percentage is returned as an integer, typically ranging from 0 to 100.
     *
     * @param deviceId The identifier of the target scanner.
     * @return An integer representing the latest battery percentage.
     */
    fun getLatestBatteryPercentage(deviceId: String): Int

    /**
     * Listens to the battery status of a specific Opticon BLE scanner.
     *
     * Use this method to get updates on the battery status of the scanner specified by the
     * [deviceId]. The status will be provided as a flow of [BatteryLevelStatus], indicating
     * the current status details such as charging state and battery health.
     *
     * @param deviceId The identifier of the target scanner.
     * @return A flow of [BatteryLevelStatus] indicating the current battery status.
     */
    fun listenToBatteryStatus(deviceId: String): Flow<BatteryLevelStatus>

    /**
     * Retrieves the latest battery status of a specific Opticon BLE scanner.
     *
     * Use this method to obtain the most recent battery status of the scanner specified by
     * the [deviceId]. The status includes details such as charging state, battery health, and more.
     *
     * @param deviceId The identifier of the target scanner.
     * @return A [BatteryLevelStatus] object indicating the latest battery status.
     */
    fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus
}
