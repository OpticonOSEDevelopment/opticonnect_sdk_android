package com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific

import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarStartStopTransmission
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code11CheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code128AndGS1128Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39MinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.Code39Mode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CompositeCodesOutputMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.DataLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.GS1128ConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.IATACheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCDTransmissionSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.MSIPlesseyCheckCDSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.TelepenMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCALeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCEConversionMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCELeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.BooleanCommandSetting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.EnumCommandSetting

internal object CodabarSettingDescriptors {
    val mode = EnumCommandSetting(
        group = "codabarCodeMode",
        defaultValue = CodabarMode.NORMAL,
        commandsByValue = mapOf(
            CodabarMode.NORMAL to CodeSpecificCommands.CODABAR_NORMAL,
            CodabarMode.ABC_CODE_ONLY to CodeSpecificCommands.CODABAR_ABC_CODE_ONLY,
            CodabarMode.CX_CODE_ONLY to CodeSpecificCommands.CODABAR_CX_CODE_ONLY,
            CodabarMode.CODABAR_ABC_AND_CX to CodeSpecificCommands.CODABAR_ABC_AND_CX
        )
    )

    val startStopTransmission = EnumCommandSetting(
        group = "codabarStSp",
        defaultValue = CodabarStartStopTransmission.DO_NOT_TRANSMIT_START_STOP,
        commandsByValue = mapOf(
            CodabarStartStopTransmission.DO_NOT_TRANSMIT_START_STOP to CodeSpecificCommands.CODABAR_DO_NOT_TRANSMIT_ST_SP,
            CodabarStartStopTransmission.START_STOP_ABCD_ABCD to CodeSpecificCommands.CODABAR_ST_SP_ABCD_ABCD,
            CodabarStartStopTransmission.START_STOP_ABCD_ABCD_LOWER to CodeSpecificCommands.CODABAR_ST_SP_ABCD_ABCD_LOWERCASE,
            CodabarStartStopTransmission.START_STOP_ABCD_TNX_E to CodeSpecificCommands.CODABAR_ST_SP_ABCD_TNE,
            CodabarStartStopTransmission.START_STOP_ABCD_TNX_E_LOWER to CodeSpecificCommands.CODABAR_ST_SP_ABCD_TNE_LOWERCASE,
            CodabarStartStopTransmission.START_STOP_DC1_DC2_DC3_DC4 to CodeSpecificCommands.CODABAR_ST_SP_DC1_DC2_DC3_DC4
        )
    )

    val minimumLength = EnumCommandSetting(
        group = "codabarMinimumLength",
        defaultValue = CodabarMinimumLength.FIVE_CHARACTERS,
        commandsByValue = mapOf(
            CodabarMinimumLength.ONE_CHARACTER to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_ONE_CHAR,
            CodabarMinimumLength.THREE_CHARACTERS to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_THREE_CHARS,
            CodabarMinimumLength.FIVE_CHARACTERS to CodeSpecificCommands.CODABAR_MINIMUM_LENGTH_FIVE_CHARS
        )
    )

    val checkCD = BooleanCommandSetting(
        group = "codabarCheckCd",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODABAR_CHECK_CD,
        disabledCommand = CodeSpecificCommands.CODABAR_DO_NOT_CHECK_CD
    )

    val transmitCD = BooleanCommandSetting(
        group = "codabarTransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.CODABAR_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.CODABAR_DO_NOT_TRANSMIT_CD
    )

    val spaceInsertion = BooleanCommandSetting(
        group = "codabarSpaceInsertion",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODABAR_ENABLE_SPACE_INSERTION,
        disabledCommand = CodeSpecificCommands.CODABAR_DISABLE_SPACE_INSERTION
    )

    val intercharacterGapCheck = BooleanCommandSetting(
        group = "codabarIntercharacterGapCheck",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.CODABAR_ENABLE_INTERCHARACTER_GAP_CHECK,
        disabledCommand = CodeSpecificCommands.CODABAR_DISABLE_INTERCHARACTER_GAP_CHECK
    )
}

