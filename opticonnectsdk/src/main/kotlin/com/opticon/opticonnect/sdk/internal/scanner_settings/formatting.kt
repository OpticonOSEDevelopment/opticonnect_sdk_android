package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.constants.commands.FormattingCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.enums.FormattableSymbology
import com.opticon.opticonnect.sdk.internal.interfaces.DirectInputKeysHelper
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Formatting
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.FormattingSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FormattingImpl @Inject constructor(
    private val directInputKeysHelper: DirectInputKeysHelper,
    private val scannerSettingsStateStore: ScannerSettingsStateStore
) : Formatting, SettingsBase() {

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
        callback: Callback<CommandResponse>
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
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPreambleFromString(deviceId, preamble) }
    }

    override fun getPreamble(deviceId: String): List<DirectInputKey> {
        return formattingKeysFor(deviceId, FormattingCommands.PREAMBLE)
    }

    override suspend fun clearPreamble(deviceId: String): CommandResponse {
        Timber.d("Clearing preamble for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.PREAMBLE)
    }

    override fun clearPreamble(deviceId: String, callback: Callback<CommandResponse>) {
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
        return sendFormattingCommand(
            deviceId,
            FormattingSettingDescriptors.prefix.commandFor(symbology),
            codes,
            "Unsupported prefix symbology: $symbology"
        )
    }

    override fun setPrefixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology,
        callback: Callback<CommandResponse>
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
        return sendFormattingCommand(
            deviceId,
            FormattingSettingDescriptors.prefix.commandFor(symbology),
            codes,
            "Unsupported prefix symbology: $symbology"
        )
    }

    override fun setPrefixFromString(
        deviceId: String,
        prefix: String,
        symbology: FormattableSymbology,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPrefixFromString(deviceId, prefix, symbology) }
    }

    override fun getPrefix(deviceId: String, symbology: FormattableSymbology): List<DirectInputKey> {
        return formattingKeysFor(
            deviceId,
            FormattingSettingDescriptors.prefix.commandFor(symbology)
        )
    }

    override suspend fun clearAllPrefixes(deviceId: String): CommandResponse {
        Timber.d("Clearing all prefixes for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.CLEAR_PREFIXES)
    }

    override fun clearAllPrefixes(deviceId: String, callback: Callback<CommandResponse>) {
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
        return sendFormattingCommand(
            deviceId,
            FormattingSettingDescriptors.suffix.commandFor(symbology),
            codes,
            "Unsupported suffix symbology: $symbology"
        )
    }

    override fun setSuffixFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        symbology: FormattableSymbology,
        callback: Callback<CommandResponse>
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
        return sendFormattingCommand(
            deviceId,
            FormattingSettingDescriptors.suffix.commandFor(symbology),
            codes,
            "Unsupported suffix symbology: $symbology"
        )
    }

    override fun setSuffixFromString(
        deviceId: String,
        suffix: String,
        symbology: FormattableSymbology,
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setSuffixFromString(deviceId, suffix, symbology) }
    }

    override fun getSuffix(deviceId: String, symbology: FormattableSymbology): List<DirectInputKey> {
        return formattingKeysFor(
            deviceId,
            FormattingSettingDescriptors.suffix.commandFor(symbology)
        )
    }

    override suspend fun clearAllSuffixes(deviceId: String): CommandResponse {
        Timber.d("Clearing all suffixes for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.CLEAR_SUFFIXES)
    }

    override fun clearAllSuffixes(deviceId: String, callback: Callback<CommandResponse>) {
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
        callback: Callback<CommandResponse>
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
        callback: Callback<CommandResponse>
    ) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setPostambleFromString(deviceId, postamble) }
    }

    override fun getPostamble(deviceId: String): List<DirectInputKey> {
        return formattingKeysFor(deviceId, FormattingCommands.POSTAMBLE)
    }

    override suspend fun clearPostamble(deviceId: String): CommandResponse {
        Timber.d("Clearing postamble for deviceId $deviceId")
        return sendCommand(deviceId, FormattingCommands.POSTAMBLE)
    }

    override fun clearPostamble(deviceId: String, callback: Callback<CommandResponse>) {
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

    private suspend fun sendFormattingCommand(
        deviceId: String,
        command: String?,
        parameters: List<String>,
        invalidMessage: String
    ): CommandResponse {
        return command?.let { sendCommand(deviceId, it, parameters = parameters) }
            ?: CommandResponse.failed(invalidMessage)
    }

    private fun formattingKeysFor(deviceId: String, command: String?): List<DirectInputKey> {
        require(command != null) { "Unsupported formatting symbology." }

        return scannerSettingsStateStore.settingsFor(deviceId)[normalizeCommand(command)]
            ?.mapNotNull { code -> directInputKeysHelper.stringToDirectInputKey(code) }
            ?: emptyList()
    }

    private fun normalizeCommand(command: String): String {
        return if (command.startsWith("[") || command.startsWith("]")) {
            command.substring(1)
        } else {
            command
        }
    }
}
