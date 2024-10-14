package com.opticon.opticonnect.sdk.internal.entities

import com.opticon.opticonnect.sdk.internal.extensions.addCommand
import com.opticon.opticonnect.sdk.public.entities.CommandResponse
import kotlinx.coroutines.CompletableDeferred

open class Command(
    code: String,
    parameters: List<String> = emptyList(),
    sendFeedback: Boolean = true,
    ledFeedback: Boolean = true,
    buzzerFeedback: Boolean = true,
    vibrationFeedback: Boolean = true
) : BaseCommand(
    code,
    parameters,
    sendFeedback,
    ledFeedback,
    buzzerFeedback,
    vibrationFeedback
) {

    val completer = CompletableDeferred<CommandResponse>()

    protected lateinit var parsedData: String
    val data: String
        get() = parsedData

    var retried = false

    init {
        buildParsedData()
    }

    protected open fun buildParsedData() {
        val buffer = StringBuilder()
        buffer.append(code)

        for (parameter in parameters) {
            buffer.addCommand(parameter)
        }
        parsedData = buffer.toString()
    }
}
