package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.ScannerFeedback
import com.opticon.opticonnect.sdk.api.constants.commands.symbology.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.SymbologyType
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Symbology
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SymbologyImpl @Inject constructor(
    scannerFeedback: ScannerFeedback
) : SettingsBase(scannerFeedback), Symbology {

    // Maps each symbology type to its respective enable command.
    private val enableSymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to SymbologyCommands.ENABLE_ALL_CODES_EXCL_ADDON,
        SymbologyType.ALL_1D_CODES to SymbologyCommands.ENABLE_ALL_1D_CODES_EXCL_ADDON,
        SymbologyType.CODE_11 to SymbologyCommands.ENABLE_CODE_11,
        SymbologyType.CODE_39 to SymbologyCommands.ENABLE_CODE_39,
        SymbologyType.CODE_93 to SymbologyCommands.ENABLE_CODE_93,
        SymbologyType.CODE_128 to SymbologyCommands.ENABLE_CODE_128,
        SymbologyType.CODABAR to SymbologyCommands.ENABLE_CODABAR,
        SymbologyType.EAN_8 to SymbologyCommands.ENABLE_EAN_8,
        SymbologyType.EAN_8_ADD_ON_2 to SymbologyCommands.ENABLE_EAN_8_PLUS_2,
        SymbologyType.EAN_8_ADD_ON_5 to SymbologyCommands.ENABLE_EAN_8_PLUS_5,
        SymbologyType.EAN_13 to SymbologyCommands.ENABLE_EAN_13,
        SymbologyType.EAN_13_ADD_ON_2 to SymbologyCommands.ENABLE_EAN_13_PLUS_2,
        SymbologyType.EAN_13_ADD_ON_5 to SymbologyCommands.ENABLE_EAN_13_PLUS_5,
        SymbologyType.IATA to SymbologyCommands.ENABLE_IATA,
        SymbologyType.INDUSTRIAL_2OF5 to SymbologyCommands.ENABLE_INDUSTRIAL_2OF5,
        SymbologyType.INTERLEAVED_2OF5 to SymbologyCommands.ENABLE_INTERLEAVED_2OF5,
        SymbologyType.MATRIX_2OF5 to SymbologyCommands.ENABLE_MATRIX_2OF5,
        SymbologyType.MSI_PLESSEY to SymbologyCommands.ENABLE_MSI_PLESSEY,
        SymbologyType.S_CODE to SymbologyCommands.ENABLE_S_CODE,
        SymbologyType.TELEPEN to SymbologyCommands.ENABLE_TELEPEN,
        SymbologyType.TRI_OPTIC to SymbologyCommands.ENABLE_TRI_OPTIC,
        SymbologyType.UK_PLESSEY to SymbologyCommands.ENABLE_UK_PLESSEY,
        SymbologyType.UPC_A to SymbologyCommands.ENABLE_UPC_A,
        SymbologyType.UPC_A_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_A_PLUS_2,
        SymbologyType.UPC_A_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_A_PLUS_5,
        SymbologyType.UPC_E to SymbologyCommands.ENABLE_UPC_E,
        SymbologyType.UPC_E_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_E_PLUS_2,
        SymbologyType.UPC_E_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_E_PLUS_5,
        SymbologyType.UPC_E1 to SymbologyCommands.ENABLE_UPC_E1,
        SymbologyType.UPC_E1_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_E1_PLUS_2,
        SymbologyType.UPC_E1_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_E1_PLUS_5,
        SymbologyType.GS1_DATABAR to SymbologyCommands.ENABLE_GS1_DATABAR,
        SymbologyType.GS1_DATABAR_LIMITED to SymbologyCommands.ENABLE_GS1_DATABAR_LIMITED,
        SymbologyType.GS1_DATABAR_EXPANDED to SymbologyCommands.ENABLE_GS1_DATABAR_EXPANDED,
        SymbologyType.ALL_2D_CODES to SymbologyCommands.ENABLE_ALL_2D_CODES,
        SymbologyType.AZTEC_CODE to SymbologyCommands.ENABLE_AZTEC_CODE,
        SymbologyType.AZTEC_RUNES to SymbologyCommands.ENABLE_AZTEC_RUNES,
        SymbologyType.CHINESE_SENSIBLE_CODE to SymbologyCommands.ENABLE_CHINESE_SENSIBLE_CODE,
        SymbologyType.CODABLOCK_F to SymbologyCommands.ENABLE_CODABLOCK_F,
        SymbologyType.DATA_MATRIX to SymbologyCommands.ENABLE_DATA_MATRIX,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to SymbologyCommands.ENABLE_DATA_MATRIX_OLD_ECC000_140,
        SymbologyType.DOT_CODE to SymbologyCommands.ENABLE_DOT_CODE,
        SymbologyType.MAXICODE to SymbologyCommands.ENABLE_MAXICODE,
        SymbologyType.MICRO_PDF_417 to SymbologyCommands.ENABLE_MICRO_PDF_417,
        SymbologyType.MICRO_QR_CODE to SymbologyCommands.ENABLE_MICRO_QR_CODE,
        SymbologyType.PDF_417 to SymbologyCommands.ENABLE_PDF_417,
        SymbologyType.QR_CODE to SymbologyCommands.ENABLE_QR_CODE,
        SymbologyType.AUSTRALIAN_POSTAL to SymbologyCommands.ENABLE_AUSTRALIAN_POSTAL,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to SymbologyCommands.ENABLE_CHINESE_POST_MATRIX_2OF5,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to SymbologyCommands.ENABLE_INTELLIGENT_MAIL_BARCODE,
        SymbologyType.JAPANESE_POSTAL to SymbologyCommands.ENABLE_JAPANESE_POSTAL,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to SymbologyCommands.ENABLE_KOREAN_POSTAL_AUTHORITY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to SymbologyCommands.ENABLE_MAILMARK_4_STATE_POSTAL,
        SymbologyType.NETHERLANDS_KIX_CODE to SymbologyCommands.ENABLE_NETHERLANDS_KIX_CODE,
        SymbologyType.PLANET to SymbologyCommands.ENABLE_PLANET,
        SymbologyType.POSTNET to SymbologyCommands.ENABLE_POSTNET,
        SymbologyType.UK_POSTAL to SymbologyCommands.ENABLE_UK_POSTAL
    )

    // Maps each symbology type to its respective disable command.
    private val disableSymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to SymbologyCommands.DISABLE_ALL_CODES,
        SymbologyType.ALL_1D_CODES to SymbologyCommands.DISABLE_ALL_1D_CODES,
        SymbologyType.CODE_11 to SymbologyCommands.DISABLE_CODE_11,
        SymbologyType.CODE_39 to SymbologyCommands.DISABLE_CODE_39,
        SymbologyType.CODE_93 to SymbologyCommands.DISABLE_CODE_93,
        SymbologyType.CODE_128 to SymbologyCommands.DISABLE_CODE_128,
        SymbologyType.CODABAR to SymbologyCommands.DISABLE_CODABAR,
        SymbologyType.EAN_8 to SymbologyCommands.DISABLE_EAN_8,
        SymbologyType.EAN_8_ADD_ON_2 to SymbologyCommands.DISABLE_EAN_8_PLUS_2,
        SymbologyType.EAN_8_ADD_ON_5 to SymbologyCommands.DISABLE_EAN_8_PLUS_5,
        SymbologyType.EAN_13 to SymbologyCommands.DISABLE_EAN_13,
        SymbologyType.EAN_13_ADD_ON_2 to SymbologyCommands.DISABLE_EAN_13_PLUS_2,
        SymbologyType.EAN_13_ADD_ON_5 to SymbologyCommands.DISABLE_EAN_13_PLUS_5,
        SymbologyType.IATA to SymbologyCommands.DISABLE_IATA,
        SymbologyType.INDUSTRIAL_2OF5 to SymbologyCommands.DISABLE_INDUSTRIAL_2OF5,
        SymbologyType.INTERLEAVED_2OF5 to SymbologyCommands.DISABLE_INTERLEAVED_2OF5,
        SymbologyType.MATRIX_2OF5 to SymbologyCommands.DISABLE_MATRIX_2OF5,
        SymbologyType.MSI_PLESSEY to SymbologyCommands.DISABLE_MSI_PLESSEY,
        SymbologyType.S_CODE to SymbologyCommands.DISABLE_S_CODE,
        SymbologyType.TELEPEN to SymbologyCommands.DISABLE_TELEPEN,
        SymbologyType.TRI_OPTIC to SymbologyCommands.DISABLE_TRI_OPTIC,
        SymbologyType.UK_PLESSEY to SymbologyCommands.DISABLE_UK_PLESSEY,
        SymbologyType.UPC_A to SymbologyCommands.DISABLE_UPC_A,
        SymbologyType.UPC_A_ADD_ON_2 to SymbologyCommands.DISABLE_UPC_A_PLUS_2,
        SymbologyType.UPC_A_ADD_ON_5 to SymbologyCommands.DISABLE_UPC_A_PLUS_5,
        SymbologyType.UPC_E to SymbologyCommands.DISABLE_UPC_E,
        SymbologyType.UPC_E_ADD_ON_2 to SymbologyCommands.DISABLE_UPC_E_PLUS_2,
        SymbologyType.UPC_E_ADD_ON_5 to SymbologyCommands.DISABLE_UPC_E_PLUS_5,
        SymbologyType.UPC_E1 to SymbologyCommands.DISABLE_UPC_E1,
        SymbologyType.UPC_E1_ADD_ON_2 to SymbologyCommands.DISABLE_UPC_E1_PLUS_2,
        SymbologyType.UPC_E1_ADD_ON_5 to SymbologyCommands.DISABLE_UPC_E1_PLUS_5,
        SymbologyType.GS1_DATABAR to SymbologyCommands.DISABLE_GS1_DATABAR,
        SymbologyType.GS1_DATABAR_LIMITED to SymbologyCommands.DISABLE_GS1_DATABAR_LIMITED,
        SymbologyType.GS1_DATABAR_EXPANDED to SymbologyCommands.DISABLE_GS1_DATABAR_EXPANDED,
        SymbologyType.ALL_2D_CODES to SymbologyCommands.DISABLE_ALL_2D_CODES,
        SymbologyType.AZTEC_CODE to SymbologyCommands.DISABLE_AZTEC_CODE,
        SymbologyType.AZTEC_RUNES to SymbologyCommands.DISABLE_AZTEC_RUNES,
        SymbologyType.CHINESE_SENSIBLE_CODE to SymbologyCommands.DISABLE_CHINESE_SENSIBLE_CODE,
        SymbologyType.CODABLOCK_F to SymbologyCommands.DISABLE_CODABLOCK_F,
        SymbologyType.DATA_MATRIX to SymbologyCommands.DISABLE_DATA_MATRIX,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to SymbologyCommands.DISABLE_DATA_MATRIX_OLD_ECC000_140,
        SymbologyType.DOT_CODE to SymbologyCommands.DISABLE_DOT_CODE,
        SymbologyType.MAXICODE to SymbologyCommands.DISABLE_MAXICODE,
        SymbologyType.MICRO_PDF_417 to SymbologyCommands.DISABLE_MICRO_PDF_417,
        SymbologyType.MICRO_QR_CODE to SymbologyCommands.DISABLE_MICRO_QR_CODE,
        SymbologyType.PDF_417 to SymbologyCommands.DISABLE_PDF_417,
        SymbologyType.QR_CODE to SymbologyCommands.DISABLE_QR_CODE,
        SymbologyType.AUSTRALIAN_POSTAL to SymbologyCommands.DISABLE_AUSTRALIAN_POSTAL,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to SymbologyCommands.DISABLE_CHINESE_POST_MATRIX_2OF5,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to SymbologyCommands.DISABLE_INTELLIGENT_MAIL_BARCODE,
        SymbologyType.JAPANESE_POSTAL to SymbologyCommands.DISABLE_JAPANESE_POSTAL,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to SymbologyCommands.DISABLE_KOREAN_POSTAL_AUTHORITY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to SymbologyCommands.DISABLE_MAILMARK_4_STATE_POSTAL,
        SymbologyType.NETHERLANDS_KIX_CODE to SymbologyCommands.DISABLE_NETHERLANDS_KIX_CODE,
        SymbologyType.PLANET to SymbologyCommands.DISABLE_PLANET,
        SymbologyType.POSTNET to SymbologyCommands.DISABLE_POSTNET,
        SymbologyType.UK_POSTAL to SymbologyCommands.DISABLE_UK_POSTAL
    )

    // Maps each symbology type to its respective enable-only command.
    private val enableOnlySymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to SymbologyCommands.ENABLE_ALL_CODES_EXCL_ADDON,
        SymbologyType.ALL_1D_CODES to SymbologyCommands.ENABLE_ALL_1D_CODES_EXCL_ADDON_ONLY,
        SymbologyType.CODE_11 to SymbologyCommands.ENABLE_CODE_11_ONLY,
        SymbologyType.CODE_39 to SymbologyCommands.ENABLE_CODE_39_ONLY,
        SymbologyType.CODE_93 to SymbologyCommands.ENABLE_CODE_93_ONLY,
        SymbologyType.CODE_128 to SymbologyCommands.ENABLE_CODE_128_ONLY,
        SymbologyType.CODABAR to SymbologyCommands.ENABLE_CODABAR_ONLY,
        SymbologyType.EAN_8 to SymbologyCommands.ENABLE_EAN_8_ONLY,
        SymbologyType.EAN_8_ADD_ON_2 to SymbologyCommands.ENABLE_EAN_8_PLUS_2_ONLY,
        SymbologyType.EAN_8_ADD_ON_5 to SymbologyCommands.ENABLE_EAN_8_PLUS_5_ONLY,
        SymbologyType.EAN_13 to SymbologyCommands.ENABLE_EAN_13_ONLY,
        SymbologyType.EAN_13_ADD_ON_2 to SymbologyCommands.ENABLE_EAN_13_PLUS_2_ONLY,
        SymbologyType.EAN_13_ADD_ON_5 to SymbologyCommands.ENABLE_EAN_13_PLUS_5_ONLY,
        SymbologyType.IATA to SymbologyCommands.ENABLE_IATA_ONLY,
        SymbologyType.INDUSTRIAL_2OF5 to SymbologyCommands.ENABLE_INDUSTRIAL_2OF5_ONLY,
        SymbologyType.INTERLEAVED_2OF5 to SymbologyCommands.ENABLE_INTERLEAVED_2OF5_ONLY,
        SymbologyType.MATRIX_2OF5 to SymbologyCommands.ENABLE_MATRIX_2OF5_ONLY,
        SymbologyType.MSI_PLESSEY to SymbologyCommands.ENABLE_MSI_PLESSEY_ONLY,
        SymbologyType.S_CODE to SymbologyCommands.ENABLE_S_CODE_ONLY,
        SymbologyType.TELEPEN to SymbologyCommands.ENABLE_TELEPEN_ONLY,
        SymbologyType.TRI_OPTIC to SymbologyCommands.ENABLE_TRI_OPTIC_ONLY,
        SymbologyType.UK_PLESSEY to SymbologyCommands.ENABLE_UK_PLESSEY_ONLY,
        SymbologyType.UPC_A to SymbologyCommands.ENABLE_UPC_A_ONLY,
        SymbologyType.UPC_A_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_A_PLUS_2_ONLY,
        SymbologyType.UPC_A_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_A_PLUS_5_ONLY,
        SymbologyType.UPC_E to SymbologyCommands.ENABLE_UPC_E_ONLY,
        SymbologyType.UPC_E_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_E_PLUS_2_ONLY,
        SymbologyType.UPC_E_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_E_PLUS_5_ONLY,
        SymbologyType.UPC_E1 to SymbologyCommands.ENABLE_UPC_E1_ONLY,
        SymbologyType.UPC_E1_ADD_ON_2 to SymbologyCommands.ENABLE_UPC_E1_PLUS_2_ONLY,
        SymbologyType.UPC_E1_ADD_ON_5 to SymbologyCommands.ENABLE_UPC_E1_PLUS_5_ONLY,
        SymbologyType.GS1_DATABAR to SymbologyCommands.ENABLE_GS1_DATABAR_ONLY,
        SymbologyType.GS1_DATABAR_LIMITED to SymbologyCommands.ENABLE_GS1_DATABAR_LIMITED_ONLY,
        SymbologyType.GS1_DATABAR_EXPANDED to SymbologyCommands.ENABLE_GS1_DATABAR_EXPANDED_ONLY,
        SymbologyType.ALL_2D_CODES to SymbologyCommands.ENABLE_ALL_2D_CODES_ONLY,
        SymbologyType.AZTEC_CODE to SymbologyCommands.ENABLE_AZTEC_CODE_ONLY,
        SymbologyType.AZTEC_RUNES to SymbologyCommands.ENABLE_AZTEC_RUNES_ONLY,
        SymbologyType.CHINESE_SENSIBLE_CODE to SymbologyCommands.ENABLE_CHINESE_SENSIBLE_CODE_ONLY,
        SymbologyType.CODABLOCK_F to SymbologyCommands.ENABLE_CODABLOCK_F_ONLY,
        SymbologyType.DATA_MATRIX to SymbologyCommands.ENABLE_DATA_MATRIX_ONLY,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to SymbologyCommands.ENABLE_DATA_MATRIX_OLD_ECC000_140_ONLY,
        SymbologyType.DOT_CODE to SymbologyCommands.ENABLE_DOT_CODE_ONLY,
        SymbologyType.MAXICODE to SymbologyCommands.ENABLE_MAXICODE_ONLY,
        SymbologyType.MICRO_PDF_417 to SymbologyCommands.ENABLE_MICRO_PDF_417_ONLY,
        SymbologyType.MICRO_QR_CODE to SymbologyCommands.ENABLE_MICRO_QR_CODE_ONLY,
        SymbologyType.PDF_417 to SymbologyCommands.ENABLE_PDF_417_ONLY,
        SymbologyType.QR_CODE to SymbologyCommands.ENABLE_QR_CODE_ONLY,
        SymbologyType.AUSTRALIAN_POSTAL to SymbologyCommands.ENABLE_AUSTRALIAN_POSTAL_ONLY,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to SymbologyCommands.ENABLE_CHINESE_POST_MATRIX_2OF5_ONLY,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to SymbologyCommands.ENABLE_INTELLIGENT_MAIL_BARCODE_ONLY,
        SymbologyType.JAPANESE_POSTAL to SymbologyCommands.ENABLE_JAPANESE_POSTAL_ONLY,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to SymbologyCommands.ENABLE_KOREAN_POSTAL_AUTHORITY_ONLY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to SymbologyCommands.ENABLE_MAILMARK_4_STATE_POSTAL_ONLY,
        SymbologyType.NETHERLANDS_KIX_CODE to SymbologyCommands.ENABLE_NETHERLANDS_KIX_CODE_ONLY,
        SymbologyType.PLANET to SymbologyCommands.ENABLE_PLANET_ONLY,
        SymbologyType.POSTNET to SymbologyCommands.ENABLE_POSTNET_ONLY,
        SymbologyType.UK_POSTAL to SymbologyCommands.ENABLE_UK_POSTAL_ONLY
    )

    override suspend fun enableOnlySymbology(deviceId: String, type: SymbologyType): CommandResponse {
        val command = enableOnlySymbologyCommands[type]

        return if (command != null) {
            sendCommand(deviceId, command)
        } else {
            val msg = "Command not found for $type"
            Timber.e(msg)
            CommandResponse.failed(msg)
        }
    }

    override suspend fun setSymbology(deviceId: String, type: SymbologyType, enabled: Boolean): CommandResponse {
        val command = if (enabled) {
            enableSymbologyCommands[type]
        } else {
            disableSymbologyCommands[type]
        }

        return if (command != null) {
            sendCommand(deviceId, command)
        } else {
            val msg = "Command not found for $type"
            Timber.e(msg)
            CommandResponse.failed(msg)
        }
    }
}
