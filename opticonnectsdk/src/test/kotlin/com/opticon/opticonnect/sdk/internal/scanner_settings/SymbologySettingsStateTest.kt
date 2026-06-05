package com.opticon.opticonnect.sdk.internal.scanner_settings

import android.content.Context
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.enums.SymbologyType
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.SymbologySettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SymbologySettingsStateTest {

    private val settingsHandler = FakeSettingsHandler()
    private val stateStore = ScannerSettingsStateStore(settingsHandler)
    private val symbology = SymbologyImpl(stateStore, settingsHandler)

    @Test
    fun gettersUseDatabaseDefaults() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.CODE_39))
        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.QR_CODE))
        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.UPC_A))
        assertFalse(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.UPC_A_ADD_ON_2))
    }

    @Test
    fun explicitSettingsOverrideDefaults() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(
                CommandData(SymbologyCommands.DISABLE_CODE_39),
                CommandData(SymbologyCommands.ENABLE_UPC_A_PLUS_2)
            )
        )

        assertFalse(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.CODE_39))
        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.UPC_A_ADD_ON_2))
    }

    @Test
    fun enableOnlyCommandEnablesOnlySelectedSymbology() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(CommandData(SymbologyCommands.ENABLE_QR_CODE_ONLY))
        )

        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.QR_CODE))
        assertFalse(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.CODE_39))
        assertFalse(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.DATA_MATRIX))
    }

    @Test
    fun applyCommandUpdatesGetter() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(SymbologyCommands.DISABLE_QR_CODE))
        assertFalse(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.QR_CODE))

        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(SymbologyCommands.ENABLE_QR_CODE))
        assertTrue(symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.QR_CODE))
    }

    @Test
    fun getterRequiresInitializedState() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            symbology.isSymbologyEnabled(TEST_DEVICE_ID, SymbologyType.QR_CODE)
        }

        assertTrue(exception.message!!.contains("Settings state for device $TEST_DEVICE_ID is not initialized"))
    }

    private class FakeSettingsHandler : SettingsHandler {
        private val defaultCodes = setOf(
            SymbologyCommands.ENABLE_CODE_39,
            SymbologyCommands.ENABLE_QR_CODE,
            SymbologyCommands.ENABLE_UPC_A,
            SymbologyCommands.DISABLE_UPC_A_PLUS_2
        ).map(::normalizeCode).toSet()

        private val groupsByCode = buildMap {
            SymbologySettingDescriptors.symbology.enableCommandsByValue.forEach { (type, command) ->
                put(normalizeCode(command), listOf(type.name))
            }
            SymbologySettingDescriptors.symbology.disableCommandsByValue.forEach { (type, command) ->
                put(normalizeCode(command), listOf(type.name))
            }
            SymbologySettingDescriptors.symbology.enableOnlyCommandsByValue.forEach { (_, command) ->
                put(normalizeCode(command), listOf("enableOnly"))
            }
        }

        private val codesByGroup = groupsByCode.entries
            .flatMap { entry -> entry.value.map { group -> group to entry.key } }
            .groupBy({ it.first }, { it.second })

        override fun initialize(context: Context, closeDB: Boolean) = Unit

        override fun isDirectInputKey(code: String): Boolean = false

        override fun isDefaultCode(code: String): Boolean {
            return normalizeCode(code) in defaultCodes
        }

        override fun normalizeCode(code: String): String {
            return if (code.startsWith("[") || code.startsWith("]")) {
                code.substring(1)
            } else {
                code
            }
        }

        override fun getGroupsToDisableForCode(code: String): List<String> {
            return getGroupsForCode(code)
        }

        override fun getGroupsForCode(code: String): List<String> {
            return groupsByCode[normalizeCode(code)] ?: emptyList()
        }

        override fun applyCommandToSettings(
            settings: MutableMap<String, List<String>>,
            commandData: CommandData
        ) {
            val commandCode = normalizeCode(commandData.command)
            getGroupsToDisableForCode(commandCode).forEach { group ->
                codesByGroup[group]?.forEach { settings.remove(normalizeCode(it)) }
            }
            settings[commandCode] = commandData.parameters.toList()
        }

        override fun addCommandToCompressedList(
            commandData: CommandData,
            compressedList: MutableList<CommandData>
        ) {
            val commandCode = normalizeCode(commandData.command)
            val groupsToDisable = getGroupsToDisableForCode(commandCode)
            compressedList.removeAll { existing ->
                normalizeCode(existing.command) == commandCode ||
                        getGroupsForCode(existing.command).any { it in groupsToDisable }
            }
            compressedList.add(commandData)
        }
    }

    private companion object {
        private const val TEST_DEVICE_ID = "00:11:22:33:44:55"
    }
}
