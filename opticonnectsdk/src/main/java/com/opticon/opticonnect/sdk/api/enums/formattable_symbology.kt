package com.opticon.opticonnect.sdk.api.enums

/**
 * Enum representing different symbologies that are formattable by the scanner.
 *
 * This enum specifically focuses on symbologies that allow for formatting operations.
 */
enum class FormattableSymbology {
    // All symbologies
    ALL_CODES,

    // 1D Symbologies
    UPC_A,
    UPC_A_ADD_ON,
    UPC_E,
    UPC_E_ADD_ON,
    EAN_13,
    EAN_13_ADD_ON,
    EAN_8,
    EAN_8_ADD_ON,
    CODE_39,
    CODABAR,
    INDUSTRIAL_2OF5,
    INTERLEAVED_2OF5,
    S_CODE,
    MATRIX_2OF5,
    IATA,
    CODE_93,
    CODE_128,
    GS1_128,
    MSI_PLESSEY,
    TELEPEN,
    UK_PLESSEY,

    // 2D Symbologies
    DATA_MATRIX,
    QR_CODE,
    MAXICODE,
    PDF_417,
    MICRO_PDF_417,
    AZTEC,

    // Special Symbologies
    CODE_11,
    TRI_OPTIC,
    KOREAN_POSTAL_AUTHORITY,
    DOT_CODE,

    // Postal Symbologies
    INTELLIGENT_MAIL,
    POSTNET,
    PLANET,
    JAPANESE_POSTAL,
    NETHERLANDS_KIX,
    UK_POSTAL,
    AUSTRALIAN_POSTAL,
    MAIL_MARK_4_STATE_POSTAL,

    // GS1 and Composite Symbologies
    GS1_DATABAR_OMNIDIRECTIONAL,
    GS1_DATABAR_LIMITED,
    GS1_DATABAR_EXPANDED,
    GS1_COMPOSITE_CODE,

    // Codablock and Other
    CODABLOCK_F,
    CHINESE_SENSIBLE_CODE,

    // Machine Readable Documents
    MACHINE_READABLE_PASSPORTS,
    MACHINE_READABLE_VISA_A,
    MACHINE_READABLE_VISA_B,
    OFFICIAL_TRAVEL_DOCUMENTS_1,
    OFFICIAL_TRAVEL_DOCUMENTS_2,

    // Special Formats
    ISBN,
    JAPANESE_BOOK_PRICE,
    JAPANESE_DRIVER_LICENSE,
    JAPANESE_PRIVATE_NUMBER
}