internal object Code11SettingDescriptors {
    val checkCD = EnumCommandSetting(
        group = "code11CheckCd",
        defaultValue = Code11CheckCDSettings.CHECK_1_CD_OR_2_CDS_AUTOMATICALLY,
        commandsByValue = mapOf(
            Code11CheckCDSettings.DO_NOT_CHECK to CodeSpecificCommands.CODE_11_DO_NOT_CHECK_CD,
            Code11CheckCDSettings.CHECK_1_CD to CodeSpecificCommands.CODE_11_CHECK_1_CD,
            Code11CheckCDSettings.CHECK_2_CDS to CodeSpecificCommands.CODE_11_CHECK_2_CDS,
            Code11CheckCDSettings.CHECK_1_CD_OR_2_CDS_AUTOMATICALLY to CodeSpecificCommands.CODE_11_CHECK_1_OR_2_CDS
        )
    )

    val transmitCD = BooleanCommandSetting(
        group = "code11TransmitCd",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_11_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.CODE_11_DO_NOT_TRANSMIT_CD
    )
}

internal object Code128AndGS1128SettingDescriptors {
    val gs1128Mode = EnumCommandSetting(
        group = "code128GS1-128",
        defaultValue = Code128AndGS1128Mode.DISABLE_GS1_128,
        commandsByValue = mapOf(
            Code128AndGS1128Mode.DISABLE_GS1_128 to CodeSpecificCommands.CODE_128_DISABLE_GS1_128,
            Code128AndGS1128Mode.ENABLE_GS1_128_ONLY to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_ONLY,
            Code128AndGS1128Mode.ENABLE_GS1_128_IF_POSSIBLE to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_IF_POSSIBLE
        )
    )

    val gs1128ConversionMode = EnumCommandSetting(
        group = "code128GS1_128Conversion",
        defaultValue = GS1128ConversionMode.DISABLED,
        commandsByValue = mapOf(
            GS1128ConversionMode.DISABLED to CodeSpecificCommands.CODE_128_DISABLE_GS1_128_CONVERSION,
            GS1128ConversionMode.ENABLE_MODE_1 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_1,
            GS1128ConversionMode.ENABLE_MODE_2 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_2,
            GS1128ConversionMode.ENABLE_MODE_3 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_3,
            GS1128ConversionMode.ENABLE_MODE_4 to CodeSpecificCommands.CODE_128_ENABLE_GS1_128_CONVERSION_MODE_4
        )
    )

    val concatenation = BooleanCommandSetting(
        group = "code128Concatenation",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_128_ENABLE_CONCATENATION,
        disabledCommand = CodeSpecificCommands.CODE_128_DISABLE_CONCATENATION
    )

    val leadingC1Output = BooleanCommandSetting(
        group = "code128LeadingC1Output",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.CODE_128_ENABLE_LEADING_C1_OUTPUT,
        disabledCommand = CodeSpecificCommands.CODE_128_DISABLE_LEADING_C1_OUTPUT
    )
}

internal object Code2Of5AndSCodeSettingDescriptors {
    val minimumDataLength = EnumCommandSetting(
        group = "2of5andSCodeMinimumLength",
        defaultValue = DataLength.FIVE_CHARACTERS,
        commandsByValue = mapOf(
            DataLength.ONE_CHARACTER to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_ONE_CHAR,
            DataLength.THREE_CHARACTERS to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_THREE_CHARS,
            DataLength.FIVE_CHARACTERS to CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_FIVE_CHARS
        )
    )

    val spaceCheck = BooleanCommandSetting(
        group = "2of5andSCodeIndustrialSpaceCheck",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_ENABLE_SPACE_CHECK_INDUSTRIAL_2OF5,
        disabledCommand = CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_DISABLE_SPACE_CHECK_INDUSTRIAL_2OF5
    )

