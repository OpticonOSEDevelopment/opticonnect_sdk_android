package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCELeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCEConversionMode

/**
 * Interface representing settings for UPCE symbology.
 *
 * This interface provides methods to configure leading zero, check digit transmission settings,
 * conversion modes, and add-on support for UPCE symbology.
 */
interface UPCE {

    /**
     * Sets the leading zero and check digit transmission mode for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired leading zero and check digit transmission mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode
    ): CommandResponse

    /**
     * Sets the conversion mode for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired conversion mode for UPCE symbology.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode
    ): CommandResponse

    /**
     * Sets the 2-character add-on for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 2-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse

    /**
     * Sets the 5-character add-on for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 5-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse
}
