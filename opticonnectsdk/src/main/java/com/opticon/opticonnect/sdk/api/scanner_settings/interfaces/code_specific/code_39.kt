package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39MinimumLength

/**
 * Interface representing settings for Code 39 symbology.
 *
 * This interface provides methods to configure the mode, check digit validation,
 * and transmission options for Code 39 symbology.
 */
interface Code39 {

    /**
     * Sets the mode for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The [Code39Mode] enum value representing the desired mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setMode(deviceId: String, mode: Code39Mode): CommandResponse

    /**
     * Sets the check digit validation for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the check digit validation.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission of the check digit for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission of start/stop characters for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of start/stop characters.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitSTSP(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the concatenation mode for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) concatenation.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission of the leading 'A' for IT Pharmaceutical mode in Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the leading 'A'.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitLdAForItPharm(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the minimum length for Code 39 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param length The [Code39MinimumLength] enum value representing the desired minimum length.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setMinLength(deviceId: String, length: Code39MinimumLength): CommandResponse
}