    val sCodeTransmissionAsInterleaved = BooleanCommandSetting(
        group = "2of5andSCodeInterleaved25",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_TRANSMIT_AS_INTERLEAVED_2OF5,
        disabledCommand = CodeSpecificCommands.TWO_OF_FIVE_AND_S_CODE_DO_NOT_TRANSMIT_AS_INTERLEAVED_2OF5
    )
}

internal object Code39SettingDescriptors {
    val mode = EnumCommandSetting(
        group = "code39Type",
        defaultValue = Code39Mode.NORMAL,
        commandsByValue = mapOf(
            Code39Mode.NORMAL to CodeSpecificCommands.NORMAL_CODE_39,
            Code39Mode.FULL_ASCII to CodeSpecificCommands.FULL_ASCII_CODE_39,
            Code39Mode.FULL_ASCII_IF_POSSIBLE to CodeSpecificCommands.FULL_ASCII_CODE_39_IF_POSSIBLE,
            Code39Mode.IT_PHARMACEUTICAL_ONLY to CodeSpecificCommands.IT_PHARMACEUTICAL_ONLY,
            Code39Mode.IT_PHARMACEUTICAL_IF_POSSIBLE to CodeSpecificCommands.IT_PHARMACEUTICAL_IF_POSSIBLE
        )
    )

    val minimumLength = EnumCommandSetting(
        group = "code39MinLength",
        defaultValue = Code39MinimumLength.ONE_DIGIT,
        commandsByValue = mapOf(
            Code39MinimumLength.ONE_DIGIT to CodeSpecificCommands.CODE_39_MIN_LENGTH_1_DIGIT,
            Code39MinimumLength.THREE_DIGITS to CodeSpecificCommands.CODE_39_MIN_LENGTH_3_DIGITS
        )
    )

    val checkCD = BooleanCommandSetting(
        group = "code39Cd",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_39_CHECK_CD,
        disabledCommand = CodeSpecificCommands.CODE_39_DO_NOT_CHECK_CD
    )

    val transmitCD = BooleanCommandSetting(
        group = "code39TransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.CODE_39_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_CD
    )

    val transmitSTSP = BooleanCommandSetting(
        group = "code39StSp",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_39_TRANSMIT_ST_SP,
        disabledCommand = CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_ST_SP
    )

    val concatenation = BooleanCommandSetting(
        group = "code39Concatenation",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_39_ENABLE_CONCATENATION,
        disabledCommand = CodeSpecificCommands.CODE_39_DISABLE_CONCATENATION
    )

    val transmitLdAForItPharm = BooleanCommandSetting(
        group = "code39ItPharmLeadingA",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_39_TRANSMIT_LEADING_A_FOR_IT_PHARM,
        disabledCommand = CodeSpecificCommands.CODE_39_DO_NOT_TRANSMIT_LEADING_A_FOR_IT_PHARM
    )
}

internal object CompositeCodesSettingDescriptors {
    val outputMode = EnumCommandSetting(
        group = "compositeOutputMode",
        defaultValue = CompositeCodesOutputMode.ONLY_1D_AND_2D_COMPONENTS_ALLOWED,
        commandsByValue = mapOf(
            CompositeCodesOutputMode.ONLY_1D_AND_2D_COMPONENTS_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_1D_AND_2D_COMPONENT,
            CompositeCodesOutputMode.ONLY_2D_COMPONENT_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_2D_COMPONENT,
            CompositeCodesOutputMode.ONLY_1D_COMPONENT_ALLOWED to CodeSpecificCommands.COMPOSITE_OUTPUT_1D_COMPONENT
        )
    )

    val ignoreLinkFlag = BooleanCommandSetting(
        group = "compositeIgnoreLinkFlag",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.COMPOSITE_IGNORE_LINK_FLAG,
        disabledCommand = CodeSpecificCommands.COMPOSITE_DO_NOT_IGNORE_LINK_FLAG
    )

