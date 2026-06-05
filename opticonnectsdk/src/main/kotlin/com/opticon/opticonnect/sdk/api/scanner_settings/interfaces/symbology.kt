package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.interfaces.Callback

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
        callback: Callback<CommandResponse>
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
        callback: Callback<CommandResponse>
    )

    /**
     * Returns whether the provided symbology is currently enabled in the SDK's scanner settings state.
     *
     * The state is initialized from the scanner settings when the scanner connects and refreshed when
     * [com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings.getSettings] is called.
     *
     * @param deviceId The ID of the device to read the symbology state for.
     * @param type The symbology to check.
     * @return True when the symbology is enabled, false otherwise.
     */
    fun isSymbologyEnabled(deviceId: String, type: SymbologyType): Boolean
}
