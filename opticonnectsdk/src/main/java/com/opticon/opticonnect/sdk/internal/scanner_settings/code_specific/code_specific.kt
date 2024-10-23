package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Codabar
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code11
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code128AndGS1128
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code2Of5AndSCode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code39
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code93
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CodeSpecific
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CompositeCodes
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN13
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN8
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.GS1Databar
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.IATA
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.KoreanPostalAuthority
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.MSIPlessey
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Telepen
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UKPlessey
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCA
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE1
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CodeSpecificImpl @Inject constructor(
    override val codabar: Codabar,
    override val code2of5AndSCode: Code2Of5AndSCode,
    override val code11: Code11,
    override val code39: Code39,
    override val code93: Code93,
    override val code128AndGS1128: Code128AndGS1128,
    override val compositeCodes: CompositeCodes,
    override val ean8: EAN8,
    override val ean13: EAN13,
    override val gs1Databar: GS1Databar,
    override val iata: IATA,
    override val koreanPostalAuthority: KoreanPostalAuthority,
    override val msiPlessey: MSIPlessey,
    override val telepen: Telepen,
    override val ukPlessey: UKPlessey,
    override val upcA: UPCA,
    override val upcE: UPCE,
    override val upcE1: UPCE1
) : CodeSpecific