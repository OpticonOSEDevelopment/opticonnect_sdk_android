package com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific

enum class UPCELeadingZeroAndTransmitCDMode {
    NO_LEADING_ZERO_TRANSMIT_CD,
    NO_LEADING_ZERO_DO_NOT_TRANSMIT_CD,
    LEADING_ZERO_TRANSMIT_CD,
    LEADING_ZERO_DO_NOT_TRANSMIT_CD
}

enum class UPCEConversionMode {
    TRANSMIT_AS_IS,
    TRANSMIT_AS_UPC_A
}