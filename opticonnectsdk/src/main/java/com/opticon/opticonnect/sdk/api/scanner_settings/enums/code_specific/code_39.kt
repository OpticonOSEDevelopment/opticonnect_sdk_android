package com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific

enum class Code39Mode {
    NORMAL,
    FULL_ASCII,
    FULL_ASCII_IF_POSSIBLE,
    IT_PHARMACEUTICAL_ONLY,
    IT_PHARMACEUTICAL_IF_POSSIBLE
}

enum class Code39MinimumLength {
    ONE_DIGIT,
    THREE_DIGITS
}