package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for GS1 Databar symbology.
 *
 * This interface provides methods to manage the transmission of check digits
 * and AI (Application Identifier) settings for GS1 Databar symbology.
 */
interface GS1Databar {

    /**
     * Sets the transmission of the check digit for GS1 Databar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission of the AI (Application Identifier) for GS1 Databar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the AI.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitAI(deviceId: String, enabled: Boolean): CommandResponse
}
