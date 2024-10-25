package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base class for scanner settings.
 *
 * This class provides methods to send commands to the scanner with optional feedback settings.
 */
internal abstract class SettingsBase() {

    /**
     * Sends a command to the scanner with optional parameters and feedback settings.
     *
     * @param deviceId The ID of the device to send the command to.
     * @param command The command to send.
     * @param parameters Optional list of parameters for the command.
     * @param sendFeedback A flag to indicate if feedback should be sent.
     * @return A [CommandResponse] representing the result of the command execution.
     */
    protected suspend fun sendCommand(
        deviceId: String,
        command: String,
        parameters: List<String> = emptyList(),
        sendFeedback: Boolean = true
    ): CommandResponse {
        return withContext(Dispatchers.IO) {
            OptiConnect.scannerSettings.executeCommand(
                deviceId,
                ScannerCommand(
                    code = command,
                    parameters = parameters,
                    sendFeedback = sendFeedback,
                )
            )
        }
    }
}
