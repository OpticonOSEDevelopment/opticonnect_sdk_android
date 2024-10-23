package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.IATACheckCDSettings

/**
 * Interface representing settings for IATA symbology.
 *
 * This interface provides methods to configure the check digit validation mode
 * and manage the transmission of check digits for IATA symbology.
 */
interface IATA {

    /**
     * Sets the check digit validation mode for IATA symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The [IATACheckCDSettings] enum value representing the desired setting.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCheckCD(deviceId: String, setting: IATACheckCDSettings): CommandResponse

    /**
     * Sets the transmission of the check digit for IATA symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse
}
