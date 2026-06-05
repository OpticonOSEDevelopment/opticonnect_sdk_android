package com.opticon.opticonnect.sdk.internal.scanner_settings

import android.content.Context
import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandData
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.*
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.*
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.BooleanCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.EnumCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.*
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CodeSpecificSettingsStateTest {

    private val settingsHandler = FakeSettingsHandler()
    private val stateStore = ScannerSettingsStateStore(settingsHandler)

    private val codabar = CodabarImpl(stateStore)
    private val code39 = Code39Impl(stateStore)
    private val code128 = Code128AndGS1128Impl(stateStore)
    private val ean13 = EAN13Impl(stateStore)
    private val msiPlessey = MSIPlesseyImpl(stateStore)
    private val upcE = UPCEImpl(stateStore)

    @Test
    fun gettersUseDefaults() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        assertEquals(CodabarMode.NORMAL, codabar.getMode(TEST_DEVICE_ID))
        assertFalse(codabar.isCheckCDEnabled(TEST_DEVICE_ID))
        assertTrue(codabar.isTransmitCDEnabled(TEST_DEVICE_ID))
        assertEquals(CodabarMinimumLength.FIVE_CHARACTERS, codabar.getMinimumLength(TEST_DEVICE_ID))

        assertEquals(Code39Mode.NORMAL, code39.getMode(TEST_DEVICE_ID))
        assertFalse(code39.isCheckCDEnabled(TEST_DEVICE_ID))
        assertTrue(code39.isTransmitCDEnabled(TEST_DEVICE_ID))
        assertEquals(Code39MinimumLength.ONE_DIGIT, code39.getMinLength(TEST_DEVICE_ID))

        assertEquals(Code128AndGS1128Mode.DISABLE_GS1_128, code128.getGS1128Mode(TEST_DEVICE_ID))
        assertFalse(code128.isConcatenationEnabled(TEST_DEVICE_ID))
        assertTrue(code128.isLeadingC1OutputEnabled(TEST_DEVICE_ID))

        assertTrue(ean13.isTransmitCDEnabled(TEST_DEVICE_ID))
        assertFalse(ean13.isAddOnPlus2Enabled(TEST_DEVICE_ID))
        assertFalse(ean13.isAddOnPlus5Enabled(TEST_DEVICE_ID))

        assertEquals(MSIPlesseyCheckCDSettings.CHECK_1_CD_MOD10, msiPlessey.getCheckCD(TEST_DEVICE_ID))
        assertEquals(MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1, msiPlessey.getCDTransmission(TEST_DEVICE_ID))

        assertEquals(UPCEConversionMode.TRANSMIT_AS_IS, upcE.getConversionMode(TEST_DEVICE_ID))
        assertFalse(upcE.isAddOnPlus2Enabled(TEST_DEVICE_ID))
    }

    @Test
    fun applyCommandUpdatesGetters() {
        stateStore.replaceSettings(TEST_DEVICE_ID, emptyList())

        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.CODABAR_CX_CODE_ONLY))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.CODABAR_CHECK_CD))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.CODE_39_MIN_LENGTH_3_DIGITS))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.CODE_128_ENABLE_GS1_128_IF_POSSIBLE))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(SymbologyCommands.ENABLE_EAN_13_PLUS_2))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10))
        stateStore.applyCommand(TEST_DEVICE_ID, CommandData(CodeSpecificCommands.UPC_E_TRANSMIT_AS_UPC_A))

        assertEquals(CodabarMode.CX_CODE_ONLY, codabar.getMode(TEST_DEVICE_ID))
        assertTrue(codabar.isCheckCDEnabled(TEST_DEVICE_ID))
        assertEquals(Code39MinimumLength.THREE_DIGITS, code39.getMinLength(TEST_DEVICE_ID))
        assertEquals(Code128AndGS1128Mode.ENABLE_GS1_128_IF_POSSIBLE, code128.getGS1128Mode(TEST_DEVICE_ID))
        assertTrue(ean13.isAddOnPlus2Enabled(TEST_DEVICE_ID))
        assertEquals(MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD11_MOD10, msiPlessey.getCheckCD(TEST_DEVICE_ID))
        assertEquals(UPCEConversionMode.TRANSMIT_AS_UPC_A, upcE.getConversionMode(TEST_DEVICE_ID))
    }

    private class FakeSettingsHandler : SettingsHandler {
        private val groupsByCode = buildMap {
            add(CodabarSettingDescriptors.mode)
            add(CodabarSettingDescriptors.checkCD)
            add(CodabarSettingDescriptors.transmitCD)
            add(CodabarSettingDescriptors.spaceInsertion)
            add(CodabarSettingDescriptors.minimumLength)
            add(CodabarSettingDescriptors.intercharacterGapCheck)
            add(CodabarSettingDescriptors.startStopTransmission)

            add(Code11SettingDescriptors.checkCD)
            add(Code11SettingDescriptors.transmitCD)
            add(Code128AndGS1128SettingDescriptors.gs1128Mode)
            add(Code128AndGS1128SettingDescriptors.gs1128ConversionMode)
            add(Code128AndGS1128SettingDescriptors.concatenation)
            add(Code128AndGS1128SettingDescriptors.leadingC1Output)
            add(Code2Of5AndSCodeSettingDescriptors.minimumDataLength)
            add(Code2Of5AndSCodeSettingDescriptors.spaceCheck)
            add(Code2Of5AndSCodeSettingDescriptors.sCodeTransmissionAsInterleaved)
            add(Code39SettingDescriptors.mode)
            add(Code39SettingDescriptors.minimumLength)
            add(Code39SettingDescriptors.checkCD)
            add(Code39SettingDescriptors.transmitCD)
            add(Code39SettingDescriptors.transmitSTSP)
            add(Code39SettingDescriptors.concatenation)
            add(Code39SettingDescriptors.transmitLdAForItPharm)
            add(Code93SettingDescriptors.checkCD)
            add(Code93SettingDescriptors.concatenation)
            add(CompositeCodesSettingDescriptors.outputMode)
            add(CompositeCodesSettingDescriptors.ignoreLinkFlag)
            add(CompositeCodesSettingDescriptors.compositeGS1DatabarGS1128)
            add(CompositeCodesSettingDescriptors.compositeEANUPC)
            add(EAN8SettingDescriptors.transmitCD)
            add(EAN8SettingDescriptors.addOnPlus2)
            add(EAN8SettingDescriptors.addOnPlus5)
            add(EAN13SettingDescriptors.transmitCD)
            add(EAN13SettingDescriptors.addOnPlus2)
            add(EAN13SettingDescriptors.addOnPlus5)
            add(GS1DatabarSettingDescriptors.transmitCD)
            add(GS1DatabarSettingDescriptors.transmitAI)
            add(IATASettingDescriptors.checkCD)
            add(IATASettingDescriptors.transmitCD)
            add(KoreanPostalAuthoritySettingDescriptors.transmitCD)
            add(KoreanPostalAuthoritySettingDescriptors.transmitDash)
            add(KoreanPostalAuthoritySettingDescriptors.orientationUpsideDown)
            add(KoreanPostalAuthoritySettingDescriptors.upsideDownReading)
            add(MSIPlesseySettingDescriptors.checkCD)
            add(MSIPlesseySettingDescriptors.cdTransmission)
            add(TelepenSettingDescriptors.mode)
            add(UPCASettingDescriptors.leadingZeroAndTransmitCDMode)
            add(UPCASettingDescriptors.addOnPlus2)
            add(UPCASettingDescriptors.addOnPlus5)
            add(UPCESettingDescriptors.leadingZeroAndTransmitCDMode)
            add(UPCESettingDescriptors.conversionMode)
            add(UPCESettingDescriptors.addOnPlus2)
            add(UPCESettingDescriptors.addOnPlus5)
            add(UPCE1SettingDescriptors.addOnPlus2)
            add(UPCE1SettingDescriptors.addOnPlus5)
            add(UKPlesseySettingDescriptors.transmitCDs)
            add(UKPlesseySettingDescriptors.spaceInsertion)
            add(UKPlesseySettingDescriptors.aToXConversion)
        }

        private val codesByGroup = groupsByCode.entries
            .flatMap { entry -> entry.value.map { group -> group to entry.key } }
            .groupBy({ it.first }, { it.second })

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
    }

    private companion object {
        private const val TEST_DEVICE_ID = "00:11:22:33:44:55"
    }
}
