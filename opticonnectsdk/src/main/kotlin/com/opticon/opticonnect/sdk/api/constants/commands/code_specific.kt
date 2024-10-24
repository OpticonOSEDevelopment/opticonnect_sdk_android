package com.opticon.opticonnect.sdk.api.constants.commands

object CodeSpecificCommands {
    // Two of Five and S Code settings
    const val TWO_OF_FIVE_AND_S_CODE_CHECK_CD = "G1"
    const val TWO_OF_FIVE_AND_S_CODE_DO_NOT_CHECK_CD = "G0"
    const val TWO_OF_FIVE_AND_S_CODE_TRANSMIT_CD = "E0"
    const val TWO_OF_FIVE_AND_S_CODE_DO_NOT_TRANSMIT_CD = "E1"
    const val TWO_OF_FIVE_AND_S_CODE_ENABLE_SPACE_CHECK_INDUSTRIAL_2OF5 = "GJ"
    const val TWO_OF_FIVE_AND_S_CODE_DISABLE_SPACE_CHECK_INDUSTRIAL_2OF5 = "GK"
    const val TWO_OF_FIVE_AND_S_CODE_TRANSMIT_AS_INTERLEAVED_2OF5 = "GG"
    const val TWO_OF_FIVE_AND_S_CODE_DO_NOT_TRANSMIT_AS_INTERLEAVED_2OF5 = "GH"
    const val TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_ONE_CHAR = "GE"
    const val TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_THREE_CHARS = "GF"
    const val TWO_OF_FIVE_AND_S_CODE_MINIMUM_LENGTH_FIVE_CHARS = "GI"

    // Codabar settings
    const val CODABAR_NORMAL = "HA"
    const val CODABAR_ABC_CODE_ONLY = "H4"
    const val CODABAR_CX_CODE_ONLY = "H5"
    const val CODABAR_ABC_AND_CX = "H3"
    const val CODABAR_CHECK_CD = "H6"
    const val CODABAR_DO_NOT_CHECK_CD = "H7"
    const val CODABAR_TRANSMIT_CD = "H8"
    const val CODABAR_DO_NOT_TRANSMIT_CD = "H9"
    const val CODABAR_DO_NOT_TRANSMIT_ST_SP = "F0"
    const val CODABAR_ST_SP_ABCD_ABCD = "F3"
    const val CODABAR_ST_SP_ABCD_ABCD_LOWERCASE = "F4"
    const val CODABAR_ST_SP_ABCD_TNE = "F1"
    const val CODABAR_ST_SP_ABCD_TNE_LOWERCASE = "F2"
    const val CODABAR_ST_SP_DC1_DC2_DC3_DC4 = "HJ"
    const val CODABAR_DISABLE_SPACE_INSERTION = "HE"
    const val CODABAR_ENABLE_SPACE_INSERTION = "HD"
    const val CODABAR_ENABLE_INTERCHARACTER_GAP_CHECK = "HH"
    const val CODABAR_DISABLE_INTERCHARACTER_GAP_CHECK = "HI"
    const val CODABAR_MINIMUM_LENGTH_ONE_CHAR = "HC"
    const val CODABAR_MINIMUM_LENGTH_THREE_CHARS = "HB"
    const val CODABAR_MINIMUM_LENGTH_FIVE_CHARS = "HF"

    // Code 11 settings
    const val CODE_11_DO_NOT_CHECK_CD = "[BLF"
    const val CODE_11_CHECK_1_CD = "[BLG"
    const val CODE_11_CHECK_2_CDS = "[BLH"
    const val CODE_11_CHECK_1_OR_2_CDS = "[BLI"
    const val CODE_11_DO_NOT_TRANSMIT_CD = "[BLJ"
    const val CODE_11_TRANSMIT_CD = "[BLK"

