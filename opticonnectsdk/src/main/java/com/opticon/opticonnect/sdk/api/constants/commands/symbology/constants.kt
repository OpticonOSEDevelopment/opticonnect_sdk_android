package com.opticon.opticonnect.sdk.api.constants.commands.symbology

object SymbologyCommands {
    // Enable 1D symbologies for scanning.
// These commands enable or disable individual 1D barcode symbologies.
    const val ENABLE_ALL_1D_CODES_EXCL_ADDON_ONLY = "[BCA"
    const val ENABLE_CODE_11_ONLY = "[BLB"
    const val ENABLE_CODE_39_ONLY = "A2"
    const val ENABLE_CODE_93_ONLY = "A5"
    const val ENABLE_CODE_128_ONLY = "A6"
    const val ENABLE_CODABAR_ONLY = "A3"
    const val ENABLE_EAN_ONLY = "J4"
    const val ENABLE_EAN_8_ONLY = "JA"
    const val ENABLE_EAN_13_ONLY = "JG"
    const val ENABLE_EAN_PLUS_2_ONLY = "J5"
    const val ENABLE_EAN_PLUS_5_ONLY = "J6"
    const val ENABLE_EAN_8_PLUS_2_ONLY = "JB"
    const val ENABLE_EAN_8_PLUS_5_ONLY = "JC"
    const val ENABLE_EAN_13_PLUS_2_ONLY = "JH"
    const val ENABLE_EAN_13_PLUS_5_ONLY = "JI"
    const val ENABLE_IATA_ONLY = "A4"
    const val ENABLE_INDUSTRIAL_2OF5_ONLY = "J7"
    const val ENABLE_INTERLEAVED_2OF5_ONLY = "J8"
    const val ENABLE_MATRIX_2OF5_ONLY = "AB"
    const val ENABLE_MSI_PLESSEY_ONLY = "A7"
    const val ENABLE_S_CODE_ONLY = "RA"
    const val ENABLE_TELEPEN_ONLY = "A9"
    const val ENABLE_TRI_OPTIC_ONLY = "JD"
    const val ENABLE_UK_PLESSEY_ONLY = "A1"
    const val ENABLE_UPC_A_ONLY = "[J1A"
    const val ENABLE_UPC_A_PLUS_2_ONLY = "[J2A"
    const val ENABLE_UPC_A_PLUS_5_ONLY = "[J3A"
    const val ENABLE_UPC_AE_ONLY = "J1"
    const val ENABLE_UPC_AE_PLUS_2_ONLY = "J2"
    const val ENABLE_UPC_AE_PLUS_5_ONLY = "J3"
    const val ENABLE_UPC_E_ONLY = "[J1B"
    const val ENABLE_UPC_E_PLUS_2_ONLY = "[J2B"
    const val ENABLE_UPC_E_PLUS_5_ONLY = "[J3B"
    const val ENABLE_UPC_E1_ONLY = "[J1C"
    const val ENABLE_UPC_E1_PLUS_2_ONLY = "[J2C"
    const val ENABLE_UPC_E1_PLUS_5_ONLY = "[J3C"
    const val ENABLE_GS1_ALL_TYPES_ONLY = "[BC6"
    const val ENABLE_GS1_DATABAR_ONLY = "J9"
    const val ENABLE_GS1_DATABAR_LIMITED_ONLY = "JJ"
    const val ENABLE_GS1_DATABAR_EXPANDED_ONLY = "JK"

    // Enable 2D symbologies for scanning.
    const val ENABLE_ALL_2D_CODES_ONLY = "[BCB"
    const val ENABLE_AZTEC_CODE_ONLY = "[BC5"
    const val ENABLE_AZTEC_RUNES_ONLY = "[BF4"
    const val ENABLE_CHINESE_SENSIBLE_CODE_ONLY = "[D4K"
    const val ENABLE_CODABLOCK_F_ONLY = "[D4R"
    const val ENABLE_DATA_MATRIX_ONLY = "[BC0"
    const val ENABLE_DATA_MATRIX_OLD_ECC000_140_ONLY = "[BG2"
    const val ENABLE_DOT_CODE_ONLY = "[DOC"
    const val ENABLE_MAXICODE_ONLY = "[BC2"
    const val ENABLE_MICRO_PDF_417_ONLY = "[BC4"
    const val ENABLE_MICRO_QR_CODE_ONLY = "[D38"
    const val ENABLE_PDF_417_ONLY = "[BC3"
    const val ENABLE_QR_CODE_ONLY = "[BC1"

