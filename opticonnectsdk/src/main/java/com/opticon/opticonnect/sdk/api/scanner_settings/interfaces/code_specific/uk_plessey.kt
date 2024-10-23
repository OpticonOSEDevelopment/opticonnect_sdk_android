package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for UK Plessey symbology.
 *
 * This interface provides methods to configure check digit transmission and space insertion for UK Plessey symbology.
 */
interface UKPlessey {

    /**
     * Sets the transmission of check digits for UK Plessey symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of check digits.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitCDs(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets space insertion between characters for UK Plessey symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) space insertion.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the A to X conversion for UK Plessey symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the A to X conversion.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAToXConversion(deviceId: String, enabled: Boolean): CommandResponse
}
