package com.opticon.opticonnect.sdk.api.interfaces

import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing Bluetooth operations with Opticon BLE scanners.
 *
 * This interface helps you handle tasks such as discovering devices, connecting and disconnecting,
 * and listening to data streams from your Opticon BLE scanners.
 */
interface BluetoothManager {
    // Discovery Methods

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
    fun listenToDiscoveredDevices(): Flow<BleDiscoveredDevice>

    /**
     * Callback-based version of [listenToDiscoveredDevices] for Java interoperability.
     *
     * @param callback The callback to receive discovered devices or errors.
     * @return A subscription that stops discovery updates when closed.
     */
    fun listenToDiscoveredDevices(callback: Callback<BleDiscoveredDevice>): ListenerSubscription

    // Connection Methods

    /**
     * Connects to a specific Opticon BLE scanner using its [deviceId].
     *
     * Use this method to attempt a connection with the Opticon BLE scanner
     * specified by the [deviceId].
     *
     * @param deviceId The identifier of the target scanner.
     * @throws com.opticon.opticonnect.sdk.api.exceptions.BleConnectionException when the connection cannot be established.
     */
    suspend fun connect(deviceId: String)

    /**
     * Callback-based version of [connect] for Java interoperability.
     *
     * Use this method to initiate a connection attempt. [Callback.onSuccess] is called only after
     * the scanner is connected; [Callback.onError] is called when the connection cannot be established.
     *
     * @param deviceId The identifier of the target scanner.
     * @param callback The callback to receive success or error status.
     */
    fun connect(deviceId: String, callback: Callback<Unit>)

    /**
     * Disconnects from a specific Opticon BLE scanner using its [deviceId].
     *
     * Use this method to disconnect from the specified Opticon BLE scanner.
     *
     * @param deviceId The identifier of the target scanner.
     */
    fun disconnect(deviceId: String)

    // Connection State Methods

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
     * Callback-based version of [listenToConnectionState] for Java interoperability.
     *
     * @param deviceId The identifier of the target scanner.
     * @param callback Callback to receive [BleDeviceConnectionState].
     * @return A subscription that stops connection state updates when closed.
     */
    fun listenToConnectionState(deviceId: String, callback: Callback<BleDeviceConnectionState>): ListenerSubscription

    // Barcode Data Methods

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
     * Callback-based version of [listenToBarcodeData] for Java interoperability.
     *
     * @param deviceId The identifier of the target scanner.
     * @param callback Callback to receive [BarcodeData].
     * @return A subscription that stops barcode updates when closed.
     */
    fun listenToBarcodeData(deviceId: String, callback: Callback<BarcodeData>): ListenerSubscription

    // Battery Percentage Methods

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
     * Callback-based version of [listenToBatteryPercentage] for Java interoperability.
     *
     * @param deviceId The identifier of the target scanner.
     * @param callback Callback to receive battery percentage as an [Int].
     * @return A subscription that stops battery percentage updates when closed.
     */
    fun listenToBatteryPercentage(deviceId: String, callback: Callback<Int>): ListenerSubscription

    /**
     * Retrieves the latest cached battery percentage of a specific Opticon BLE scanner.
     *
     * Use this method to obtain the most recent battery percentage of the scanner specified
     * by the [deviceId]. The percentage is returned as an integer, typically ranging from 0 to 100.
     * If no battery percentage has been received yet, or the device ID is unknown, this method returns null.
     *
     * @param deviceId The identifier of the target scanner.
     * @return An integer representing the latest battery percentage, or null if no battery percentage is available.
     */
    fun getLatestBatteryPercentage(deviceId: String): Int?

    // Battery Status Methods

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
     * Callback-based version of [listenToBatteryStatus] for Java interoperability.
     *
     * @param deviceId The identifier of the target scanner.
     * @param callback Callback to receive [BatteryLevelStatus].
     * @return A subscription that stops battery status updates when closed.
     */
    fun listenToBatteryStatus(deviceId: String, callback: Callback<BatteryLevelStatus>): ListenerSubscription

    /**
     * Retrieves the latest cached battery status of a specific Opticon BLE scanner.
     *
     * Use this method to obtain the most recent battery status of the scanner specified by
     * the [deviceId]. The status includes details such as charging state, battery health, and more.
     * If no battery status has been received yet, or the device ID is unknown, this method returns null.
     *
     * @param deviceId The identifier of the target scanner.
     * @return A [BatteryLevelStatus] object indicating the latest battery status, or null if no status is available.
     */
    fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus?
}
