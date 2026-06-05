package com.opticon.opticonnect.sdk.internal.scanner_settings

import android.content.Context
import com.opticon.opticonnect.sdk.api.constants.commands.FormattingCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.enums.FormattableSymbology
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.services.core.DirectInputKeysHelperImpl
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FormattingSettingsStateTest {
    private val stateStore = ScannerSettingsStateStore(FakeSettingsHandler())
    private val formatting = FormattingImpl(DirectInputKeysHelperImpl(), stateStore)

    @Test
    fun gettersDecodeSeededFormattingSettings() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(
                CommandData(FormattingCommands.PREAMBLE, mutableListOf("Q1", "0A")),
                CommandData(FormattingCommands.PREFIX_ALL_CODES, mutableListOf("Q2", "\$B")),
                CommandData(FormattingCommands.SUFFIX_ALL_CODES, mutableListOf("7I")),
                CommandData(FormattingCommands.POSTAMBLE, mutableListOf("Q3", "5A"))
            )
        )

        assertEquals(
            listOf(DirectInputKey.DIGIT_1, DirectInputKey.LETTER_A),
            formatting.getPreamble(TEST_DEVICE_ID)
        )
        assertEquals(
            listOf(DirectInputKey.DIGIT_2, DirectInputKey.LETTER_B_LOWER),
            formatting.getPrefix(TEST_DEVICE_ID)
        )
        assertEquals(
            listOf(DirectInputKey.RETURN_KEY),
            formatting.getSuffix(TEST_DEVICE_ID)
        )
        assertEquals(
            listOf(DirectInputKey.DIGIT_3, DirectInputKey.SPACE),
            formatting.getPostamble(TEST_DEVICE_ID)
        )
    }

    @Test
    fun gettersReturnEmptyListWhenFormattingCommandIsMissing() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        assertEquals(emptyList<DirectInputKey>(), formatting.getPreamble(TEST_DEVICE_ID))
        assertEquals(emptyList<DirectInputKey>(), formatting.getPrefix(TEST_DEVICE_ID, FormattableSymbology.QR_CODE))
        assertEquals(emptyList<DirectInputKey>(), formatting.getSuffix(TEST_DEVICE_ID, FormattableSymbology.QR_CODE))
        assertEquals(emptyList<DirectInputKey>(), formatting.getPostamble(TEST_DEVICE_ID))
    }

    @Test
    fun getterReflectsRuntimeCommandOverride() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(CommandData(FormattingCommands.PREFIX_ALL_CODES, mutableListOf("Q2")))
        )

        stateStore.applyCommand(
            TEST_DEVICE_ID,
            CommandData(FormattingCommands.PREFIX_ALL_CODES, mutableListOf("Q4", "Q5"))
        )

        assertEquals(
            listOf(DirectInputKey.DIGIT_4, DirectInputKey.DIGIT_5),
            formatting.getPrefix(TEST_DEVICE_ID)
        )
    }

    @Test
    fun getterRequiresInitializedState() {
        assertThrows(IllegalArgumentException::class.java) {
            formatting.getPreamble(TEST_DEVICE_ID)
        }
    }

    private class FakeSettingsHandler : SettingsHandler {
        override fun initialize(context: Context, closeDB: Boolean) = Unit

        override fun isDirectInputKey(code: String): Boolean = false

        override fun isDefaultCode(code: String): Boolean = false

        override fun normalizeCode(code: String): String {
            return if (code.startsWith("[") || code.startsWith("]")) {
                code.substring(1)
            } else {
                code
            }
        }

        override fun getGroupsToDisableForCode(code: String): List<String> = getGroupsForCode(code)

        override fun getGroupsForCode(code: String): List<String> = listOf(normalizeCode(code))

        override fun applyCommandToSettings(
            settings: MutableMap<String, List<String>>,
            commandData: CommandData
        ) {
            settings[normalizeCode(commandData.command)] = commandData.parameters.toList()
        }

        override fun addCommandToCompressedList(
            commandData: CommandData,
            compressedList: MutableList<CommandData>
        ) {
            compressedList.removeAll { normalizeCode(it.command) == normalizeCode(commandData.command) }
            compressedList.add(
                CommandData(commandData.command, commandData.parameters.toMutableList())
            )
        }
    }

    private companion object {
        const val TEST_DEVICE_ID = "00:11:22:33:44:55"
    }
}
