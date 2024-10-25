package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for Korean Postal Authority Code symbology.
 *
 * This interface provides methods to enable or disable the transmission of check digits,
 * and to manage the transmission of dashes and orientation for the Korean Postal Authority Code symbology.
 */
interface KoreanPostalAuthority {

    /**
     * Sets the transmission of the check digit for Korean Postal Authority Code symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission of dashes in the barcode data for Korean Postal Authority Code symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of dashes.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitDash(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the orientation of the scan engine for Korean Postal Authority Code symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param setUpsideDown A boolean indicating whether to set the orientation to upside-down (true) or normal (false).
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setKoreanPostalOrientation(deviceId: String, setUpsideDown: Boolean): CommandResponse

    /**
     * Sets upside-down reading for Korean Postal Authority Code symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) upside-down reading.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setUpsideDownReading(deviceId: String, enabled: Boolean): CommandResponse
}