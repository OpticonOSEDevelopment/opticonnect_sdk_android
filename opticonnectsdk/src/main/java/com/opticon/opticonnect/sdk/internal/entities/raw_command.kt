package com.opticon.opticonnect.sdk.internal.entities

class RawCommand(
    code: String,
    parameters: List<String> = emptyList(),
    sendFeedback: Boolean = true,
    ledFeedback: Boolean = true,
    buzzerFeedback: Boolean = true,
    vibrationFeedback: Boolean = true
) : Command(
    code = code,
    parameters = parameters,
    ledFeedback = ledFeedback,
    buzzerFeedback = buzzerFeedback,
    vibrationFeedback = vibrationFeedback,
    sendFeedback = sendFeedback
) {

    override fun buildParsedData() {
        val buffer = StringBuilder()
        buffer.append(code)
        parsedData = buffer.toString()
    }
}