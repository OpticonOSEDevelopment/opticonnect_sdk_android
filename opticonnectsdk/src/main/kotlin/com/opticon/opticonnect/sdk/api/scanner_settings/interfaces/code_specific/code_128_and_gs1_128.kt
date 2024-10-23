package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code128AndGS1128Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.GS1128ConversionMode

/**
 * Interface representing settings for Code 128 and GS1-128 symbologies.
 *
 * This interface provides methods to configure the mode and manage concatenation for these symbologies.
 */
interface Code128AndGS1128 {

    /**
     * Sets the mode for Code 128 and GS1-128 symbologies.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The [Code128AndGS1128Mode] enum value representing the desired mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setGS1128Mode(deviceId: String, mode: Code128AndGS1128Mode): CommandResponse

    /**
     * Sets the conversion mode for GS1-128 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The [GS1128ConversionMode] enum value representing the desired conversion mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setGS1128ConversionMode(deviceId: String, mode: GS1128ConversionMode): CommandResponse

    /**
     * Sets the concatenation mode for Code 128 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) concatenation.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setConcatenation(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the output mode for the leading ]C1 character in Code 128 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the leading ]C1 output.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setLeadingC1Output(deviceId: String, enabled: Boolean): CommandResponse
}
