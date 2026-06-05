package com.opticon.opticonnect.sdk.internal.scanner_settings

import android.content.Context
import com.opticon.opticonnect.sdk.api.constants.commands.ScanCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.PositiveAndNegativeBarcodesMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.BooleanCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.EnumCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.ReadOptionsSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.DataWizardHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadOptionsSettingsStateTest {

    private val settingsHandler = FakeSettingsHandler()
    private val stateStore = ScannerSettingsStateStore(settingsHandler)
    private val readOptions = ReadOptionsImpl(stateStore)

    @Test
    fun gettersDecodeFetchedSettingsString() {
        seedFromSettingsString(
            buildString {
                append(ScanCommands.NEGATIVE_BARCODES)
                append(ScanCommands.MULTIPLE_READ)
                append(ScanCommands.READ_TIME_8_SECONDS)
                append(ScanCommands.ALTERNATING_FLOODLIGHT)
                append(ScanCommands.AIMING_DISABLED)
                append(ScanCommands.TRIGGER_REPEAT_ENABLED)
                append(ScanCommands.DELETE_KEY_DISABLED)
            }
        )

        assertEquals(
            PositiveAndNegativeBarcodesMode.NEGATIVE_BARCODES,
            readOptions.getPositiveAndNegativeBarcodesMode(TEST_DEVICE_ID)
        )
        assertEquals(ReadMode.MULTIPLE_READ, readOptions.getReadMode(TEST_DEVICE_ID))
        assertEquals(ReadTime.EIGHT_SECONDS, readOptions.getReadTime(TEST_DEVICE_ID))
        assertEquals(IlluminationMode.ALTERNATING_FLOODLIGHT, readOptions.getIlluminationMode(TEST_DEVICE_ID))
        assertFalse(readOptions.isAimingEnabled(TEST_DEVICE_ID))
        assertTrue(readOptions.isTriggerRepeatEnabled(TEST_DEVICE_ID))
        assertFalse(readOptions.isDeleteKeyEnabled(TEST_DEVICE_ID))
    }

    @Test
    fun gettersUseDefaults() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        assertEquals(
            PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES,
            readOptions.getPositiveAndNegativeBarcodesMode(TEST_DEVICE_ID)
        )
        assertEquals(ReadMode.SINGLE_READ, readOptions.getReadMode(TEST_DEVICE_ID))
        assertEquals(ReadTime.TWO_SECONDS, readOptions.getReadTime(TEST_DEVICE_ID))
        assertEquals(IlluminationMode.ENABLE_FLOODLIGHT, readOptions.getIlluminationMode(TEST_DEVICE_ID))
        assertTrue(readOptions.isAimingEnabled(TEST_DEVICE_ID))
        assertFalse(readOptions.isTriggerRepeatEnabled(TEST_DEVICE_ID))
        assertTrue(readOptions.isDeleteKeyEnabled(TEST_DEVICE_ID))
    }

    @Test
    fun applyCommandUpdatesGetters() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(
                CommandData(ScanCommands.POSITIVE_BARCODES),
                CommandData(ScanCommands.SINGLE_READ),
                CommandData(ScanCommands.READ_TIME_2_SECONDS),
                CommandData(ScanCommands.ENABLE_FLOODLIGHT),
                CommandData(ScanCommands.AIMING_ENABLED),
                CommandData(ScanCommands.TRIGGER_REPEAT_DISABLED),
                CommandData(ScanCommands.DELETE_KEY_ENABLED)
            )
        )

        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.POSITIVE_AND_NEGATIVE_BARCODES))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.CONTINUOUS_READ))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.READ_TIME_INDEFINITELY))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.PREVENT_SPECULAR_REFLECTION))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.AIMING_DISABLED))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.TRIGGER_REPEAT_ENABLED))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(ScanCommands.DELETE_KEY_DISABLED))

        assertEquals(
            PositiveAndNegativeBarcodesMode.POSITIVE_AND_NEGATIVE_BARCODES,
            readOptions.getPositiveAndNegativeBarcodesMode(TEST_DEVICE_ID)
        )
        assertEquals(ReadMode.CONTINUOUS_READ, readOptions.getReadMode(TEST_DEVICE_ID))
        assertEquals(ReadTime.INDEFINITE, readOptions.getReadTime(TEST_DEVICE_ID))
        assertEquals(IlluminationMode.PREVENT_SPECULAR_REFLECTION, readOptions.getIlluminationMode(TEST_DEVICE_ID))
        assertFalse(readOptions.isAimingEnabled(TEST_DEVICE_ID))
        assertTrue(readOptions.isTriggerRepeatEnabled(TEST_DEVICE_ID))
        assertFalse(readOptions.isDeleteKeyEnabled(TEST_DEVICE_ID))
    }

    @Test
    fun getterRequiresInitializedState() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            readOptions.getReadMode(TEST_DEVICE_ID)
        }

        assertTrue(exception.message!!.contains("Settings state for device $TEST_DEVICE_ID is not initialized"))
    }

    private fun seedFromSettingsString(settings: String) {
        val commandData = SettingsCompressor(settingsHandler, DataWizardHelper())
            .getCompressedSettingsList(settings)

        stateStore.replaceSettings(TEST_DEVICE_ID, commandData)
    }

    private class FakeSettingsHandler : SettingsHandler {
        private val groupsByCode = buildMap {
            add(ReadOptionsSettingDescriptors.positiveAndNegativeBarcodesMode)
            add(ReadOptionsSettingDescriptors.readMode)
            add(ReadOptionsSettingDescriptors.readTime)
            add(ReadOptionsSettingDescriptors.illuminationMode)
            add(ReadOptionsSettingDescriptors.aiming)
            add(ReadOptionsSettingDescriptors.triggerRepeat)
            add(ReadOptionsSettingDescriptors.deleteKey)
        }

        private val codesByGroup = groupsByCode.entries
            .flatMap { entry -> entry.value.map { group -> group to entry.key } }
            .groupBy({ it.first }, { it.second })

        override fun initialize(context: Context, closeDB: Boolean) = Unit

        override fun isDefaultCode(code: String): Boolean = false

        override fun isDirectInputKey(code: String): Boolean = false

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

        private fun <T> MutableMap<String, List<String>>.add(setting: EnumCommandSetting<T>) {
            setting.commandsByValue.values.forEach { command ->
                put(normalizeCode(command), listOf(setting.group))
            }
        }

        private fun MutableMap<String, List<String>>.add(setting: BooleanCommandSetting) {
            put(normalizeCode(setting.enabledCommand), listOf(setting.group))
            put(normalizeCode(setting.disabledCommand), listOf(setting.group))
        }
    }

    private companion object {
        private const val TEST_DEVICE_ID = "00:11:22:33:44:55"
    }
}
