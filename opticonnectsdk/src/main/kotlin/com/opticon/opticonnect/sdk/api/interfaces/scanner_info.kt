package com.opticon.opticonnect.sdk.api.interfaces

import com.opticon.opticonnect.sdk.api.entities.DeviceInfo

/**
 * The [ScannerInfo] interface defines a method for you to obtain detailed information
 * about a connected Opticon BLE scanner, such as its MAC address, serial number, local name, and firmware version.
 */
interface ScannerInfo {

    /**
     * Retrieves cached detailed information about a connected Opticon BLE scanner.
     *
     * Scanner information is fetched when the SDK initializes a connection to the scanner.
     * If information for [deviceId] has not been fetched yet, or the device ID is unknown,
     * this method returns null.
     *
     * @param deviceId The unique identifier of the Opticon BLE scanner.
     * @return A [DeviceInfo] object containing detailed information about the scanner, or null
     * if no scanner information is available for [deviceId].
     */
    fun getInfo(deviceId: String): DeviceInfo?
}
