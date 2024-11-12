package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.enums.FormattableSymbology

/**
 * Interface defining methods for formatting options such as preambles, prefixes, suffixes, and postambles.
 * These methods allow setting, clearing, and configuring formatting using direct input keys or strings.
 */
interface Formatting {

    /**
     * Sets the preamble formatting using a list of [DirectInputKey].
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the preamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPreambleFromKeys(deviceId: String, keys: List<DirectInputKey>): CommandResponse

    /**
     * Callback-based version of [setPreambleFromKeys] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the preamble.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPreambleFromKeys(deviceId: String, keys: List<DirectInputKey>, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the preamble formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param preamble A string to be used as the preamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPreambleFromString(deviceId: String, preamble: String): CommandResponse

    /**
     * Callback-based version of [setPreambleFromString] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param preamble A string to be used as the preamble.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPreambleFromString(deviceId: String, preamble: String, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Clears the preamble formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearPreamble(deviceId: String): CommandResponse

    /**
     * Callback-based version of [clearPreamble] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun clearPreamble(deviceId: String, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the prefix formatting using a list of [DirectInputKey].
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the prefix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPrefixFromKeys(deviceId: String, keys: List<DirectInputKey>, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Callback-based version of [setPrefixFromKeys] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the prefix.
     * @param symbology The symbology type to be used.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPrefixFromKeys(deviceId: String, keys: List<DirectInputKey>, symbology: FormattableSymbology, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the prefix formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param prefix A string to be used as the prefix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPrefixFromString(deviceId: String, prefix: String, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Callback-based version of [setPrefixFromString] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param prefix A string to be used as the prefix.
     * @param symbology The symbology type to be used.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPrefixFromString(deviceId: String, prefix: String, symbology: FormattableSymbology, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Clears the prefix formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearAllPrefixes(deviceId: String): CommandResponse

    /**
     * Callback-based version of [clearAllPrefixes] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun clearAllPrefixes(deviceId: String, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the suffix formatting using a list of [DirectInputKey].
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the suffix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSuffixFromKeys(deviceId: String, keys: List<DirectInputKey>, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Callback-based version of [setSuffixFromKeys] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the suffix.
     * @param symbology The symbology type to be used.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setSuffixFromKeys(deviceId: String, keys: List<DirectInputKey>, symbology: FormattableSymbology, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the suffix formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param suffix A string to be used as the suffix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSuffixFromString(deviceId: String, suffix: String, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Callback-based version of [setSuffixFromString] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param suffix A string to be used as the suffix.
     * @param symbology The symbology type to be used.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setSuffixFromString(deviceId: String, suffix: String, symbology: FormattableSymbology, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Clears the suffix formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearAllSuffixes(deviceId: String): CommandResponse

    /**
     * Callback-based version of [clearAllSuffixes] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun clearAllSuffixes(deviceId: String, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the postamble formatting using a list of [DirectInputKey].
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the postamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPostambleFromKeys(deviceId: String, keys: List<DirectInputKey>): CommandResponse

    /**
     * Callback-based version of [setPostambleFromKeys] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the postamble.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPostambleFromKeys(
        deviceId: String,
        keys: List<DirectInputKey>,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Sets the postamble formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param postamble A string to be used as the postamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPostambleFromString(deviceId: String, postamble: String): CommandResponse

    /**
     * Callback-based version of [setPostambleFromString] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param postamble A string to be used as the postamble.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPostambleFromString(
        deviceId: String,
        postamble: String,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Clears the postamble formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearPostamble(deviceId: String): CommandResponse

    /**
     * Callback-based version of [clearPostamble] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun clearPostamble(deviceId: String, callback: (Result<CommandResponse>) -> Unit)
}
