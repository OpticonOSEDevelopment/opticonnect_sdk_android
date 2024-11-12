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
     * Callback-based version of [setId] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param poolId The 4-character hexadecimal connection pool ID.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setId(deviceId: String, poolId: String, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Caches the connection pool ID locally for the specified device.
     *
     * This method stores the provided `poolId` for a given `deviceId` in memory,
     * allowing it to be quickly retrieved later via [getId] without needing to re-fetch from the device.
     *
     * @param deviceId The identifier of the target device.
     * @param poolId The 4-character hexadecimal connection pool ID to cache.
     */
    fun cacheId(deviceId: String, poolId: String)

    /**
     * Retrieves the current connection pool ID for the specified device.
     *
     * @param deviceId The identifier of the target device.
     * @return The 4-character hexadecimal connection pool ID.
     */
    suspend fun getId(deviceId: String): String

    /**
     * Callback-based version of [getId] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive the connection pool ID as a [String].
     */
    fun getId(deviceId: String, callback: (Result<String>) -> Unit)

    /**
     * Resets the connection pool ID to the default '0000'.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun resetId(deviceId: String): CommandResponse

    /**
     * Callback-based version of [resetId] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive [CommandResponse].
     */
    fun resetId(deviceId: String, callback: (Result<CommandResponse>) -> Unit)

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
