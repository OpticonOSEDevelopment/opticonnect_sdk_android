package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code11CheckCDSettings

/**
 * Interface representing settings for Code 11 symbology.
 *
 * This interface provides methods to configure check digit validation and transmission
 * settings for Code 11 symbology.
 */
interface Code11 {

    /**
     * Sets the check digit validation mode for Code 11 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The [Code11CheckCDSettings] enum value representing the desired setting.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCheckCD(deviceId: String, setting: Code11CheckCDSettings): CommandResponse

    /**
     * Callback-based version of [setCheckCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The [Code11CheckCDSettings] enum value representing the desired setting.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setCheckCD(deviceId: String, setting: Code11CheckCDSettings, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the transmission state of the check digit for Code 11 symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setTransmitCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the transmission of the check digit.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setTransmitCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)
}
