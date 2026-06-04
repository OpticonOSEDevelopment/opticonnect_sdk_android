package com.opticon.opticonnect.sdk.internal.services.commands.interfaces

import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.CommandPacket

internal interface CommandBytesProvider {
    fun getCommandPacket(command: Command): CommandPacket
}
