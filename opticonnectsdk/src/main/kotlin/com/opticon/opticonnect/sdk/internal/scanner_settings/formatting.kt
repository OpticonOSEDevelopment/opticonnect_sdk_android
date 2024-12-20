package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.FormattingCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.enums.FormattableSymbology
import com.opticon.opticonnect.sdk.internal.interfaces.DirectInputKeysHelper
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Formatting
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FormattingImpl @Inject constructor(
    private val directInputKeysHelper: DirectInputKeysHelper,
) : Formatting, SettingsBase() {

    private val prefixSymbologyMap = mapOf(
        FormattableSymbology.ALL_CODES to FormattingCommands.PREFIX_ALL_CODES,
        FormattableSymbology.UPC_A to FormattingCommands.UPC_A_PREFIX,
        FormattableSymbology.UPC_A_ADD_ON to FormattingCommands.UPC_A_ADDON_PREFIX,
        FormattableSymbology.UPC_E to FormattingCommands.UPC_E_PREFIX,
        FormattableSymbology.UPC_E_ADD_ON to FormattingCommands.UPC_E_ADDON_PREFIX,
        FormattableSymbology.EAN_13 to FormattingCommands.EAN_13_PREFIX,
        FormattableSymbology.EAN_13_ADD_ON to FormattingCommands.EAN_13_ADDON_PREFIX,
        FormattableSymbology.EAN_8 to FormattingCommands.EAN_8_PREFIX,
        FormattableSymbology.EAN_8_ADD_ON to FormattingCommands.EAN_8_ADDON_PREFIX,
        FormattableSymbology.CODE_39 to FormattingCommands.CODE39_PREFIX,
        FormattableSymbology.CODABAR to FormattingCommands.CODABAR_PREFIX,
        FormattableSymbology.INDUSTRIAL_2OF5 to FormattingCommands.INDUSTRIAL_2OF5_PREFIX,
        FormattableSymbology.INTERLEAVED_2OF5 to FormattingCommands.INTERLEAVED_2OF5_PREFIX,
        FormattableSymbology.S_CODE to FormattingCommands.S_CODE_PREFIX,
        FormattableSymbology.MATRIX_2OF5 to FormattingCommands.MATRIX_2OF5_PREFIX,
        FormattableSymbology.IATA to FormattingCommands.IATA_PREFIX,
        FormattableSymbology.CODE_93 to FormattingCommands.CODE93_PREFIX,
        FormattableSymbology.CODE_128 to FormattingCommands.CODE128_PREFIX,
        FormattableSymbology.GS1_128 to FormattingCommands.GS1_128_PREFIX,
        FormattableSymbology.MSI_PLESSEY to FormattingCommands.MSI_PLESSEY_PREFIX,
        FormattableSymbology.TELEPEN to FormattingCommands.TELEPEN_PREFIX,
        FormattableSymbology.UK_PLESSEY to FormattingCommands.UK_PLESSEY_PREFIX,
        FormattableSymbology.DATA_MATRIX to FormattingCommands.DATA_MATRIX_PREFIX,
        FormattableSymbology.QR_CODE to FormattingCommands.QR_CODE_PREFIX,
        FormattableSymbology.MAXICODE to FormattingCommands.MAXICODE_PREFIX,
        FormattableSymbology.PDF_417 to FormattingCommands.PDF417_PREFIX,
        FormattableSymbology.MICRO_PDF_417 to FormattingCommands.MICRO_PDF417_PREFIX,
        FormattableSymbology.AZTEC to FormattingCommands.AZTEC_PREFIX,
        FormattableSymbology.CODE_11 to FormattingCommands.CODE11_PREFIX,
        FormattableSymbology.TRI_OPTIC to FormattingCommands.TRI_OPTIC_PREFIX,
        FormattableSymbology.KOREAN_POSTAL_AUTHORITY to FormattingCommands.KOREAN_POSTAL_AUTHORITY_PREFIX,
        FormattableSymbology.DOT_CODE to FormattingCommands.DOT_CODE_PREFIX,
        FormattableSymbology.INTELLIGENT_MAIL to FormattingCommands.INTELLIGENT_MAIL_PREFIX,
        FormattableSymbology.POSTNET to FormattingCommands.POSTNET_PREFIX,
        FormattableSymbology.PLANET to FormattingCommands.PLANET_PREFIX,
        FormattableSymbology.JAPANESE_POSTAL to FormattingCommands.JAPANESE_POSTAL_PREFIX,
        FormattableSymbology.NETHERLANDS_KIX to FormattingCommands.NETHERLANDS_KIX_PREFIX,
        FormattableSymbology.UK_POSTAL to FormattingCommands.UK_POSTAL_PREFIX,
        FormattableSymbology.AUSTRALIAN_POSTAL to FormattingCommands.AUSTRALIAN_POSTAL_PREFIX,
        FormattableSymbology.MAIL_MARK_4_STATE_POSTAL to FormattingCommands.MAILMARK_4_STATE_POSTAL_PREFIX,
        FormattableSymbology.GS1_DATABAR_OMNIDIRECTIONAL to FormattingCommands.GS1_DATABAR_OMNIDIRECTIONAL_PREFIX,
        FormattableSymbology.GS1_DATABAR_LIMITED to FormattingCommands.GS1_DATABAR_LIMITED_PREFIX,
        FormattableSymbology.GS1_DATABAR_EXPANDED to FormattingCommands.GS1_DATABAR_EXPANDED_PREFIX,
        FormattableSymbology.GS1_COMPOSITE_CODE to FormattingCommands.GS1_COMPOSITE_CODE_PREFIX,
        FormattableSymbology.CODABLOCK_F to FormattingCommands.CODABLOCK_F_PREFIX,
        FormattableSymbology.CHINESE_SENSIBLE_CODE to FormattingCommands.CHINESE_SENSIBLE_CODE_PREFIX,
        FormattableSymbology.MACHINE_READABLE_PASSPORTS to FormattingCommands.MACHINE_READABLE_PASSPORTS_PREFIX,
        FormattableSymbology.MACHINE_READABLE_VISA_A to FormattingCommands.MACHINE_READABLE_VISA_A_PREFIX,
        FormattableSymbology.MACHINE_READABLE_VISA_B to FormattingCommands.MACHINE_READABLE_VISA_B_PREFIX,
        FormattableSymbology.OFFICIAL_TRAVEL_DOCUMENTS_1 to FormattingCommands.OFFICIAL_TRAVEL_DOCUMENTS_1_PREFIX,
        FormattableSymbology.OFFICIAL_TRAVEL_DOCUMENTS_2 to FormattingCommands.OFFICIAL_TRAVEL_DOCUMENTS_2_PREFIX,
        FormattableSymbology.ISBN to FormattingCommands.ISBN_PREFIX,
        FormattableSymbology.JAPANESE_BOOK_PRICE to FormattingCommands.JAPANESE_BOOK_PRICE_PREFIX,
        FormattableSymbology.JAPANESE_DRIVER_LICENSE to FormattingCommands.JAPANESE_DRIVER_LICENSE_PREFIX,
        FormattableSymbology.JAPANESE_PRIVATE_NUMBER to FormattingCommands.JAPANESE_PRIVATE_NUMBER_PREFIX
    )

    private val suffixSymbologyMap = mapOf(
        FormattableSymbology.ALL_CODES to FormattingCommands.SUFFIX_ALL_CODES,
        FormattableSymbology.UPC_A to FormattingCommands.UPC_A_SUFFIX,
        FormattableSymbology.UPC_A_ADD_ON to FormattingCommands.UPC_A_ADDON_SUFFIX,
        FormattableSymbology.UPC_E to FormattingCommands.UPC_E_SUFFIX,
        FormattableSymbology.UPC_E_ADD_ON to FormattingCommands.UPC_E_ADDON_SUFFIX,
        FormattableSymbology.EAN_13 to FormattingCommands.EAN_13_SUFFIX,
        FormattableSymbology.EAN_13_ADD_ON to FormattingCommands.EAN_13_ADDON_SUFFIX,
        FormattableSymbology.EAN_8 to FormattingCommands.EAN_8_SUFFIX,
        FormattableSymbology.EAN_8_ADD_ON to FormattingCommands.EAN_8_ADDON_SUFFIX,
        FormattableSymbology.CODE_39 to FormattingCommands.CODE39_SUFFIX,
        FormattableSymbology.CODABAR to FormattingCommands.CODABAR_SUFFIX,
        FormattableSymbology.INDUSTRIAL_2OF5 to FormattingCommands.INDUSTRIAL_2OF5_SUFFIX,
        FormattableSymbology.INTERLEAVED_2OF5 to FormattingCommands.INTERLEAVED_2OF5_SUFFIX,
        FormattableSymbology.S_CODE to FormattingCommands.S_CODE_SUFFIX,
        FormattableSymbology.MATRIX_2OF5 to FormattingCommands.MATRIX_2OF5_SUFFIX,
        FormattableSymbology.IATA to FormattingCommands.IATA_SUFFIX,
        FormattableSymbology.CODE_93 to FormattingCommands.CODE93_SUFFIX,
        FormattableSymbology.CODE_128 to FormattingCommands.CODE128_SUFFIX,
        FormattableSymbology.GS1_128 to FormattingCommands.GS1_128_SUFFIX,
        FormattableSymbology.MSI_PLESSEY to FormattingCommands.MSI_PLESSEY_SUFFIX,
        FormattableSymbology.TELEPEN to FormattingCommands.TELEPEN_SUFFIX,
        FormattableSymbology.UK_PLESSEY to FormattingCommands.UK_PLESSEY_SUFFIX,
        FormattableSymbology.DATA_MATRIX to FormattingCommands.DATA_MATRIX_SUFFIX,
        FormattableSymbology.QR_CODE to FormattingCommands.QR_CODE_SUFFIX,
        FormattableSymbology.MAXICODE to FormattingCommands.MAXICODE_SUFFIX,
        FormattableSymbology.PDF_417 to FormattingCommands.PDF417_SUFFIX,
        FormattableSymbology.MICRO_PDF_417 to FormattingCommands.MICRO_PDF417_SUFFIX,
        FormattableSymbology.AZTEC to FormattingCommands.AZTEC_SUFFIX,
        FormattableSymbology.CODE_11 to FormattingCommands.CODE11_SUFFIX,
        FormattableSymbology.TRI_OPTIC to FormattingCommands.TRI_OPTIC_SUFFIX,
        FormattableSymbology.KOREAN_POSTAL_AUTHORITY to FormattingCommands.KOREAN_POSTAL_AUTHORITY_SUFFIX,
        FormattableSymbology.DOT_CODE to FormattingCommands.DOT_CODE_SUFFIX,
        FormattableSymbology.INTELLIGENT_MAIL to FormattingCommands.INTELLIGENT_MAIL_SUFFIX,
        FormattableSymbology.POSTNET to FormattingCommands.POSTNET_SUFFIX,
        FormattableSymbology.PLANET to FormattingCommands.PLANET_SUFFIX,
        FormattableSymbology.JAPANESE_POSTAL to FormattingCommands.JAPANESE_POSTAL_SUFFIX,
        FormattableSymbology.NETHERLANDS_KIX to FormattingCommands.NETHERLANDS_KIX_SUFFIX,
        FormattableSymbology.UK_POSTAL to FormattingCommands.UK_POSTAL_SUFFIX,
        FormattableSymbology.AUSTRALIAN_POSTAL to FormattingCommands.AUSTRALIAN_POSTAL_SUFFIX,
        FormattableSymbology.MAIL_MARK_4_STATE_POSTAL to FormattingCommands.MAILMARK_4_STATE_POSTAL_SUFFIX,
        FormattableSymbology.GS1_DATABAR_OMNIDIRECTIONAL to FormattingCommands.GS1_DATABAR_OMNIDIRECTIONAL_SUFFIX,
        FormattableSymbology.GS1_DATABAR_LIMITED to FormattingCommands.GS1_DATABAR_LIMITED_SUFFIX,
        FormattableSymbology.GS1_DATABAR_EXPANDED to FormattingCommands.GS1_DATABAR_EXPANDED_SUFFIX,
        FormattableSymbology.GS1_COMPOSITE_CODE to FormattingCommands.GS1_COMPOSITE_CODE_SUFFIX,
        FormattableSymbology.CODABLOCK_F to FormattingCommands.CODABLOCK_F_SUFFIX,
        FormattableSymbology.CHINESE_SENSIBLE_CODE to FormattingCommands.CHINESE_SENSIBLE_CODE_SUFFIX,
        FormattableSymbology.MACHINE_READABLE_PASSPORTS to FormattingCommands.MACHINE_READABLE_PASSPORTS_SUFFIX,
        FormattableSymbology.MACHINE_READABLE_VISA_A to FormattingCommands.MACHINE_READABLE_VISA_A_SUFFIX,
        FormattableSymbology.MACHINE_READABLE_VISA_B to FormattingCommands.MACHINE_READABLE_VISA_B_SUFFIX,
        FormattableSymbology.OFFICIAL_TRAVEL_DOCUMENTS_1 to FormattingCommands.OFFICIAL_TRAVEL_DOCUMENTS_1_SUFFIX,
        FormattableSymbology.OFFICIAL_TRAVEL_DOCUMENTS_2 to FormattingCommands.OFFICIAL_TRAVEL_DOCUMENTS_2_SUFFIX,
        FormattableSymbology.ISBN to FormattingCommands.ISBN_SUFFIX,
        FormattableSymbology.JAPANESE_BOOK_PRICE to FormattingCommands.JAPANESE_BOOK_PRICE_SUFFIX,
        FormattableSymbology.JAPANESE_DRIVER_LICENSE to FormattingCommands.JAPANESE_DRIVER_LICENSE_SUFFIX,
        FormattableSymbology.JAPANESE_PRIVATE_NUMBER to FormattingCommands.JAPANESE_PRIVATE_NUMBER_SUFFIX
    )

    companion object {
        const val MAX_PREAMBLE_CHARS = 8
        const val MAX_SUFFIX_CHARS = 4
        const val MAX_PREFIX_CHARS = 4
        const val MAX_POSTAMBLE_CHARS = 8
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // -------------------
    // Preamble Methods
    // -------------------

    override suspend fun setPreambleFromKeys(deviceId: String, keys: List<DirectInputKey>): CommandResponse {
        val codes = validateAndConvertKeysToCodes(keys, MAX_PREAMBLE_CHARS)
        Timber.d("Setting preamble for deviceId $deviceId with keys $keys")
        return sendCommand(deviceId, FormattingCommands.PREAMBLE, parameters = codes)
    }

    override fun setPreambleFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPreambleFromKeys(deviceId, keys) }
    }

    override suspend fun setPreambleFromString(deviceId: String, preamble: String): CommandResponse {
        val codes = validateAndConvertStringToCodes(preamble, MAX_PREAMBLE_CHARS)
        Timber.d("Setting preamble for deviceId $deviceId with string '$preamble'")
        return sendCommand(deviceId, FormattingCommands.PREAMBLE, parameters = codes)
    }

    override fun setPreambleFromString(
        deviceId: String,
        preamble: String,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPreambleFromString(deviceId, preamble) }
    }

    override suspend fun clearPreamble(deviceId: String): CommandResponse {
        Timber.d("Clearing preamble for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.PREAMBLE)
    }

    override fun clearPreamble(deviceId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { clearPreamble(deviceId) }
    }

    // -------------------
    // Prefix Methods
    // -------------------

    override suspend fun setPrefixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology
    ): CommandResponse {
        val codes = validateAndConvertKeysToCodes(keys, MAX_PREFIX_CHARS)
        Timber.d("Setting prefix for deviceId $deviceId with keys $keys and symbology $symbology")
        return sendCommand(deviceId, prefixSymbologyMap[symbology] ?: "", parameters = codes)
    }

    override fun setPrefixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPrefixFromKeys(deviceId, keys, symbology) }
    }

    override suspend fun setPrefixFromString(
        deviceId: String,
        prefix: String,
        symbology: FormattableSymbology
    ): CommandResponse {
        val codes = validateAndConvertStringToCodes(prefix, MAX_PREFIX_CHARS)
        Timber.d("Setting prefix for deviceId $deviceId with string '$prefix' and symbology $symbology")
        return sendCommand(deviceId, prefixSymbologyMap[symbology] ?: "", parameters = codes)
    }

    override fun setPrefixFromString(
        deviceId: String,
        prefix: String,
        symbology: FormattableSymbology,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPrefixFromString(deviceId, prefix, symbology) }
    }

    override suspend fun clearAllPrefixes(deviceId: String): CommandResponse {
        Timber.d("Clearing all prefixes for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.CLEAR_PREFIXES)
    }

    override fun clearAllPrefixes(deviceId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { clearAllPrefixes(deviceId) }
    }

    // -------------------
    // Suffix Methods
    // -------------------

    override suspend fun setSuffixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology
    ): CommandResponse {
        val codes = validateAndConvertKeysToCodes(keys, MAX_SUFFIX_CHARS)
        Timber.d("Setting suffix for deviceId $deviceId with keys $keys and symbology $symbology")
        return sendCommand(deviceId, suffixSymbologyMap[symbology] ?: "", parameters = codes)
    }

    override fun setSuffixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSuffixFromKeys(deviceId, keys, symbology) }
    }

    override suspend fun setSuffixFromString(
        deviceId: String,
        suffix: String,
        symbology: FormattableSymbology
    ): CommandResponse {
        val codes = validateAndConvertStringToCodes(suffix, MAX_SUFFIX_CHARS)
        Timber.d("Setting suffix for deviceId $deviceId with string '$suffix' and symbology $symbology")
        return sendCommand(deviceId, suffixSymbologyMap[symbology] ?: "", parameters = codes)
    }

    override fun setSuffixFromString(
        deviceId: String,
        suffix: String,
        symbology: FormattableSymbology,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSuffixFromString(deviceId, suffix, symbology) }
    }

    override suspend fun clearAllSuffixes(deviceId: String): CommandResponse {
        Timber.d("Clearing all suffixes for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.CLEAR_SUFFIXES)
    }

    override fun clearAllSuffixes(deviceId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { clearAllSuffixes(deviceId) }
    }

    // -------------------
    // Postamble Methods
    // -------------------

    override suspend fun setPostambleFromKeys(deviceId: String, keys: List<DirectInputKey>): CommandResponse {
        val codes = validateAndConvertKeysToCodes(keys, MAX_POSTAMBLE_CHARS)
        Timber.d("Setting postamble for deviceId $deviceId with keys $keys")
        return sendCommand(deviceId, FormattingCommands.POSTAMBLE, parameters = codes)
    }

    override fun setPostambleFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPostambleFromKeys(deviceId, keys) }
    }

    override suspend fun setPostambleFromString(deviceId: String, postamble: String): CommandResponse {
        val codes = validateAndConvertStringToCodes(postamble, MAX_POSTAMBLE_CHARS)
        Timber.d("Setting postamble for deviceId $deviceId with string '$postamble'")
        return sendCommand(deviceId, FormattingCommands.POSTAMBLE, parameters = codes)
    }

    override fun setPostambleFromString(
        deviceId: String,
        postamble: String,
        callback: (Result<CommandResponse>) -> Unit
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPostambleFromString(deviceId, postamble) }
    }

    override suspend fun clearPostamble(deviceId: String): CommandResponse {
        Timber.d("Clearing postamble for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.POSTAMBLE)
    }

    override fun clearPostamble(deviceId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { clearPostamble(deviceId) }
    }

    // -------------------
    // Helper Methods
    // -------------------

    /**
     * Validates and converts a list of [DirectInputKey] to their corresponding codes.
     *
     * @param keys The list of [DirectInputKey] to validate and convert.
     * @param maxChars The maximum number of characters allowed.
     * @return A list of [String] codes.
     */
    private fun validateAndConvertKeysToCodes(keys: List<DirectInputKey>, maxChars: Int): List<String> {
        var truncatedKeys = keys
        if (keys.size > maxChars) {
            Timber.w("Input exceeds $maxChars characters. Truncating input.")
            truncatedKeys = keys.subList(0, maxChars)
        }
        return directInputKeysHelper.convertKeysToCodes(truncatedKeys)
    }

    /**
     * Validates and converts a string to its corresponding codes.
     *
     * @param input The string to validate and convert.
     * @param maxChars The maximum number of characters allowed.
     * @return A list of [String] codes.
     */
    private fun validateAndConvertStringToCodes(input: String, maxChars: Int): List<String> {
        var truncatedInput = input
        if (input.length > maxChars) {
            Timber.w("Input exceeds $maxChars characters. Truncating input.")
            truncatedInput = input.substring(0, maxChars)
        }
        return directInputKeysHelper.convertStringToCodes(truncatedInput)
    }
}