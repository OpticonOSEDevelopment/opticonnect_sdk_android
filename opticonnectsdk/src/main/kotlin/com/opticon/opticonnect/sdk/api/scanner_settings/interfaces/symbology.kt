package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.SymbologyType

/**
 * Interface defining methods for configuring symbology settings on the scanner device.
 *
 * Provides both coroutine-based suspend functions for Kotlin and callback-based methods for Java interoperability.
 */
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
     * Callback-based version of [enableOnlySymbology] for Java interoperability.
     *
     * @param deviceId The ID of the device where the symbology should be enabled.
     * @param type The symbology to enable exclusively.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun enableOnlySymbology(
        deviceId: String,
        type: SymbologyType,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Toggles a specific symbology on the device based on the [enabled] flag.
     *
     * @param deviceId The ID of the device where the symbology should be enabled or disabled.
     * @param type The symbology to enable or disable.
     * @param enabled A boolean flag indicating whether to enable (true) or disable (false) the symbology.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSymbology(deviceId: String, type: SymbologyType, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setSymbology] for Java interoperability.
     *
     * @param deviceId The ID of the device where the symbology should be enabled or disabled.
     * @param type The symbology to enable or disable.
     * @param enabled A boolean flag indicating whether to enable (true) or disable (false) the symbology.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setSymbology(
        deviceId: String,
        type: SymbologyType,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    )
}
