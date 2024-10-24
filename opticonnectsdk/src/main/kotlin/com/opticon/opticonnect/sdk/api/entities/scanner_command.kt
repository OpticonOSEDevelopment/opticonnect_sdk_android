package com.opticon.opticonnect.sdk.api.entities

/**
 * Class representing a specific scanner command, inheriting from [BaseCommand].
 *
 * This class is used to create specific scanner commands with optional parameters
 * and feedback settings for LED, buzzer, and vibration. If the feedback parameters
 * are null and sendFeedback is true, global [ScannerFeedback] settings from the [OptiConnect] instance will be used.
 *
 * @param code The command code to be sent to the scanner.
 * @param parameters Optional parameters to be included with the command.
 * @param sendFeedback A flag to indicate if feedback should be sent.
 * @param ledFeedback A flag to indicate if LED feedback is enabled. Defaults to the global setting if `null`.
 * @param buzzerFeedback A flag to indicate if buzzer feedback is enabled. Defaults to the global setting if `null`.
 * @param vibrationFeedback A flag to indicate if vibration feedback is enabled. Defaults to the global setting if `null`.
 */
class ScannerCommand(
    code: String,
    parameters: List<String> = emptyList(),
    sendFeedback: Boolean = true,
    ledFeedback: Boolean? = null,
    buzzerFeedback: Boolean? = null,
    vibrationFeedback: Boolean? = null
) : BaseCommand(
    code = code,
    parameters = parameters,
    sendFeedback = sendFeedback,
    ledFeedback = ledFeedback,
    buzzerFeedback = buzzerFeedback,
    vibrationFeedback = vibrationFeedback
)