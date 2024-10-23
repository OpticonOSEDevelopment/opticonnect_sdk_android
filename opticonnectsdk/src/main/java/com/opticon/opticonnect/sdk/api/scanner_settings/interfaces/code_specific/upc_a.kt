package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCALeadingZeroAndTransmitCDMode

/**
 * Interface representing settings for UPC_A symbology.
 *
 * This interface provides methods to configure leading zero and check digit transmission settings,
 * as well as add-on support for UPC_A symbology.
 */
interface UPCA {

    /**
     * Sets the leading zero and check digit transmission mode for UPC_A symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired leading zero and check digit transmission mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setLeadingZeroAndTransmitCDMode(deviceId: String, mode: UPCALeadingZeroAndTransmitCDMode): CommandResponse

    /**
     * Sets the 2-character add-on for UPC_A symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 2-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the 5-character add-on for UPC_A symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 5-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse
}
