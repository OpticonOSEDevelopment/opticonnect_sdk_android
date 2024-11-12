package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.TelepenMode

/**
 * Interface representing settings for Telepen symbology.
 *
 * This interface provides methods to configure the code mode for Telepen symbology.
 */
interface Telepen {

    /**
     * Sets the code mode for Telepen symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired Telepen code mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setMode(deviceId: String, mode: TelepenMode): CommandResponse

    /**
     * Callback-based version of [setMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired Telepen code mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setMode(deviceId: String, mode: TelepenMode, callback: (Result<CommandResponse>) -> Unit)
}
