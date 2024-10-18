package com.opticon.opticonnect.sdk.api.scanner_settings

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.SymbologyType
import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.api.ScannerFeedback
import com.opticon.opticonnect.sdk.api.constants.commands.symbology.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A class representing settings for enabling and disabling symbologies in the scanner.
 *
 * This class provides methods to enable, disable, or enable-only specific symbologies on a scanner.
 */
@Singleton
class Symbology @Inject constructor(
    scannerFeedback: ScannerFeedback
) : SettingsBase(scannerFeedback) {

    // Maps each symbology type to its respective enable command.
    private val enableSymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to ENABLE_ALL_CODES_EXCL_ADDON,
        SymbologyType.ALL_1D_CODES to ENABLE_ALL_1D_CODES_EXCL_ADDON,
        SymbologyType.CODE_11 to ENABLE_CODE_11,
        SymbologyType.CODE_39 to ENABLE_CODE_39,
        SymbologyType.CODE_93 to ENABLE_CODE_93,
        SymbologyType.CODE_128 to ENABLE_CODE_128,
        SymbologyType.CODABAR to ENABLE_CODABAR,
        SymbologyType.EAN_8 to ENABLE_EAN_8,
        SymbologyType.EAN_8_ADD_ON_2 to ENABLE_EAN_8_PLUS_2,
        SymbologyType.EAN_8_ADD_ON_5 to ENABLE_EAN_8_PLUS_5,
        SymbologyType.EAN_13 to ENABLE_EAN_13,
        SymbologyType.EAN_13_ADD_ON_2 to ENABLE_EAN_13_PLUS_2,
        SymbologyType.EAN_13_ADD_ON_5 to ENABLE_EAN_13_PLUS_5,
        SymbologyType.IATA to ENABLE_IATA,
        SymbologyType.INDUSTRIAL_2OF5 to ENABLE_INDUSTRIAL_2OF5,
        SymbologyType.INTERLEAVED_2OF5 to ENABLE_INTERLEAVED_2OF5,
        SymbologyType.MATRIX_2OF5 to ENABLE_MATRIX_2OF5,
        SymbologyType.MSI_PLESSEY to ENABLE_MSI_PLESSEY,
        SymbologyType.S_CODE to ENABLE_S_CODE,
        SymbologyType.TELEPEN to ENABLE_TELEPEN,
        SymbologyType.TRI_OPTIC to ENABLE_TRI_OPTIC,
        SymbologyType.UK_PLESSEY to ENABLE_UK_PLESSEY,
        SymbologyType.UPC_A to ENABLE_UPC_A,
        SymbologyType.UPC_A_ADD_ON_2 to ENABLE_UPC_A_PLUS_2,
        SymbologyType.UPC_A_ADD_ON_5 to ENABLE_UPC_A_PLUS_5,
        SymbologyType.UPC_E to ENABLE_UPC_E,
        SymbologyType.UPC_E_ADD_ON_2 to ENABLE_UPC_E_PLUS_2,
        SymbologyType.UPC_E_ADD_ON_5 to ENABLE_UPC_E_PLUS_5,
        SymbologyType.UPC_E1 to ENABLE_UPC_E1,
        SymbologyType.UPC_E1_ADD_ON_2 to ENABLE_UPC_E1_PLUS_2,
        SymbologyType.UPC_E1_ADD_ON_5 to ENABLE_UPC_E1_PLUS_5,
        SymbologyType.GS1_DATABAR to ENABLE_GS1_DATABAR,
        SymbologyType.GS1_DATABAR_LIMITED to ENABLE_GS1_DATABAR_LIMITED,
        SymbologyType.GS1_DATABAR_EXPANDED to ENABLE_GS1_DATABAR_EXPANDED,
        SymbologyType.ALL_2D_CODES to ENABLE_ALL_2D_CODES,
        SymbologyType.AZTEC_CODE to ENABLE_AZTEC_CODE,
        SymbologyType.AZTEC_RUNES to ENABLE_AZTEC_RUNES,
        SymbologyType.CHINESE_SENSIBLE_CODE to ENABLE_CHINESE_SENSIBLE_CODE,
        SymbologyType.CODABLOCK_F to ENABLE_CODABLOCK_F,
        SymbologyType.DATA_MATRIX to ENABLE_DATA_MATRIX,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to ENABLE_DATA_MATRIX_OLD_ECC000_140,
        SymbologyType.DOT_CODE to ENABLE_DOT_CODE,
        SymbologyType.MAXICODE to ENABLE_MAXICODE,
        SymbologyType.MICRO_PDF_417 to ENABLE_MICRO_PDF_417,
        SymbologyType.MICRO_QR_CODE to ENABLE_MICRO_QR_CODE,
        SymbologyType.PDF_417 to ENABLE_PDF_417,
        SymbologyType.QR_CODE to ENABLE_QR_CODE,
        SymbologyType.AUSTRALIAN_POSTAL to ENABLE_AUSTRALIAN_POSTAL,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to ENABLE_CHINESE_POST_MATRIX_2OF5,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to ENABLE_INTELLIGENT_MAIL_BARCODE,
        SymbologyType.JAPANESE_POSTAL to ENABLE_JAPANESE_POSTAL,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to ENABLE_KOREAN_POSTAL_AUTHORITY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to ENABLE_MAILMARK_4_STATE_POSTAL,
        SymbologyType.NETHERLANDS_KIX_CODE to ENABLE_NETHERLANDS_KIX_CODE,
        SymbologyType.PLANET to ENABLE_PLANET,
        SymbologyType.POSTNET to ENABLE_POSTNET,
        SymbologyType.UK_POSTAL to ENABLE_UK_POSTAL
    )

    // Maps each symbology type to its respective disable command.
    private val disableSymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to DISABLE_ALL_CODES,
        SymbologyType.ALL_1D_CODES to DISABLE_ALL_1D_CODES,
        SymbologyType.CODE_11 to DISABLE_CODE_11,
        SymbologyType.CODE_39 to DISABLE_CODE_39,
        SymbologyType.CODE_93 to DISABLE_CODE_93,
        SymbologyType.CODE_128 to DISABLE_CODE_128,
        SymbologyType.CODABAR to DISABLE_CODABAR,
        SymbologyType.EAN_8 to DISABLE_EAN_8,
        SymbologyType.EAN_8_ADD_ON_2 to DISABLE_EAN_8_PLUS_2,
        SymbologyType.EAN_8_ADD_ON_5 to DISABLE_EAN_8_PLUS_5,
        SymbologyType.EAN_13 to DISABLE_EAN_13,
        SymbologyType.EAN_13_ADD_ON_2 to DISABLE_EAN_13_PLUS_2,
        SymbologyType.EAN_13_ADD_ON_5 to DISABLE_EAN_13_PLUS_5,
        SymbologyType.IATA to DISABLE_IATA,
        SymbologyType.INDUSTRIAL_2OF5 to DISABLE_INDUSTRIAL_2OF5,
        SymbologyType.INTERLEAVED_2OF5 to DISABLE_INTERLEAVED_2OF5,
        SymbologyType.MATRIX_2OF5 to DISABLE_MATRIX_2OF5,
        SymbologyType.MSI_PLESSEY to DISABLE_MSI_PLESSEY,
        SymbologyType.S_CODE to DISABLE_S_CODE,
        SymbologyType.TELEPEN to DISABLE_TELEPEN,
        SymbologyType.TRI_OPTIC to DISABLE_TRI_OPTIC,
        SymbologyType.UK_PLESSEY to DISABLE_UK_PLESSEY,
        SymbologyType.UPC_A to DISABLE_UPC_A,
        SymbologyType.UPC_A_ADD_ON_2 to DISABLE_UPC_A_PLUS_2,
        SymbologyType.UPC_A_ADD_ON_5 to DISABLE_UPC_A_PLUS_5,
        SymbologyType.UPC_E to DISABLE_UPC_E,
        SymbologyType.UPC_E_ADD_ON_2 to DISABLE_UPC_E_PLUS_2,
        SymbologyType.UPC_E_ADD_ON_5 to DISABLE_UPC_E_PLUS_5,
        SymbologyType.UPC_E1 to DISABLE_UPC_E1,
        SymbologyType.UPC_E1_ADD_ON_2 to DISABLE_UPC_E1_PLUS_2,
        SymbologyType.UPC_E1_ADD_ON_5 to DISABLE_UPC_E1_PLUS_5,
        SymbologyType.GS1_DATABAR to DISABLE_GS1_DATABAR,
        SymbologyType.GS1_DATABAR_LIMITED to DISABLE_GS1_DATABAR_LIMITED,
        SymbologyType.GS1_DATABAR_EXPANDED to DISABLE_GS1_DATABAR_EXPANDED,
        SymbologyType.ALL_2D_CODES to DISABLE_ALL_2D_CODES,
        SymbologyType.AZTEC_CODE to DISABLE_AZTEC_CODE,
        SymbologyType.AZTEC_RUNES to DISABLE_AZTEC_RUNES,
        SymbologyType.CHINESE_SENSIBLE_CODE to DISABLE_CHINESE_SENSIBLE_CODE,
        SymbologyType.CODABLOCK_F to DISABLE_CODABLOCK_F,
        SymbologyType.DATA_MATRIX to DISABLE_DATA_MATRIX,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to DISABLE_DATA_MATRIX_OLD_ECC000_140,
        SymbologyType.DOT_CODE to DISABLE_DOT_CODE,
        SymbologyType.MAXICODE to DISABLE_MAXICODE,
        SymbologyType.MICRO_PDF_417 to DISABLE_MICRO_PDF_417,
        SymbologyType.MICRO_QR_CODE to DISABLE_MICRO_QR_CODE,
        SymbologyType.PDF_417 to DISABLE_PDF_417,
        SymbologyType.QR_CODE to DISABLE_QR_CODE,
        SymbologyType.AUSTRALIAN_POSTAL to DISABLE_AUSTRALIAN_POSTAL,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to DISABLE_CHINESE_POST_MATRIX_2OF5,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to DISABLE_INTELLIGENT_MAIL_BARCODE,
        SymbologyType.JAPANESE_POSTAL to DISABLE_JAPANESE_POSTAL,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to DISABLE_KOREAN_POSTAL_AUTHORITY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to DISABLE_MAILMARK_4_STATE_POSTAL,
        SymbologyType.NETHERLANDS_KIX_CODE to DISABLE_NETHERLANDS_KIX_CODE,
        SymbologyType.PLANET to DISABLE_PLANET,
        SymbologyType.POSTNET to DISABLE_POSTNET,
        SymbologyType.UK_POSTAL to DISABLE_UK_POSTAL
    )

    // Maps each symbology type to its respective enable-only command.
    private val enableOnlySymbologyCommands: Map<SymbologyType, String> = mapOf(
        SymbologyType.ALL_CODES to ENABLE_ALL_CODES_EXCL_ADDON,
        SymbologyType.ALL_1D_CODES to ENABLE_1D_ALL_CODES_EXCL_ADDON_ONLY,
        SymbologyType.CODE_11 to ENABLE_CODE_11_ONLY,
        SymbologyType.CODE_39 to ENABLE_CODE_39_ONLY,
        SymbologyType.CODE_93 to ENABLE_CODE_93_ONLY,
        SymbologyType.CODE_128 to ENABLE_CODE_128_ONLY,
        SymbologyType.CODABAR to ENABLE_CODABAR_ONLY,
        SymbologyType.EAN_8 to ENABLE_EAN_8_ONLY,
        SymbologyType.EAN_8_ADD_ON_2 to ENABLE_EAN_8_PLUS_2_ONLY,
        SymbologyType.EAN_8_ADD_ON_5 to ENABLE_EAN_8_PLUS_5_ONLY,
        SymbologyType.EAN_13 to ENABLE_EAN_13_ONLY,
        SymbologyType.EAN_13_ADD_ON_2 to ENABLE_EAN_13_PLUS_2_ONLY,
        SymbologyType.EAN_13_ADD_ON_5 to ENABLE_EAN_13_PLUS_5_ONLY,
        SymbologyType.IATA to ENABLE_IATA_ONLY,
        SymbologyType.INDUSTRIAL_2OF5 to ENABLE_INDUSTRIAL_2OF5_ONLY,
        SymbologyType.INTERLEAVED_2OF5 to ENABLE_INTERLEAVED_2OF5_ONLY,
        SymbologyType.MATRIX_2OF5 to ENABLE_MATRIX_2OF5_ONLY,
        SymbologyType.MSI_PLESSEY to ENABLE_MSI_PLESSEY_ONLY,
        SymbologyType.S_CODE to ENABLE_S_CODE_ONLY,
        SymbologyType.TELEPEN to ENABLE_TELEPEN_ONLY,
        SymbologyType.TRI_OPTIC to ENABLE_TRI_OPTIC_ONLY,
        SymbologyType.UK_PLESSEY to ENABLE_UK_PLESSEY_ONLY,
        SymbologyType.UPC_A to ENABLE_UPC_A_ONLY,
        SymbologyType.UPC_A_ADD_ON_2 to ENABLE_UPC_A_PLUS_2_ONLY,
        SymbologyType.UPC_A_ADD_ON_5 to ENABLE_UPC_A_PLUS_5_ONLY,
        SymbologyType.UPC_E to ENABLE_UPC_E_ONLY,
        SymbologyType.UPC_E_ADD_ON_2 to ENABLE_UPC_E_PLUS_2_ONLY,
        SymbologyType.UPC_E_ADD_ON_5 to ENABLE_UPC_E_PLUS_5_ONLY,
        SymbologyType.UPC_E1 to ENABLE_UPC_E1_ONLY,
        SymbologyType.UPC_E1_ADD_ON_2 to ENABLE_UPC_E1_PLUS_2_ONLY,
        SymbologyType.UPC_E1_ADD_ON_5 to ENABLE_UPC_E1_PLUS_5_ONLY,
        SymbologyType.GS1_DATABAR to ENABLE_GS1_DATABAR_ONLY,
        SymbologyType.GS1_DATABAR_LIMITED to ENABLE_GS1_DATABAR_LIMITED_ONLY,
        SymbologyType.GS1_DATABAR_EXPANDED to ENABLE_GS1_DATABAR_EXPANDED_ONLY,
        SymbologyType.ALL_2D_CODES to ENABLE_2D_ALL_CODES_ONLY,
        SymbologyType.AZTEC_CODE to ENABLE_AZTEC_CODE_ONLY,
        SymbologyType.AZTEC_RUNES to ENABLE_AZTEC_RUNES_ONLY,
        SymbologyType.CHINESE_SENSIBLE_CODE to ENABLE_CHINESE_SENSIBLE_CODE_ONLY,
        SymbologyType.CODABLOCK_F to ENABLE_CODABLOCK_F_ONLY,
        SymbologyType.DATA_MATRIX to ENABLE_DATA_MATRIX_ONLY,
        SymbologyType.DATA_MATRIX_OLD_ECC000_140 to ENABLE_DATA_MATRIX_OLD_ECC000_140_ONLY,
        SymbologyType.DOT_CODE to ENABLE_DOT_CODE_ONLY,
        SymbologyType.MAXICODE to ENABLE_MAXICODE_ONLY,
        SymbologyType.MICRO_PDF_417 to ENABLE_MICRO_PDF_417_ONLY,
        SymbologyType.MICRO_QR_CODE to ENABLE_MICRO_QR_CODE_ONLY,
        SymbologyType.PDF_417 to ENABLE_PDF_417_ONLY,
        SymbologyType.QR_CODE to ENABLE_QR_CODE_ONLY,
        SymbologyType.AUSTRALIAN_POSTAL to ENABLE_AUSTRALIAN_POSTAL_ONLY,
        SymbologyType.CHINESE_POST_MATRIX_2OF5 to ENABLE_CHINESE_POST_MATRIX_2OF5_ONLY,
        SymbologyType.INTELLIGENT_MAIL_BARCODE to ENABLE_INTELLIGENT_MAIL_BARCODE_ONLY,
        SymbologyType.JAPANESE_POSTAL to ENABLE_JAPANESE_POSTAL_ONLY,
        SymbologyType.KOREAN_POSTAL_AUTHORITY to ENABLE_KOREAN_POSTAL_AUTHORITY_ONLY,
        SymbologyType.MAILMARK_4_STATE_POSTAL to ENABLE_MAILMARK_4_STATE_POSTAL_ONLY,
        SymbologyType.NETHERLANDS_KIX_CODE to ENABLE_NETHERLANDS_KIX_CODE_ONLY,
        SymbologyType.PLANET to ENABLE_PLANET_ONLY,
        SymbologyType.POSTNET to ENABLE_POSTNET_ONLY,
        SymbologyType.UK_POSTAL to ENABLE_UK_POSTAL_ONLY
    )

    /**
     * Enables a specific symbology on the device.
     *
     * @param deviceId The ID of the device where the symbology should be enabled.
     * @param type The symbology to enable exclusively.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun enableOnlySymbology(deviceId: String, type: SymbologyType): CommandResponse {
        val command = enableOnlySymbologyCommands[type]

        return if (command != null) {
            sendCommand(deviceId, command)
        } else {
            val msg = "Command not found for $type"
            Timber.e(msg)
            CommandResponse.failed(msg)
        }
    }

    /**
     * Toggles a specific symbology on the device based on the [enabled] flag.
     *
     * @param deviceId The ID of the device where the symbology should be enabled or disabled.
     * @param type The symbology to enable or disable.
     * @param enabled A boolean flag indicating whether to enable (true) or disable (false) the symbology.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSymbology(deviceId: String, type: SymbologyType, enabled: Boolean): CommandResponse {
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
