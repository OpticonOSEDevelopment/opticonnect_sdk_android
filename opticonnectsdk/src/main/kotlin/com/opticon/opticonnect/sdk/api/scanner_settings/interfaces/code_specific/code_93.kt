package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for Code 93 symbology.
 *
 * This interface provides methods to enable or disable check digit validation,
 * and concatenation for Code 93 symbology.
 */
interface Code93 {

    /**
     * Sets the check digit validation for Code 93 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the check digit validation.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setCheckCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the check digit validation.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setCheckCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the concatenation mode for Code 93 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) concatenation.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setConcatenation] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable concatenation.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setConcatenation(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)
}