    // Code 39 settings
    const val NORMAL_CODE_39 = "D5"
    const val FULL_ASCII_CODE_39 = "D4"
    const val FULL_ASCII_CODE_39_IF_POSSIBLE = "+K"
    const val IT_PHARMACEUTICAL_ONLY = "D6"
    const val IT_PHARMACEUTICAL_IF_POSSIBLE = "D7"
    const val CODE_39_CHECK_CD = "C0"
    const val CODE_39_DO_NOT_CHECK_CD = "C1"
    const val CODE_39_TRANSMIT_CD = "D9"
    const val CODE_39_DO_NOT_TRANSMIT_CD = "D8"
    const val CODE_39_TRANSMIT_ST_SP = "D0"
    const val CODE_39_DO_NOT_TRANSMIT_ST_SP = "D1"
    const val CODE_39_DO_NOT_TRANSMIT_LEADING_A_FOR_IT_PHARM = "DA"
    const val CODE_39_TRANSMIT_LEADING_A_FOR_IT_PHARM = "DB"
    const val CODE_39_MIN_LENGTH_3_DIGITS = "8D"
    const val CODE_39_MIN_LENGTH_1_DIGIT = "8E"
    const val CODE_39_ENABLE_CONCATENATION = "+L"
    const val CODE_39_DISABLE_CONCATENATION = "+M"

    // Code 93 settings
    const val CODE_93_DO_NOT_CHECK_CD = "9Q"
    const val CODE_93_CHECK_CD = "AC"
    const val CODE_93_DO_NOT_TRANSMIT_CD = "DZ"
    const val CODE_93_TRANSMIT_CD = "DY"
    const val CODE_93_ENABLE_CONCATENATION = "+V"
    const val CODE_93_DISABLE_CONCATENATION = "+W"

    // Code 128 settings
    const val CODE_128_DISABLE_GS1_128 = "OF"
    const val CODE_128_ENABLE_GS1_128_ONLY = "JF"
    const val CODE_128_ENABLE_GS1_128_IF_POSSIBLE = "OG"
    const val CODE_128_DISABLE_GS1_128_CONVERSION = "[X/0"
    const val CODE_128_ENABLE_GS1_128_CONVERSION_MODE_1 = "[X/1"
    const val CODE_128_ENABLE_GS1_128_CONVERSION_MODE_2 = "[X/2"
    const val CODE_128_ENABLE_GS1_128_CONVERSION_MODE_3 = "[X/3"
    const val CODE_128_ENABLE_GS1_128_CONVERSION_MODE_4 = "[X/4"
    const val CODE_128_DISABLE_LEADING_C1_OUTPUT = "[X/Q"
    const val CODE_128_ENABLE_LEADING_C1_OUTPUT = "[X/R"
    const val CODE_128_DISABLE_CONCATENATION = "MP"
    const val CODE_128_ENABLE_CONCATENATION = "MO"

    // Composite Code settings
    const val COMPOSITE_OUTPUT_1D_AND_2D_COMPONENT = "[BL2"
    const val COMPOSITE_OUTPUT_2D_COMPONENT = "[BL1"
    const val COMPOSITE_OUTPUT_1D_COMPONENT = "[BL0"
    const val COMPOSITE_DO_NOT_IGNORE_LINK_FLAG = "RQ"
    const val COMPOSITE_IGNORE_LINK_FLAG = "RP"
    const val DISABLE_COMPOSITE_GS1 = "[BHF"
    const val ENABLE_COMPOSITE_GS1 = "[BHE"
    const val DISABLE_COMPOSITE_EAN_UPC = "[D1W"
    const val ENABLE_COMPOSITE_EAN_UPC = "[D1V"

    // EAN settings
    const val EAN_8_TRANSMIT_CD = "6I"
    const val EAN_8_DO_NOT_TRANSMIT_CD = "6H"
    const val EAN_13_TRANSMIT_CD = "6K"
    const val EAN_13_DO_NOT_TRANSMIT_CD = "6J"
    const val EAN_DISABLE_ISSN_TRANSLATION = "HN"
    const val EAN_ENABLE_ISSN_TRANSLATION = "HO"
    const val EAN_ENABLE_ISSN_IF_POSSIBLE = "4V"
    const val EAN_DISABLE_ISMN_TRANSLATION = "IO"
    const val EAN_ENABLE_ISMN_TRANSLATION = "IP"
    const val EAN_ENABLE_ISMN_IF_POSSIBLE = "IQ"
    const val EAN_DISABLE_ISBN_TRANSLATION = "IB"
    const val EAN_ENABLE_ISBN_TRANSLATION = "IA"
    const val EAN_ENABLE_ISBN_IF_POSSIBLE = "IK"
    const val EAN_ENABLE_FORCED_ADDON_378_379_529 = "-G"
    const val EAN_DISABLE_FORCED_ADDON_378_379_529 = "-H"
    const val EAN_ENABLE_FORCED_ADDON_434_439_414_419_977_978 = "-C"
    const val EAN_DISABLE_FORCED_ADDON_434_439_414_419_977_978 = "-D"

