package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.SymbologyType

interface Symbology {
    /**
     * Enables a specific symbology on the device.
     *
     * @param deviceId The ID of the device where the symbology should be enabled.
     * @param type The symbology to enable exclusively.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun enableOnlySymbology(deviceId: String, type: SymbologyType): CommandResponse

    /**
     * Toggles a specific symbology on the device based on the [enabled] flag.
     *
     * @param deviceId The ID of the device where the symbology should be enabled or disabled.
     * @param type The symbology to enable or disable.
     * @param enabled A boolean flag indicating whether to enable (true) or disable (false) the symbology.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSymbology(deviceId: String, type: SymbologyType, enabled: Boolean): CommandResponse
}