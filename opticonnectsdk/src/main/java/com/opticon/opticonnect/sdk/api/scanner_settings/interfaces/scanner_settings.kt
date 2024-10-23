package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CodeSpecific

/**
 * Interface representing the scanner settings for Opticon BLE scanners.
 *
 * This interface provides access to various settings, including symbology options,
 * indicator configurations, and methods for sending commands to the scanner.
 */
interface ScannerSettings {

    /**
     * Provides access to the barcode symbology settings.
     */
    val symbology: Symbology

    /**
     * Provides access to the settings of individual barcode symbologies.
     */
    val codeSpecific: CodeSpecific

    /**
     * Provides access to the indicator settings, such as buzzer and LED configurations.
     */
    val indicator: Indicator

    /**
     * Sends a command to the connected BLE device.
     *
     * @param deviceId The unique identifier of the BLE device to send the command to.
     * @param command The command to be executed, along with any necessary parameters.
     * @return A [CommandResponse] indicating the result of the command execution.
     * @throws Exception if the command fails to send or an error occurs.
     */
    suspend fun executeCommand(deviceId: String, command: ScannerCommand): CommandResponse

    /**
     * Retrieves the current scanner settings from the connected BLE device.
     *
     * @param deviceId The unique identifier of the BLE device to fetch settings from.
     * @return A list of [CommandData], each containing the command name and associated parameters.
     * @throws Exception if fetching settings fails or an error occurs.
     */
    suspend fun getSettings(deviceId: String): List<CommandData>

    /**
     * Resets the scanner settings to their default values.
     *
     * @param deviceId The unique identifier of the BLE device to reset settings for.
     * @return `true` if the settings were successfully reset, `false` otherwise.
     * @throws Exception if resetting the settings fails or an error occurs.
     */
    suspend fun resetSettings(deviceId: String): Boolean
}
