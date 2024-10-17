package com.opticon.opticonnect.sdk.api.scanner_settings

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.api.ScannerFeedback
import com.opticon.opticonnect.sdk.api.constants.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A class representing the scanner settings for Opticon BLE scanners.
 *
 * This class provides access to various settings, including enabling specific codes.
 *
 * It is recommended that you access this class via the `OptiConnect` class
 * and avoid direct initialization.
 */
@Singleton
class ScannerSettings @Inject constructor(
    private val symbology: Symbology,
    feedback: ScannerFeedback
) : SettingsBase(feedback) {

    /**
     * Sends a command to the connected BLE device.
     *
     * The [deviceId] specifies the BLE device to send the command to.
     * The [command] represents the command to be sent, along with any associated parameters.
     * 3-letter commands should always start with '[',
     * and 4-letter commands should start with ']'.
     *
     * Returns a [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun executeCommand(
        deviceId: String,
        command: ScannerCommand
    ): CommandResponse {
        return try {
            return CommandResponse.succeeded()
        } catch (e: Exception) {
            throw e
        }
    }
}

    /**
     * Retrieves the current scanner settings from the device as a list of [CommandData].
     *
     * The [deviceId] specifies the BLE device whose settings will be retrieved.
     *
     * The method will return a list of [CommandData], where each [CommandData] object contains
     * the command name and its associated parameters, if any.
     *
     * Be sure to first persist the settings by calling [persistSettings] before fetching them.
     *
     * Throws an exception if there is an error during the process or if fetching the settings fails.
     *
     * Returns:
     * - A list of [CommandData] representing the current scanner settings.
     */
//    suspend fun getSettings(deviceId: String): List<CommandData> {
//        return try {
//            val result = _commandExecutorsManager.sendCommand(
//                deviceId,
//                ScannerCommand(fetchSettings, sendFeedback = false, ledFeedback = false, buzzerFeedback = false, vibrationFeedback = false)
//            )
//
//            if (!result.succeeded) {
//                throw Exception("Error fetching settings for device $deviceId: ${result.response}")
//            }
//
//            _scannerSettingsCompressor.getCompressedSettingsList(result.response)
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
///**
// * Resets the scanner settings to the default.
// *
// * The [deviceId] specifies the BLE device