package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

interface CodeSpecific {
    val codabar: Codabar
    val code2of5AndSCode: Code2Of5AndSCode
    val code11: Code11
    val code39: Code39
    val code93: Code93
    val code128AndGS1128: Code128AndGS1128
    val compositeCodes: CompositeCodes
    val ean8: EAN8
    val ean13: EAN13
    val gs1Databar: GS1Databar
    val iata: IATA
    val koreanPostalAuthority: KoreanPostalAuthority
    val msiPlessey: MSIPlessey
    val telepen: Telepen
    val ukPlessey: UKPlessey
    val upcA: UPCA
    val upcE: UPCE
    val upcE1: UPCE1
}
