package com.opticon.opticonnect.sdk.api.entities

/**
 * A class representing basic information about a connected Opticon BLE scanner.
 *
 * This class contains detailed information fetched from a connected Opticon BLE scanner, including
 * its MAC address, serial number, local name, and firmware version.
 */
data class DeviceInfo(
    /**
     * The unique identifier (BLE device ID) for the connected scanner.
     */
    val deviceId: String,

    /**
     * The MAC address of the connected scanner.
     */
    val macAddress: String,

    /**
     * The serial number of the connected scanner.
     */
    val serialNumber: String,

    /**
     * The local name (advertising name) of the connected scanner.
     */
    val localName: String,

    /**
     * The firmware version of the connected scanner.
     */
    val firmwareVersion: String
) {

    override fun toString(): String {
        return "DeviceInfo(deviceId: $deviceId, macAddress: $macAddress, " +
                "serialNumber: $serialNumber, localName: $localName, " +
                "firmwareVersion: $firmwareVersion)"
    }
}