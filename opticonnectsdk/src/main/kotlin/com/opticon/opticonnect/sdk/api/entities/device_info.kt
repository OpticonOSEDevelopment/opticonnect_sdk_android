package com.opticon.opticonnect.sdk.api.entities

/**
 * A class representing basic information about a BLE device.
 * This class holds the information fetched from a BLE device, such as
 * its MAC address, serial number, local name, and firmware version.
 */
data class DeviceInfo(
    /**
     * The unique identifier (BLE device ID) for the device.
     */
    val deviceId: String,

    /**
     * The MAC address of the device.
     */
    val macAddress: String,

    /**
     * The serial number of the device.
     */
    val serialNumber: String,

    /**
     * The local name (advertising name) of the device.
     */
    val localName: String,

    /**
     * The firmware version of the device.
     */
    val firmwareVersion: String
) {

    override fun toString(): String {
        return "DeviceInfo(deviceId: $deviceId, macAddress: $macAddress, " +
                "serialNumber: $serialNumber, localName: $localName, " +
                "firmwareVersion: $firmwareVersion)"
    }
}