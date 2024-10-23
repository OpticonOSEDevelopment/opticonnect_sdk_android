package com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific

enum class Code128AndGS1128Mode {
    DISABLE_GS1_128,
    ENABLE_GS1_128_ONLY,
    ENABLE_GS1_128_IF_POSSIBLE
}

enum class GS1128ConversionMode {
    DISABLED,
    ENABLE_MODE_1,
    ENABLE_MODE_2,
    ENABLE_MODE_3,
    ENABLE_MODE_4
}