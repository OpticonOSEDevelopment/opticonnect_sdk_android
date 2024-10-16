package com.opticon.opticonnect.sdk.internal.services.core

import com.opticon.opticonnect.sdk.api.constants.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbologyHandler @Inject constructor() {
    private val symbologyNameMap = mapOf(
        EAN_8 to "EAN-8",
        EAN_13 to "EAN-13",
        DISCRETE_2_OF_5 to "Industrial 2 of 5",
        MATRIX_2_OF_5 to "Matrix 2 of 5",
        INTERLEAVED_2_OF_5 to "Interleaved 2 of 5",
        CODABAR to "Codabar",
        CODE_93 to "Code 93",
        CODE_128 to "Code 128",
        UPC_A to "UPC-A",
        UPC_E to "UPC-E",
        GS1_14 to "GS1 Databar-14",
        GS1_LIMIT to "GS1 DataBar Limited",
        GS1_EXP to "GS1 DataBar Expanded",
        PDF_417 to "PDF417",
        TRI_OPTIC to "Tri-Optic",
        CODE_32 to "Code 32",
        MICRO_PDF_417 to "MicroPDF417",
        QR_CODE to "QR Code",
        AZTEC to "Aztec",
        POSTAL_PLANET to "PLANET",
        POSTAL_POSTNET to "POSTNET",
        POSTAL_4_STATE to "Mailmark4StatePostal",
        POSTAL_ROYAL_MAIL to "UK Postal (Royal Mail)",
        POSTAL_AUSTRALIAN to "Australian Postal",
        POSTAL_KIX to "Netherlands KIX Code",
        POSTAL_JAPAN to "Japanese Postal",
        GS1_128 to "GS1-128",
        MICRO_QR to "Micro QR Code",
        UPC_E1 to "UPC-E1",
        UPC_A_ADD_ON_2 to "UPC-A + 2",
        UPC_E_ADD_ON_2 to "UPC-E + 2",
        EAN_13_ADD_ON_2 to "EAN-13 + 2",
        EAN_8_ADD_ON_2 to "EAN-8 + 2",
        UPC_A_ADD_ON_5 to "UPC-A + 5",
        UPC_E_ADD_ON_5 to "UPC-E + 5",
        EAN_13_ADD_ON_5 to "EAN-13 + 5",
        EAN_8_ADD_ON_5 to "EAN-8 + 5",
        ISSN to "ISSN",
        ISBN to "ISBN",
        UPC_E1_ADD_ON_2 to "UPC-E1 + 2",
        UPC_E1_ADD_ON_5 to "UPC-E1 + 5",
        ISBT_128 to "ISBT 128",
        CODE_39_FULL_ASCII to "Code 39 Full ASCII",
        CODE_39 to "Code 39",
        ITALIAN_PHARMACEUTICAL to "Italian Pharmacode",
        CODABAR_ABC to "ABC Codabar",
        CODABAR_CX to "CX Codabar",
        INDUSTRIAL_2_OF_5 to "Industrial 2 of 5",
        S_CODE to "S-Code",
        CHINESE_POST to "Chinese Post Matrix 2 of 5",
        IATA to "IATA",
        MSI_PLESSEY to "MSI/Plessey",
        TELEPEN to "Telepen",
        UK_PLESSEY to "UK/Plessey",
        CODE_11 to "Code 11",
        KOREAN_POSTAL to "Korean Postal Authority code",
        INTELLIGENT_MAIL to "Intelligent Mail Barcode",
        GS1_DATA_BAR to "GS1 DataBar",
        CC_A to "CC-A",
        CC_B to "CC-B",
        CC_C to "CC-C",
        CODABLOCK_F to "Codablock-F",
        DATA_MATRIX to "DataMatrix",
        CHINESE_SENSIBLE to "Chinese Sensible code",
        MAXI_CODE to "MaxiCode",
        OCR to "OCR",
        DOT_CODE to "DotCode"
    )

    // Lookup function using the map
    fun getSymbologyNameById(id: Int): String {
        return symbologyNameMap[id] ?: ""
    }

    private val symbologyIdByOpticonAndAimIdMap = mapOf(
        Pair("B", "E") to EAN_13,
        Pair("C", "E") to UPC_A,
        Pair("A", "E") to EAN_8,
        Pair("D", "E") to UPC_E,
        Pair("B", "X") to ISBN,
        Pair("V", "A") to CODE_39,
        Pair("R", "F") to CODABAR,
        Pair("O", "S") to DISCRETE_2_OF_5,
        Pair("N", "I") to INTERLEAVED_2_OF_5,
        Pair("U", "G") to CODE_93,
        Pair("T", "C") to CODE_128,
        Pair("Z", "M") to MSI_PLESSEY,
        Pair("P", "R") to IATA,
        Pair("a", "P") to UK_PLESSEY,
        Pair("d", "B") to TELEPEN,
        Pair("Q", "X") to MATRIX_2_OF_5,
        Pair("g", "X") to S_CODE,
        Pair("V", "X") to TRI_OPTIC,
        Pair("W", "A") to CODE_39_FULL_ASCII,
        Pair("Y", "X") to ITALIAN_PHARMACEUTICAL,
        Pair("y", "e") to GS1_DATA_BAR,
        Pair("r", "L") to PDF_417,
        Pair("l", "e") to CC_C,
        Pair("s", "L") to MICRO_PDF_417,
        Pair("m", "e") to CC_A,
        Pair("n", "e") to CC_B,
        Pair("b", "H") to CODE_11,
        Pair("c", "X") to KOREAN_POSTAL,
        Pair("E", "O") to CODABLOCK_F,
        Pair("L", "E") to EAN_13_ADD_ON_2,
        Pair("F", "E") to UPC_A_ADD_ON_2,
        Pair("J", "E") to EAN_8_ADD_ON_2,
        Pair("H", "E") to UPC_E_ADD_ON_2,
        Pair("M", "E") to EAN_13_ADD_ON_5,
        Pair("G", "E") to UPC_A_ADD_ON_5,
        Pair("K", "E") to EAN_8_ADD_ON_5,
        Pair("I", "E") to UPC_E_ADD_ON_5,
        Pair("u", "Q") to QR_CODE,
        Pair("t", "d") to DATA_MATRIX,
        Pair("v", "Q") to MICRO_QR,
        Pair("v", "U") to MAXI_CODE,
        Pair("o", "z") to AZTEC,
        Pair("?", "X") to CHINESE_SENSIBLE
    )

    fun getSymbologyIdByOpticonIdAndAimId(opticonId: String, aimId: String): Int {
        return symbologyIdByOpticonAndAimIdMap[Pair(opticonId, aimId)] ?: -1
    }

    private val symbologyIdByCodeIdMap = mapOf(
        0x01 to EAN_13,
        0x02 to UPC_A,
        0x03 to EAN_8,
        0x04 to UPC_E,
        0x05 to UPC_E1,
        0x06 to ISBN,
        0x07 to ISSN,
        0x08 to ISMN,
        0x09 to CODE_39,
        0x0A to CODABAR,
        0x0B to DISCRETE_2_OF_5,
        0x0C to INTERLEAVED_2_OF_5,
        0x0D to CODE_93,
        0x0E to CODE_128,
        0x0F to MSI_PLESSEY,
        0x10 to IATA,
        0x11 to UK_PLESSEY,
        0x12 to TELEPEN,
        0x13 to MATRIX_2_OF_5,
        0x14 to CHINESE_POST,
        0x15 to CODABAR_ABC,
        0x16 to CODABAR_CX,
        0x17 to S_CODE,
        0x18 to TRI_OPTIC,
        0x19 to CODE_39_FULL_ASCII,
        0x1A to ITALIAN_PHARMACEUTICAL,
        0x1C to GS1_14,
        0x1D to GS1_LIMIT,
        0x1E to GS1_EXP,
        0x1F to PDF_417,
        0x21 to MICRO_PDF_417,
        0x24 to CODE_11,
        0x26 to KOREAN_POSTAL,
        0x27 to CODABLOCK_F,
        0x30 to QR_CODE,
        0x31 to DATA_MATRIX,
        0x32 to MAXI_CODE,
        0x33 to AZTEC,
        0x34 to OCR,
        0x35 to CHINESE_SENSIBLE,
        0x38 to DOT_CODE,
        0x41 to EAN_13_ADD_ON_2,
        0x42 to UPC_A_ADD_ON_2,
        0x43 to EAN_8_ADD_ON_2,
        0x44 to UPC_E_ADD_ON_2,
        0x45 to UPC_E1_ADD_ON_2,
        0x81 to EAN_13_ADD_ON_5,
        0x82 to UPC_A_ADD_ON_5,
        0x83 to EAN_8_ADD_ON_5,
        0x84 to UPC_E_ADD_ON_5,
        0x85 to UPC_E1_ADD_ON_5,
        0xF0 to CODE_39,
        0xF1 to PDF_417,
        0xF2 to CODE_128,
        0xF3 to QR_CODE,
        0xF4 to AZTEC
    )

    fun getSymbologyIdByCodeId(codeId: Int): Int {
        return symbologyIdByCodeIdMap[codeId] ?: 0
    }
}