    val compositeGS1DatabarGS1128 = BooleanCommandSetting(
        group = "compositeGs1Enable",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.ENABLE_COMPOSITE_GS1,
        disabledCommand = CodeSpecificCommands.DISABLE_COMPOSITE_GS1
    )

    val compositeEANUPC = BooleanCommandSetting(
        group = "compositeEanUpc",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.ENABLE_COMPOSITE_EAN_UPC,
        disabledCommand = CodeSpecificCommands.DISABLE_COMPOSITE_EAN_UPC
    )
}

internal object IATASettingDescriptors {
    val checkCD = EnumCommandSetting(
        group = "IATACd",
        defaultValue = IATACheckCDSettings.DO_NOT_CHECK_CD,
        commandsByValue = mapOf(
            IATACheckCDSettings.DO_NOT_CHECK_CD to CodeSpecificCommands.IATA_DO_NOT_CHECK_CD,
            IATACheckCDSettings.CHECK_FC_AND_SN_ONLY to CodeSpecificCommands.IATA_CHECK_FC_AND_SN_ONLY,
            IATACheckCDSettings.CHECK_CPN_FC_AND_SN to CodeSpecificCommands.IATA_CHECK_CPN_FC_AND_SN,
            IATACheckCDSettings.CHECK_CPN_AC_FC_AND_SN to CodeSpecificCommands.IATA_CHECK_CPN_AC_FC_AND_SN
        )
    )

    val transmitCD = BooleanCommandSetting(
        group = "IATATransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.IATA_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.IATA_DO_NOT_TRANSMIT_CD
    )
}

internal object Code93SettingDescriptors {
    val checkCD = BooleanCommandSetting(
        group = "code93Cd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.CODE_93_CHECK_CD,
        disabledCommand = CodeSpecificCommands.CODE_93_DO_NOT_CHECK_CD
    )

    val concatenation = BooleanCommandSetting(
        group = "code93Concatenation",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.CODE_93_ENABLE_CONCATENATION,
        disabledCommand = CodeSpecificCommands.CODE_93_DISABLE_CONCATENATION
    )
}

internal object EAN8SettingDescriptors {
    val transmitCD = BooleanCommandSetting(
        group = "ean8TransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.EAN_8_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.EAN_8_DO_NOT_TRANSMIT_CD
    )

    val addOnPlus2 = BooleanCommandSetting(
        group = "ean8AddOnPlus2",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_EAN_8_PLUS_2,
        disabledCommand = SymbologyCommands.DISABLE_EAN_8_PLUS_2
    )

    val addOnPlus5 = BooleanCommandSetting(
        group = "ean8AddOnPlus5",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_EAN_8_PLUS_5,
        disabledCommand = SymbologyCommands.DISABLE_EAN_8_PLUS_5
    )
}

internal object EAN13SettingDescriptors {
    val transmitCD = BooleanCommandSetting(
        group = "ean13TransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.EAN_13_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.EAN_13_DO_NOT_TRANSMIT_CD
    )

    val addOnPlus2 = BooleanCommandSetting(
        group = "ean13AddOnPlus2",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_EAN_13_PLUS_2,
        disabledCommand = SymbologyCommands.DISABLE_EAN_13_PLUS_2
    )

    val addOnPlus5 = BooleanCommandSetting(
        group = "ean13AddOnPlus5",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_EAN_13_PLUS_5,
        disabledCommand = SymbologyCommands.DISABLE_EAN_13_PLUS_5
    )
}

internal object GS1DatabarSettingDescriptors {
    val transmitCD = BooleanCommandSetting(
        group = "gs1DatabarTransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.GS1_DATABAR_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.GS1_DATABAR_DO_NOT_TRANSMIT_CD
    )

    val transmitAI = BooleanCommandSetting(
        group = "gs1DatabarAI",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.GS1_DATABAR_TRANSMIT_AI,
        disabledCommand = CodeSpecificCommands.GS1_DATABAR_DO_NOT_TRANSMIT_AI
    )
}