    // Enable postal symbologies for scanning.
    const val ENABLE_AUSTRALIAN_POSTAL_ONLY = "[D6O"
    const val ENABLE_CHINESE_POST_MATRIX_2OF5_ONLY = "JE"
    const val ENABLE_INTELLIGENT_MAIL_BARCODE_ONLY = "[D5H"
    const val ENABLE_JAPANESE_POSTAL_ONLY = "[D5R"
    const val ENABLE_KOREAN_POSTAL_AUTHORITY_ONLY = "JL"
    const val ENABLE_MAILMARK_4_STATE_POSTAL_ONLY = "[DGS"
    const val ENABLE_NETHERLANDS_KIX_CODE_ONLY = "[D5M"
    const val ENABLE_PLANET_ONLY = "[DG2"
    const val ENABLE_POSTNET_ONLY = "[D6C"
    const val ENABLE_UK_POSTAL_ONLY = "[DG7"

    // Disable symbologies
    const val DISABLE_ALL_CODES = "B0"

    // Disable 1D symbologies
    const val DISABLE_ALL_1D_CODES = "[BCY"
    const val DISABLE_CODE_11 = "[BLA"
    const val DISABLE_CODE_39 = "VB"
    const val DISABLE_CODE_93 = "VD"
    const val DISABLE_CODE_128 = "VE"
    const val DISABLE_CODABAR = "VC"
    const val DISABLE_EAN = "[X4E"
    const val DISABLE_EAN_8 = "[DDN"
    const val DISABLE_EAN_13 = "[DDM"
    const val DISABLE_EAN_PLUS_2 = "[X4F"
    const val DISABLE_EAN_PLUS_5 = "[X4G"
    const val DISABLE_EAN_8_PLUS_2 = "[X4M"
    const val DISABLE_EAN_8_PLUS_5 = "[X4O"
    const val DISABLE_EAN_13_PLUS_2 = "[X4N"
    const val DISABLE_EAN_13_PLUS_5 = "[X4P"
    const val DISABLE_IATA = "VH"
    const val DISABLE_INDUSTRIAL_2OF5 = "[X4K"
    const val DISABLE_INTERLEAVED_2OF5 = "[X4L"
    const val DISABLE_MATRIX_2OF5 = "[DDL"
    const val DISABLE_MSI_PLESSEY = "VF"
    const val DISABLE_S_CODE = "[DDK"
    const val DISABLE_TELEPEN = "VG"
    const val DISABLE_TRI_OPTIC = "[DDJ"
    const val DISABLE_UK_PLESSEY = "VA"
    const val DISABLE_UPC_A = "[V1A"
    const val DISABLE_UPC_A_PLUS_2 = "[V2A"
    const val DISABLE_UPC_A_PLUS_5 = "[V3A"
    const val DISABLE_UPC_AE = "[X4B"
    const val DISABLE_UPC_AE_PLUS_2 = "[X4C"
    const val DISABLE_UPC_AE_PLUS_5 = "[X4D"
    const val DISABLE_UPC_E = "[V1B"
    const val DISABLE_UPC_E_PLUS_2 = "[V2B"
    const val DISABLE_UPC_E_PLUS_5 = "[V3B"
    const val DISABLE_UPC_E1 = "KP"
    const val DISABLE_UPC_E1_PLUS_2 = "[V2C"
    const val DISABLE_UPC_E1_PLUS_5 = "[V3C"
    const val DISABLE_GS1_ALL_TYPES = "[BCU"
    const val DISABLE_GS1_DATABAR = "SJ"
    const val DISABLE_GS1_DATABAR_LIMITED = "SK"
    const val DISABLE_GS1_DATABAR_EXPANDED = "SL"

    // Disable 2D symbologies
    const val DISABLE_ALL_2D_CODES = "[BCZ"
    const val DISABLE_AZTEC_CODE = "[BCT"
    const val DISABLE_AZTEC_RUNES = "[BF3"
    const val DISABLE_CHINESE_SENSIBLE_CODE = "[D4M"
    const val DISABLE_CODABLOCK_F = "[D4Q"
    const val DISABLE_DATA_MATRIX = "[BCO"
    const val DISABLE_DATA_MATRIX_OLD_ECC000_140 = "[BG1"
    const val DISABLE_DOT_CODE = "[DOE"
    const val DISABLE_MAXICODE = "[BCQ"
    const val DISABLE_MICRO_PDF_417 = "[BCS"
    const val DISABLE_MICRO_QR_CODE = "[D2V"
    const val DISABLE_PDF_417 = "[BCR"
    const val DISABLE_QR_CODE = "[BCP"

    // Disable postal symbologies
    const val DISABLE_AUSTRALIAN_POSTAL = "[D6N"
    const val DISABLE_CHINESE_POST_MATRIX_2OF5 = "JT"
    const val DISABLE_INTELLIGENT_MAIL_BARCODE = "[D5G"
    const val DISABLE_JAPANESE_POSTAL = "[D5Q"
    const val DISABLE_KOREAN_POSTAL_AUTHORITY = "WI"
    const val DISABLE_MAILMARK_4_STATE_POSTAL = "[DGU"
    const val DISABLE_NETHERLANDS_KIX_CODE = "[D5L"
    const val DISABLE_PLANET = "[DG4"
    const val DISABLE_POSTNET = "[D6B"
    const val DISABLE_UK_POSTAL = "[DG9"

