package com.opticon.opticonnect.sdk.internal.scanner_settings

import android.content.Context
import com.opticon.opticonnect.sdk.api.constants.commands.IndicatorCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.GoodReadLedDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.VibratorDuration
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.BooleanCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.EnumCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.IndicatorSettingDescriptors
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.ParameterSetting
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.DataWizardHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class IndicatorSettingsStateTest {

    private val settingsHandler = FakeSettingsHandler()
    private val stateStore = ScannerSettingsStateStore(settingsHandler)
    private val indicator = IndicatorImpl(stateStore)

    @Test
    fun gettersDecodeFetchedSettingsString() {
        seedFromSettingsString(
            buildString {
                append(IndicatorCommands.BUZZER_DISABLED)
                append(IndicatorCommands.PERSISTENT_SET_BUZZER)
                append("Q3")
                append("Q7")
                append(IndicatorCommands.LOW_HIGH_BUZZER)
                append(IndicatorCommands.BUZZER_DURATION_75_MS)
                append(IndicatorCommands.VIBRATOR_DISABLED)
                append(IndicatorCommands.VIBRATOR_DURATION_50_MS)
                append(IndicatorCommands.PERSISTENT_SET_LED)
                append("Q0")
                append("\$A")
                append("Q1")
                append("Q4")
                append("Q1")
                append("\$E")
                append(IndicatorCommands.GOOD_READ_LED_DURATION_800_MS)
            }
        )

        assertFalse(indicator.isBuzzerEnabled(TEST_DEVICE_ID))
        assertEquals(37, indicator.getBuzzerVolume(TEST_DEVICE_ID))
        assertEquals(BuzzerType.LOW_HIGH_BUZZER, indicator.getBuzzerType(TEST_DEVICE_ID))
        assertEquals(BuzzerDuration.DURATION_75_MS, indicator.getBuzzerDuration(TEST_DEVICE_ID))
        assertFalse(indicator.isVibratorEnabled(TEST_DEVICE_ID))
        assertEquals(VibratorDuration.DURATION_50_MS, indicator.getVibratorDuration(TEST_DEVICE_ID))
        assertEquals(LEDColor(10, 20, 30), indicator.getLED(TEST_DEVICE_ID))
        assertEquals(GoodReadLedDuration.DURATION_800_MS, indicator.getGoodReadLedDuration(TEST_DEVICE_ID))
    }

    @Test
    fun gettersUseDefaults() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        assertTrue(indicator.isBuzzerEnabled(TEST_DEVICE_ID))
        assertEquals(100, indicator.getBuzzerVolume(TEST_DEVICE_ID))
        assertEquals(BuzzerType.HIGH_LOW_BUZZER, indicator.getBuzzerType(TEST_DEVICE_ID))
        assertEquals(BuzzerDuration.DURATION_200_MS, indicator.getBuzzerDuration(TEST_DEVICE_ID))
        assertTrue(indicator.isVibratorEnabled(TEST_DEVICE_ID))
        assertEquals(VibratorDuration.DURATION_100_MS, indicator.getVibratorDuration(TEST_DEVICE_ID))
        assertEquals(LEDColor(0, 0, 0), indicator.getLED(TEST_DEVICE_ID))
        assertEquals(GoodReadLedDuration.DURATION_200_MS, indicator.getGoodReadLedDuration(TEST_DEVICE_ID))
    }

    @Test
    fun applyCommandUpdatesGetters() {
        stateStore.replaceSettings(
            TEST_DEVICE_ID,
            listOf(
                CommandData(IndicatorCommands.HIGH_LOW_BUZZER),
                CommandData(IndicatorCommands.BUZZER_DURATION_200_MS),
                CommandData(IndicatorCommands.PERSISTENT_SET_BUZZER, mutableListOf("Q1", "Q0", "Q0"))
            )
        )

        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(IndicatorCommands.SINGLE_TONE_BUZZER))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(IndicatorCommands.BUZZER_DURATION_400_MS))
        stateStore.applyCommand(
            TEST_DEVICE_ID,
            CommandData(IndicatorCommands.PERSISTENT_SET_BUZZER, mutableListOf("Q1", "Q2"))
        )
        stateStore.applyCommand(
            TEST_DEVICE_ID,
            CommandData(
                IndicatorCommands.PERSISTENT_SET_LED,
                mutableListOf("Q0", "Q0", "\$F", "\$F", "Q0", "Q0")
            )
        )

        assertEquals(BuzzerType.SINGLE_TONE_BUZZER, indicator.getBuzzerType(TEST_DEVICE_ID))
        assertEquals(BuzzerDuration.DURATION_400_MS, indicator.getBuzzerDuration(TEST_DEVICE_ID))
        assertEquals(12, indicator.getBuzzerVolume(TEST_DEVICE_ID))
        assertEquals(LEDColor(0, 255, 0), indicator.getLED(TEST_DEVICE_ID))
    }

    @Test
    fun descriptorsEncodeDirectInputParameters() {
        assertEquals(
            listOf("Q3", "Q7"),
            IndicatorSettingDescriptors.buzzerVolume.parametersFor(37)
        )
        assertEquals(
            listOf("Q0", "Q0", "\$F", "\$F", "Q0", "Q0"),
            IndicatorSettingDescriptors.ledColor.parametersFor(LEDColor(0, 255, 0))
        )
    }

    @Test
    fun getterRequiresInitializedState() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            indicator.getBuzzerType(TEST_DEVICE_ID)
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
            add(IndicatorSettingDescriptors.buzzerEnabled)
            add(IndicatorSettingDescriptors.buzzerVolume)
            add(IndicatorSettingDescriptors.buzzerType)
            add(IndicatorSettingDescriptors.buzzerDuration)
            add(IndicatorSettingDescriptors.vibratorEnabled)
            add(IndicatorSettingDescriptors.vibratorDuration)
            add(IndicatorSettingDescriptors.ledColor)
            add(IndicatorSettingDescriptors.goodReadLedDuration)
        }

        private val codesByGroup = groupsByCode.entries
            .flatMap { entry -> entry.value.map { group -> group to entry.key } }
            .groupBy({ it.first }, { it.second })

        override fun initialize(context: Context, closeDB: Boolean) = Unit

        override fun isDefaultCode(code: String): Boolean = false

        override fun isDirectInputKey(code: String): Boolean {
            return code.length == 2 &&
                    ((code.startsWith("Q") && code[1].isDigit()) ||
                            (code.startsWith("$") && code[1] in 'A'..'Z') ||
                            (code.startsWith("0") && code[1] in 'A'..'Z'))
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

        private fun MutableMap<String, List<String>>.add(setting: BooleanCommandSetting) {
            put(normalizeCode(setting.enabledCommand), listOf(setting.group))
            put(normalizeCode(setting.disabledCommand), listOf(setting.group))
        }

        private fun <T> MutableMap<String, List<String>>.add(setting: EnumCommandSetting<T>) {
            setting.commandsByValue.values.forEach { command ->
                put(normalizeCode(command), listOf(setting.group))
            }
        }

        private fun <T> MutableMap<String, List<String>>.add(setting: ParameterSetting<T>) {
            put(normalizeCode(setting.command), listOf(setting.group))
        }
    }

    private companion object {
        private const val TEST_DEVICE_ID = "00:11:22:33:44:55"
    }
}
