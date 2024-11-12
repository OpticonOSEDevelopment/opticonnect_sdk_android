package com.opticon.opticonnect.sdk.api.interfaces

import com.opticon.opticonnect.sdk.api.entities.DeviceInfo

/**
 * The [ScannerInfo] interface defines a method for you to obtain detailed information
 * about a connected Opticon BLE scanner, such as its MAC address, serial number, local name, and firmware version.
 */
interface ScannerInfo {

    /**
     * Retrieves detailed information about a connected Opticon BLE scanner.
     *
     * Use this method to get stored information about the scanner specified by [deviceId].
     * You will receive details including the scanner's MAC address, serial number, local name,
     * and firmware version.
     *
     * @param deviceId The unique identifier of the Opticon BLE scanner.
     * @return A [DeviceInfo] object containing detailed information about the scanner.
     */
    fun getInfo(deviceId: String): DeviceInfo
}