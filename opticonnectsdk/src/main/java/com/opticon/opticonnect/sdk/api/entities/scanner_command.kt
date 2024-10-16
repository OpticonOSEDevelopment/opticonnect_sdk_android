package com.opticon.opticonnect.sdk.api.entities

import com.opticon.opticonnect.sdk.internal.entities.BaseCommand

/**
 * Class representing a specific scanner command, inheriting from [BaseCommand].
 *
 * This class is used to create specific scanner commands with optional parameters
 * and feedback settings for LED, buzzer, and vibration.
 *
 * @param code The command code to be sent to the scanner.
 * @param parameters Optional parameters to be included with the command.
 * @param sendFeedback A flag to indicate if feedback should be sent.
 * @param ledFeedback A flag to indicate if LED feedback is enabled.
 * @param buzzerFeedback A flag to indicate if buzzer feedback is enabled.
 * @param vibrationFeedback A flag to indicate if vibration feedback is enabled.
 */
class ScannerCommand(
    code: String,
    parameters: List<String> = emptyList(),
    sendFeedback: Boolean = true,
    ledFeedback: Boolean = true,
    buzzerFeedback: Boolean = true,
    vibrationFeedback: Boolean = true
) : BaseCommand(
    code = code,
    parameters = parameters,
    sendFeedback = sendFeedback,
    ledFeedback = ledFeedback,
    buzzerFeedback = buzzerFeedback,
    vibrationFeedback = vibrationFeedback
)