package com.opticon.opticonnect.sdk.internal.entities

/**
 * Abstract class representing a base command for the scanner.
 *
 * @property code The command code to be sent to the scanner.
 * @property parameters Optional parameters to be included with the command.
 * @property sendFeedback A flag to indicate if feedback should be sent.
 * @property ledFeedback A flag to indicate if LED feedback is enabled.
 * @property buzzerFeedback A flag to indicate if buzzer feedback is enabled.
 * @property vibrationFeedback A flag to indicate if vibration feedback is enabled.
 */
abstract class BaseCommand(
    val code: String,
    val parameters: List<String> = emptyList(),
    val sendFeedback: Boolean = true,
    val ledFeedback: Boolean = true,
    val buzzerFeedback: Boolean = true,
    val vibrationFeedback: Boolean = true
)
