package com.opticon.opticonnect.sdk.api.enums

/**
 * Enum representing different symbologies supported by the scanner.
 *
 * This includes both 1D, 2D, and postal symbologies.
 */
enum class SymbologyType {
    // All symbologies
    ALL_CODES,

    // 1D Symbologies
    ALL_1D_CODES,
    CODE_11,
    CODE_39,
    CODE_93,
    CODE_128,
    CODABAR,
    EAN_8,
    EAN_8_ADD_ON_2,
    EAN_8_ADD_ON_5,
    EAN_13,
    EAN_13_ADD_ON_2,
    EAN_13_ADD_ON_5,
    IATA,
    INDUSTRIAL_2OF5,
    INTERLEAVED_2OF5,
    MATRIX_2OF5,
    MSI_PLESSEY,
    S_CODE,
    TELEPEN,
    TRI_OPTIC,
    UK_PLESSEY,
    UPC_A,
    UPC_A_ADD_ON_2,
    UPC_A_ADD_ON_5,
    UPC_E,
    UPC_E_ADD_ON_2,
    UPC_E_ADD_ON_5,
    UPC_E1,
    UPC_E1_ADD_ON_2,
    UPC_E1_ADD_ON_5,
    GS1_DATABAR_ALL_TYPES,
    GS1_DATABAR,
    GS1_DATABAR_LIMITED,
    GS1_DATABAR_EXPANDED,

    // 2D Symbologies
    ALL_2D_CODES,
    AZTEC_CODE,
    AZTEC_RUNES,
    CHINESE_SENSIBLE_CODE,
    CODABLOCK_F,
    DATA_MATRIX,
    DATA_MATRIX_OLD_ECC000_140,
    DOT_CODE,
    MAXICODE,
    MICRO_PDF_417,
    MICRO_QR_CODE,
    PDF_417,
    QR_CODE,

    // Postal Symbologies
    AUSTRALIAN_POSTAL,
    CHINESE_POST_MATRIX_2OF5,
    INTELLIGENT_MAIL_BARCODE,
    JAPANESE_POSTAL,
    KOREAN_POSTAL_AUTHORITY,
    MAILMARK_4_STATE_POSTAL,
    NETHERLANDS_KIX_CODE,
    PLANET,
    POSTNET,
    UK_POSTAL
}
