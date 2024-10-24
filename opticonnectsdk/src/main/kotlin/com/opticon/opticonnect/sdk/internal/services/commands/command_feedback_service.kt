package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.SingleLetterCommands
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.internal.entities.Command
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CommandFeedbackService @Inject constructor(private val scannerFeedback: ScannerFeedback) {
    fun generateFeedbackCommands(
        sendAckFeedback: Boolean,
        sendNakFeedback: Boolean,
        command: Command
    ): List<Command> {
        if (!command.sendFeedback) {
            return emptyList()
        }

        val feedbackCommands = mutableListOf<Command>()

        if (sendAckFeedback && (command.buzzerFeedback ?: scannerFeedback.buzzer)) {
            feedbackCommands.add(Command(SingleLetterCommands.GOOD_READ_BUZZER, sendFeedback = false))
        }

        if (sendAckFeedback && (command.ledFeedback ?: scannerFeedback.led)) {
            feedbackCommands.add(Command(SingleLetterCommands.GOOD_READ_LED, sendFeedback = false))
        }

        if ((sendNakFeedback || sendAckFeedback) && (command.vibrationFeedback ?: scannerFeedback.vibration)) {
            feedbackCommands.add(Command(SingleLetterCommands.WORK_VIBRATION, sendFeedback = false))
        }

        if (sendNakFeedback && (command.buzzerFeedback ?: scannerFeedback.buzzer)) {
            feedbackCommands.add(Command(SingleLetterCommands.BAD_READ_BUZZER, sendFeedback = false))
        }

        if (sendNakFeedback && (command.ledFeedback ?: scannerFeedback.led)) {
            feedbackCommands.add(Command(SingleLetterCommands.BAD_READ_LED, sendFeedback = false))
        }

        return feedbackCommands
    }
}
