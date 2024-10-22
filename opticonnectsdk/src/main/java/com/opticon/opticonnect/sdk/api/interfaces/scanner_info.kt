package com.opticon.opticonnect.sdk.api.interfaces

import com.opticon.opticonnect.sdk.api.entities.DeviceInfo

/**
 * Interface for retrieving information about a BLE device.
 *
 * The [ScannerInfo] interface defines a method to retrieve detailed information
 * about a BLE device such as its MAC address, serial number, local name, and firmware version.
 */
interface ScannerInfo {

    /**
     * Retrieves information about a BLE device.
     *
     * This method fetches the stored information about the device specified by [deviceId],
     * including the MAC address, serial number, local name, and firmware version.
     *
     * @param deviceId The unique identifier of the BLE device.
     * @return A [DeviceInfo] object containing detailed information about the device.
     */
    fun getInfo(deviceId: String): DeviceInfo
}
