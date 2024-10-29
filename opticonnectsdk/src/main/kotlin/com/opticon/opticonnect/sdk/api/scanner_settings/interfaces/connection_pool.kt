package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface for managing connection pool settings in BLE devices.
 *
 * Provides methods for setting, resetting, validating hex IDs, and generating QR data for connection pools.
 */
interface ConnectionPool {

    /**
     * Sets the connection pool ID to the specified 4-character hexadecimal value.
     *
     * @param deviceId The identifier of the target device.
     * @param poolId The 4-character hexadecimal connection pool ID.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setId(deviceId: String, poolId: String): CommandResponse

    /**
     * Retrieves the current connection pool ID for the specified device.
     *
     * @param deviceId The identifier of the target device.
     * @return The 4-character hexadecimal connection pool ID.
     */
    suspend fun getId(deviceId: String): String

    /**
     * Resets the connection pool ID to the default '0000'.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun resetId(deviceId: String): CommandResponse

    /**
     * Checks if the given ID is a valid 4-character hexadecimal value.
     *
     * @param poolId The connection pool ID to validate.
     * @return True if the ID is valid, false otherwise.
     */
    fun isValidId(poolId: String): Boolean

    /**
     * Generates a configuration command string that can be encoded into a QR code.
     *
     * @param poolId A valid 4-character hexadecimal connection pool ID.
     * @return The QR code data string, or an empty string if the ID is invalid.
     */
    fun getConnectionPoolQRData(poolId: String): String
}
