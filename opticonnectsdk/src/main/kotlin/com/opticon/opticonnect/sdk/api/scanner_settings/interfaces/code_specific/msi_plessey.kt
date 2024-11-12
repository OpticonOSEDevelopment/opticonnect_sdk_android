package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCDTransmissionSettings

/**
 * Interface representing settings for MSI Plessey symbology.
 *
 * This interface provides methods to configure check digit validation and transmission
 * settings for MSI Plessey symbology.
 */
interface MSIPlessey {

    /**
     * Sets the check digit validation mode for MSI Plessey symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The desired check digit validation mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings): CommandResponse

    /**
     * Callback-based version of [setCheckCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The desired check digit validation mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setCheckCD(deviceId: String, setting: MSIPlesseyCheckCDSettings, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the check digit transmission mode for MSI Plessey symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The desired check digit transmission mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings): CommandResponse

    /**
     * Callback-based version of [setCDTransmission] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param setting The desired check digit transmission mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setCDTransmission(deviceId: String, setting: MSIPlesseyCDTransmissionSettings, callback: (Result<CommandResponse>) -> Unit)
}
