package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.BAD_READ_BUZZER
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.BAD_READ_LED
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.GOOD_READ_BUZZER
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.GOOD_READ_LED
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.WORK_VIBRATION
import com.opticon.opticonnect.sdk.internal.entities.Command
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandFeedbackService @Inject constructor() {

    fun generateFeedbackCommands(
        sendAckFeedback: Boolean,
        sendNakFeedback: Boolean,
        command: Command
    ): List<Command> {
        if (!command.sendFeedback) {
            return emptyList()
        }

        val feedbackCommands = mutableListOf<Command>()

        if (sendAckFeedback && command.buzzerFeedback) {
            feedbackCommands.add(Command(GOOD_READ_BUZZER, sendFeedback = false))
        }

        if (sendAckFeedback && command.ledFeedback) {
            feedbackCommands.add(Command(GOOD_READ_LED, sendFeedback = false))
        }

        if ((sendNakFeedback || sendAckFeedback) && command.vibrationFeedback) {
            feedbackCommands.add(Command(WORK_VIBRATION, sendFeedback = false))
        }

        if (sendNakFeedback && command.buzzerFeedback) {
            feedbackCommands.add(Command(BAD_READ_BUZZER, sendFeedback = false))
        }

        if (sendNakFeedback && command.ledFeedback) {
            feedbackCommands.add(Command(BAD_READ_LED, sendFeedback = false))
        }

        return feedbackCommands
    }
}