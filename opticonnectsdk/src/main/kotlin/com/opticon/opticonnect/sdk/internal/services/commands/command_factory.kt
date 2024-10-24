package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.internal.entities.Command
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CommandFactory @Inject constructor() {
    fun createCommand(scannerCommand: ScannerCommand): Command {
        return Command(
            code = scannerCommand.code,
            parameters = scannerCommand.parameters,
            sendFeedback = scannerCommand.sendFeedback,
            ledFeedback = scannerCommand.ledFeedback,
            buzzerFeedback = scannerCommand.buzzerFeedback,
            vibrationFeedback = scannerCommand.vibrationFeedback
        )
    }
}
