package com.opticon.opticonnect.sdk.api.scanner_settings.enums

enum class PositiveAndNegativeBarcodesMode {
    POSITIVE_BARCODES,
    NEGATIVE_BARCODES,
    POSITIVE_AND_NEGATIVE_BARCODES
}

enum class ReadMode {
    SINGLE_READ,
    MULTIPLE_READ
}

enum class ReadTime {
    ZERO_SECONDS,
    ONE_SECOND,
    TWO_SECONDS,
    THREE_SECONDS,
    FOUR_SECONDS,
    FIVE_SECONDS,
    SIX_SECONDS,
    SEVEN_SECONDS,
    EIGHT_SECONDS,
    NINE_SECONDS,
    INDEFINITE
}

enum class IlluminationMode {
    ENABLE_FLOODLIGHT,
    DISABLE_FLOODLIGHT,
    ALTERNATING_FLOODLIGHT,
    PREVENT_SPECULAR_REFLECTION
}