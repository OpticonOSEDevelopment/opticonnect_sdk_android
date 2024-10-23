package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.DataLength
import com.opticon.opticonnect.sdk.api.entities.CommandResponse

/**
 * Interface representing settings for Code 2 of 5 and S-Code symbologies.
 *
 * This interface provides methods to configure space checks, data length, and S-code
 * transmission for Code 2 of 5 and S-Code symbologies.
 */
interface Code2Of5AndSCode {

    /**
     * Sets the space check mode for Industrial 2 of 5 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the space check.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSpaceCheck(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the transmission mode of S-Code as Interleaved 2 of 5.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to transmit (`true`) or not transmit (`false`) the S-Code as Interleaved 2 of 5.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSCodeTransmissionAsInterleaved(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Sets the minimum data length for Code 2 of 5 and S-Code symbologies.
     *
     * @param deviceId The identifier of the target device.
     * @param dataLength The [DataLength] enum value representing the minimum data length.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setMinimumDataLength(deviceId: String, dataLength: DataLength): CommandResponse
}
