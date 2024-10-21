package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.internal.helpers.TimeoutManager
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandExecutorFactory @Inject constructor(
    private val bleDataWriter: BleDataWriter,
    private val bleCommandResponseReader: BleCommandResponseReader,
    private val commandBytesProvider: CommandBytesProvider,
    private val commandFeedbackService: CommandFeedbackService
) {
    fun create(deviceId: String): CommandExecutor {
        return CommandExecutor(
            deviceId = deviceId,
            bleDataWriter = bleDataWriter,
            bleCommandResponseReader = bleCommandResponseReader,
            commandBytesProvider = commandBytesProvider,
            commandFeedbackService = commandFeedbackService,
            timeoutManager = TimeoutManager()
        )
    }
}
