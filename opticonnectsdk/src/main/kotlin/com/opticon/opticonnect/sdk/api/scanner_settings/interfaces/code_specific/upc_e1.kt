package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for UPCE1 symbology.
 *
 * This interface provides methods to configure leading zero, check digit transmission settings,
 * conversion modes, and add-on support for UPCE1 symbology.
 */
interface UPCE1 {

    /**
     * Sets the 2-character add-on for UPCE1 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (true) or disable (false) the 2-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus2(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the 5-character add-on for UPCE1 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (true) or disable (false) the 5-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus5(deviceId: String, enabled: Boolean): CommandResponse
}