    // GS1 DataBar settings
    const val GS1_DATABAR_TRANSMIT_CD = "DL"
    const val GS1_DATABAR_DO_NOT_TRANSMIT_CD = "DM"
    const val GS1_DATABAR_TRANSMIT_AI = "DS"
    const val GS1_DATABAR_DO_NOT_TRANSMIT_AI = "DT"

    // IATA settings
    const val IATA_DO_NOT_CHECK_CD = "4H"
    const val IATA_CHECK_FC_AND_SN_ONLY = "4I"
    const val IATA_CHECK_CPN_FC_AND_SN = "4J"
    const val IATA_CHECK_CPN_AC_FC_AND_SN = "4K"
    const val IATA_TRANSMIT_CD = "4L"
    const val IATA_DO_NOT_TRANSMIT_CD = "4M"

    // Korean Postal settings
    const val KOREAN_POSTAL_TRANSMIT_CD = "*+"
    const val KOREAN_POSTAL_DO_NOT_TRANSMIT_CD = "*-"
    const val KOREAN_POSTAL_TRANSMIT_DASH = "*."
    const val KOREAN_POSTAL_DO_NOT_TRANSMIT_DASH = "*/"
    const val KOREAN_POSTAL_ORIENTATION_NORMAL = "*A"
    const val KOREAN_POSTAL_ORIENTATION_UPSIDE_DOWN = "*B"
    const val KOREAN_POSTAL_UPSIDE_DOWN_READING_DISABLED = "*8"
    const val KOREAN_POSTAL_UPSIDE_DOWN_READING_ENABLED = "*9"

    // MSI Plessey settings
    const val MSI_PLESSEY_DO_NOT_CHECK_CD = "4A"
    const val MSI_PLESSEY_CHECK_1_CD_MOD_10 = "4B"
    const val MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_10 = "4C"
    const val MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_11 = "4D"
    const val MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10 = "4R"
    const val MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_11 = "4S"
    const val MSI_PLESSEY_TRANSMIT_CD_1 = "4E"
    const val MSI_PLESSEY_TRANSMIT_CD_1_AND_CD_2 = "4F"
    const val MSI_PLESSEY_DO_NOT_TRANSMIT_CD = "4G"

    // Telepen settings
    const val TELEPEN_NUMERIC_MODE = "D2"
    const val TELEPEN_ASCII_MODE = "D3"

    // UK Plessey settings
    const val UK_PLESSEY_TRANSMIT_CDS = "4N"
    const val UK_PLESSEY_DO_NOT_TRANSMIT_CDS = "4O"
    const val UK_PLESSEY_DISABLE_SPACE_INSERTION = "DO"
    const val UK_PLESSEY_ENABLE_SPACE_INSERTION = "DN"
    const val UK_PLESSEY_DISABLE_A_TO_X_CONVERSION = "DP"
    const val UK_PLESSEY_ENABLE_A_TO_X_CONVERSION = "DQ"

    // UPC-A settings
    const val UPC_A_NO_LEADING_ZERO_TRANSMIT_CD = "E3"
    const val UPC_A_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD = "E5"
    const val UPC_A_LEADING_ZERO_TRANSMIT_CD = "E2"
    const val UPC_A_LEADING_ZERO_DO_NOT_TRANSMIT_CD = "E4"

    // UPC-E settings
    const val UPC_E_NO_LEADING_ZERO_TRANSMIT_CD = "E7"
    const val UPC_E_NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD = "E9"
    const val UPC_E_LEADING_ZERO_TRANSMIT_CD = "E6"
    const val UPC_E_LEADING_ZERO_DO_NOT_TRANSMIT_CD = "E8"
    const val UPC_E_TRANSMIT_AS_IS = "6Q"
    const val UPC_E_TRANSMIT_AS_UPC_A = "6P"
}