    // Enable all symbologies
    const val ENABLE_ALL_CODES_EXCL_ADDON = "A0"

    // Enable 1D symbologies
    const val ENABLE_ALL_1D_CODES_EXCL_ADDON = "[BCM"
    const val ENABLE_CODE_11 = "[BLC"
    const val ENABLE_CODE_39 = "B2"
    const val ENABLE_CODE_93 = "B5"
    const val ENABLE_CODE_128 = "B6"
    const val ENABLE_CODABAR = "B3"
    const val ENABLE_EAN = "R4"
    const val ENABLE_EAN_8 = "JO"
    const val ENABLE_EAN_13 = "JU"
    const val ENABLE_EAN_PLUS_2 = "R5"
    const val ENABLE_EAN_PLUS_5 = "R6"
    const val ENABLE_EAN_8_PLUS_2 = "JP"
    const val ENABLE_EAN_8_PLUS_5 = "JQ"
    const val ENABLE_EAN_13_PLUS_2 = "JV"
    const val ENABLE_EAN_13_PLUS_5 = "JW"
    const val ENABLE_IATA = "B4"
    const val ENABLE_INDUSTRIAL_2OF5 = "R7"
    const val ENABLE_INTERLEAVED_2OF5 = "R8"
    const val ENABLE_MATRIX_2OF5 = "BB"
    const val ENABLE_MSI_PLESSEY = "B7"
    const val ENABLE_S_CODE = "R9"
    const val ENABLE_TELEPEN = "B9"
    const val ENABLE_TRI_OPTIC = "JZ"
    const val ENABLE_UK_PLESSEY = "B1"
    const val ENABLE_UPC_A = "[R1A"
    const val ENABLE_UPC_A_PLUS_2 = "[R2A"
    const val ENABLE_UPC_A_PLUS_5 = "[R3A"
    const val ENABLE_UPC_AE = "R1"
    const val ENABLE_UPC_AE_PLUS_2 = "R2"
    const val ENABLE_UPC_AE_PLUS_5 = "R3"
    const val ENABLE_UPC_E = "[R1B"
    const val ENABLE_UPC_E_PLUS_2 = "[R2B"
    const val ENABLE_UPC_E_PLUS_5 = "[R3B"
    const val ENABLE_UPC_E1 = "KQ"
    const val ENABLE_UPC_E1_PLUS_2 = "[R2C"
    const val ENABLE_UPC_E1_PLUS_5 = "[R3C"
    const val ENABLE_GS1_ALL_TYPES = "[BCI"
    const val ENABLE_GS1_DATABAR = "JX"
    const val ENABLE_GS1_DATABAR_LIMITED = "JY"
    const val ENABLE_GS1_DATABAR_EXPANDED = "DR"

    // Enable 2D symbologies
    const val ENABLE_ALL_2D_CODES = "[BCN"
    const val ENABLE_AZTEC_CODE = "[BCH"
    const val ENABLE_AZTEC_RUNES = "[BF2"
    const val ENABLE_CHINESE_SENSIBLE_CODE = "[D4L"
    const val ENABLE_CODABLOCK_F = "[D4P"
    const val ENABLE_DATA_MATRIX = "[BCC"
    const val ENABLE_DATA_MATRIX_OLD_ECC000_140 = "[BG0"
    const val ENABLE_DOT_CODE = "[DOD"
    const val ENABLE_MAXICODE = "[BCE"
    const val ENABLE_MICRO_PDF_417 = "[BCG"
    const val ENABLE_MICRO_QR_CODE = "[D2U"
    const val ENABLE_PDF_417 = "[BCF"
    const val ENABLE_QR_CODE = "[BCD"

    // Enable postal symbologies
    const val ENABLE_AUSTRALIAN_POSTAL = "[D6M"
    const val ENABLE_CHINESE_POST_MATRIX_2OF5 = "JS"
    const val ENABLE_INTELLIGENT_MAIL_BARCODE = "[D5F"
    const val ENABLE_JAPANESE_POSTAL = "[D5P"
    const val ENABLE_KOREAN_POSTAL_AUTHORITY = "WH"
    const val ENABLE_MAILMARK_4_STATE_POSTAL = "[DGT"
    const val ENABLE_NETHERLANDS_KIX_CODE = "[D5K"
    const val ENABLE_PLANET = "[DG3"
    const val ENABLE_POSTNET = "[D6A"
    const val ENABLE_UK_POSTAL = "[DG8"
}
