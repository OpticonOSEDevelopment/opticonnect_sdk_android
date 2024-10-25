package com.opticon.opticonnect.sdk.internal.services.commands.interfaces

import com.opticon.opticonnect.sdk.internal.entities.Command

internal interface CommandSender {
    fun sendCommand(command: Command)
}
