package com.opticon.opticonnect.sdk.internal.services.commands.interfaces

import com.opticon.opticonnect.sdk.internal.entities.Command

interface CommandSender {
    fun sendCommand(command: Command)
}