internal object KoreanPostalAuthoritySettingDescriptors {
    val transmitCD = BooleanCommandSetting(
        group = "koreanPostalTransmitCd",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.KOREAN_POSTAL_TRANSMIT_CD,
        disabledCommand = CodeSpecificCommands.KOREAN_POSTAL_DO_NOT_TRANSMIT_CD
    )

    val transmitDash = BooleanCommandSetting(
        group = "koreanPostalTransmitDash",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.KOREAN_POSTAL_TRANSMIT_DASH,
        disabledCommand = CodeSpecificCommands.KOREAN_POSTAL_DO_NOT_TRANSMIT_DASH
    )

    val orientationUpsideDown = BooleanCommandSetting(
        group = "koreanPostalOrientation",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.KOREAN_POSTAL_ORIENTATION_UPSIDE_DOWN,
        disabledCommand = CodeSpecificCommands.KOREAN_POSTAL_ORIENTATION_NORMAL
    )

    val upsideDownReading = BooleanCommandSetting(
        group = "koreanPostalUpsideDownReading",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.KOREAN_POSTAL_UPSIDE_DOWN_READING_ENABLED,
        disabledCommand = CodeSpecificCommands.KOREAN_POSTAL_UPSIDE_DOWN_READING_DISABLED
    )
}

internal object MSIPlesseySettingDescriptors {
    val checkCD = EnumCommandSetting(
        group = "msiPlesseyCd",
        defaultValue = MSIPlesseyCheckCDSettings.CHECK_1_CD_MOD10,
        commandsByValue = mapOf(
            MSIPlesseyCheckCDSettings.DO_NOT_CHECK_CD to CodeSpecificCommands.MSI_PLESSEY_DO_NOT_CHECK_CD,
            MSIPlesseyCheckCDSettings.CHECK_1_CD_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_1_CD_MOD_10,
            MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD10_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_10,
            MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD10_MOD11 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_11,
            MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD11_MOD10 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10,
            MSIPlesseyCheckCDSettings.CHECK_2_CDS_MOD11_MOD11 to CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_11
        )
    )

    val cdTransmission = EnumCommandSetting(
        group = "msiPlesseyTransmitCd",
        defaultValue = MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1,
        commandsByValue = mapOf(
            MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1 to CodeSpecificCommands.MSI_PLESSEY_TRANSMIT_CD_1,
            MSIPlesseyCDTransmissionSettings.TRANSMIT_CD1_AND_CD2 to CodeSpecificCommands.MSI_PLESSEY_TRANSMIT_CD_1_AND_CD_2,
            MSIPlesseyCDTransmissionSettings.DO_NOT_TRANSMIT_CD to CodeSpecificCommands.MSI_PLESSEY_DO_NOT_TRANSMIT_CD
        )
    )
}

internal object TelepenSettingDescriptors {
    val mode = EnumCommandSetting(
        group = "telepenMode",
        defaultValue = TelepenMode.NUMERIC,
        commandsByValue = mapOf(
            TelepenMode.NUMERIC to CodeSpecificCommands.TELEPEN_NUMERIC_MODE,
            TelepenMode.ASCII to CodeSpecificCommands.TELEPEN_ASCII_MODE
        )
    )
}

internal object UPCASettingDescriptors {
    val leadingZeroAndTransmitCDMode = EnumCommandSetting(
        group = "upcALeadingZeroCd",
        defaultValue = UPCALeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD,
        commandsByValue = mapOf(
            UPCALeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_A_NO_LEADING_ZERO_TRANSMIT_CD,
            UPCALeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_A_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD,
            UPCALeadingZeroAndTransmitCDMode.LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_A_LEADING_ZERO_TRANSMIT_CD,
            UPCALeadingZeroAndTransmitCDMode.LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_A_LEADING_ZERO_DO_NOT_TRANSMIT_CD
        )
    )

