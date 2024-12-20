package com.opticon.opticonnect.sdk.internal.services.commands.interfaces

import com.opticon.opticonnect.sdk.internal.entities.Command

internal interface CommandBytesProvider {
    fun getCommandBytes(command: Command): ByteArray
}