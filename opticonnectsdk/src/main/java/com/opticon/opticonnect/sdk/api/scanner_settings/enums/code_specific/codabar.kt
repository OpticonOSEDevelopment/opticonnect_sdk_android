package com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific

/**
 * Enum representing different modes for Codabar symbology.
 */
enum class CodabarMode {
    /** Normal Codabar mode. */
    NORMAL,

    /** Codabar mode with only ABC codes. */
    ABC_CODE_ONLY,

    /** Codabar mode with only CX codes. */
    CX_CODE_ONLY,

    /** Codabar mode with both ABC and CX codes. */
    CODABAR_ABC_AND_CX
}

/**
 * Enum representing the start and stop character transmission options for Codabar symbology.
 */
enum class CodabarStartStopTransmission {
    /** Do not transmit start and stop characters. */
    DO_NOT_TRANSMIT_START_STOP,

    /** Transmit start and stop characters in ABCD-ABCD format. */
    START_STOP_ABCD_ABCD,

    /** Transmit start and stop characters in abcd-abcd format. */
    START_STOP_ABCD_ABCD_LOWER,

    /** Transmit start and stop characters in ABCD-TNxE format. */
    START_STOP_ABCD_TNX_E,

    /** Transmit start and stop characters in abcd-tnxe format. */
    START_STOP_ABCD_TNX_E_LOWER,

    /** Transmit start and stop characters in DC1-DC2-DC3-DC4 format. */
    START_STOP_DC1_DC2_DC3_DC4
}

/**
 * Enum representing the minimum data lengths for Codabar symbology.
 */
enum class CodabarMinimumLength {
    /** Minimum length of 1 character. */
    ONE_CHARACTER,

    /** Minimum length of 3 characters. */
    THREE_CHARACTERS,

    /** Minimum length of 5 characters. */
    FIVE_CHARACTERS
}