    val addOnPlus2 = BooleanCommandSetting(
        group = "upcAAddOnPlus2",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_A_PLUS_2,
        disabledCommand = SymbologyCommands.DISABLE_UPC_A_PLUS_2
    )

    val addOnPlus5 = BooleanCommandSetting(
        group = "upcAAddOnPlus5",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_A_PLUS_5,
        disabledCommand = SymbologyCommands.DISABLE_UPC_A_PLUS_5
    )
}

internal object UPCESettingDescriptors {
    val leadingZeroAndTransmitCDMode = EnumCommandSetting(
        group = "upcELeadingZeroCd",
        defaultValue = UPCELeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD,
        commandsByValue = mapOf(
            UPCELeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_E_NO_LEADING_ZERO_TRANSMIT_CD,
            UPCELeadingZeroAndTransmitCDMode.NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_E_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD,
            UPCELeadingZeroAndTransmitCDMode.LEADING_ZERO_TRANSMIT_CD to CodeSpecificCommands.UPC_E_LEADING_ZERO_TRANSMIT_CD,
            UPCELeadingZeroAndTransmitCDMode.LEADING_ZERO_DO_NOT_TRANSMIT_CD to CodeSpecificCommands.UPC_E_LEADING_ZERO_DO_NOT_TRANSMIT_CD
        )
    )

    val conversionMode = EnumCommandSetting(
        group = "upcEConversion",
        defaultValue = UPCEConversionMode.TRANSMIT_AS_IS,
        commandsByValue = mapOf(
            UPCEConversionMode.TRANSMIT_AS_IS to CodeSpecificCommands.UPC_E_TRANSMIT_AS_IS,
            UPCEConversionMode.TRANSMIT_AS_UPC_A to CodeSpecificCommands.UPC_E_TRANSMIT_AS_UPC_A
        )
    )

    val addOnPlus2 = BooleanCommandSetting(
        group = "upcEAddOnPlus2",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_E_PLUS_2,
        disabledCommand = SymbologyCommands.DISABLE_UPC_E_PLUS_2
    )

    val addOnPlus5 = BooleanCommandSetting(
        group = "upcEAddOnPlus5",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_E_PLUS_5,
        disabledCommand = SymbologyCommands.DISABLE_UPC_E_PLUS_5
    )
}

internal object UPCE1SettingDescriptors {
    val addOnPlus2 = BooleanCommandSetting(
        group = "upcE1AddOnPlus2",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_E1_PLUS_2,
        disabledCommand = SymbologyCommands.DISABLE_UPC_E1_PLUS_2
    )

    val addOnPlus5 = BooleanCommandSetting(
        group = "upcE1AddOnPlus5",
        defaultValue = false,
        enabledCommand = SymbologyCommands.ENABLE_UPC_E1_PLUS_5,
        disabledCommand = SymbologyCommands.DISABLE_UPC_E1_PLUS_5
    )
}

internal object UKPlesseySettingDescriptors {
    val transmitCDs = BooleanCommandSetting(
        group = "ukPlesseyTransmitCd",
        defaultValue = true,
        enabledCommand = CodeSpecificCommands.UK_PLESSEY_TRANSMIT_CDS,
        disabledCommand = CodeSpecificCommands.UK_PLESSEY_DO_NOT_TRANSMIT_CDS
    )

    val spaceInsertion = BooleanCommandSetting(
        group = "ukPlesseySpaceInsertion",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.UK_PLESSEY_ENABLE_SPACE_INSERTION,
        disabledCommand = CodeSpecificCommands.UK_PLESSEY_DISABLE_SPACE_INSERTION
    )

    val aToXConversion = BooleanCommandSetting(
        group = "plesseyAtoXConversion",
        defaultValue = false,
        enabledCommand = CodeSpecificCommands.UK_PLESSEY_ENABLE_A_TO_X_CONVERSION,
        disabledCommand = CodeSpecificCommands.UK_PLESSEY_DISABLE_A_TO_X_CONVERSION
    )
}
