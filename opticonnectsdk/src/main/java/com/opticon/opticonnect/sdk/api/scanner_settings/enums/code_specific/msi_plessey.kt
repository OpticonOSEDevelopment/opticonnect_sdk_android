package com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific

enum class MSIPlesseyCheckCDSettings {
    DO_NOT_CHECK_CD,
    CHECK_1_CD_MOD10,
    CHECK_2_CDS_MOD10_MOD10,
    CHECK_2_CDS_MOD10_MOD11,
    CHECK_2_CDS_MOD11_MOD10,
    CHECK_2_CDS_MOD11_MOD11
}

enum class MSIPlesseyCDTransmissionSettings {
    TRANSMIT_CD1,
    TRANSMIT_CD1_AND_CD2,
    DO_NOT_TRANSMIT_CD
}