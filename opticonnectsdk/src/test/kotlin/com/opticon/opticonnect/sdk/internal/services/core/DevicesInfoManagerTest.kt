package com.opticon.opticonnect.sdk.internal.services.core

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.internal.entities.Command
import com.opticon.opticonnect.sdk.internal.entities.CommandPacket
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorFactory
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import com.opticon.opticonnect.sdk.internal.services.commands.CommandFactory
import com.opticon.opticonnect.sdk.internal.services.commands.CommandFeedbackService
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.DataWizardHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertNull
import org.junit.Test

class DevicesInfoManagerTest {

    @Test
    fun getInfoReturnsNullWhenDeviceInfoHasNotBeenCached() {
        val manager = DevicesInfoManager(testCommandExecutorsManager())

        assertNull(manager.getInfo("38:89:DC:0E:00:0F"))
    }

    private fun testCommandExecutorsManager(): CommandExecutorsManager {
        return CommandExecutorsManager(
            commandExecutorFactory = CommandExecutorFactory(
                bleDataWriter = NoOpBleDataWriter(),
                bleCommandResponseReader = EmptyBleCommandResponseReader(),
                commandBytesProvider = EchoCommandBytesProvider(),
                commandFeedbackService = CommandFeedbackService(DisabledScannerFeedback())
            ),
            commandFactory = CommandFactory(),
            settingsCompressor = SettingsCompressor(NoOpSettingsHandler(), DataWizardHelper())
        )
    }

    private class NoOpBleDataWriter : BleDataWriter {
        override suspend fun writeData(deviceId: String, data: String, dataBytes: ByteArray) = Unit
    }

    private class EmptyBleCommandResponseReader : BleCommandResponseReader {
        override suspend fun getCommandResponseStream(deviceId: String): Flow<com.opticon.opticonnect.sdk.internal.entities.CommandResponsePacket> =
            emptyFlow()
    }

    private class EchoCommandBytesProvider : CommandBytesProvider {
        override fun getCommandPacket(command: Command): CommandPacket {
            return CommandPacket(command.data.toByteArray(Charsets.UTF_8), sequenceNumber = 0)
        }
    }

    private class DisabledScannerFeedback : ScannerFeedback {
        override val led = false
        override val buzzer = false
        override val vibration = false

        override fun set(led: Boolean?, buzzer: Boolean?, vibration: Boolean?) = Unit
    }

    private class NoOpSettingsHandler : SettingsHandler {
        override fun initialize(context: Context, closeDB: Boolean) = Unit

        override fun isDirectInputKey(code: String): Boolean = false

        override fun isDefaultCode(code: String): Boolean = false

        override fun normalizeCode(code: String): String = code

        override fun getGroupsToDisableForCode(code: String): List<String> = emptyList()

        override fun getGroupsForCode(code: String): List<String> = emptyList()

        override fun applyCommandToSettings(
            settings: MutableMap<String, List<String>>,
            commandData: CommandData
        ) = Unit

        override fun addCommandToCompressedList(
            commandData: CommandData,
            compressedList: MutableList<CommandData>
        ) {
            compressedList.add(commandData)
        }
    }
}
