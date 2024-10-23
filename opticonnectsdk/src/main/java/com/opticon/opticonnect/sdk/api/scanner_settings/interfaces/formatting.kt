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
     * Sets the preamble formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param preamble A string to be used as the preamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPreambleFromString(deviceId: String, preamble: String): CommandResponse

    /**
     * Clears the preamble formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearPreamble(deviceId: String): CommandResponse

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
     * Sets the prefix formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param prefix A string to be used as the prefix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPrefixFromString(deviceId: String, prefix: String, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Clears the prefix formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearAllPrefixes(deviceId: String): CommandResponse

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
     * Sets the suffix formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param suffix A string to be used as the suffix.
     * @param symbology The symbology type to be used.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setSuffixFromString(deviceId: String, suffix: String, symbology: FormattableSymbology = FormattableSymbology.ALL_CODES): CommandResponse

    /**
     * Clears the suffix formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearAllSuffixes(deviceId: String): CommandResponse

    /**
     * Sets the postamble formatting using a list of [DirectInputKey].
     *
     * @param deviceId The identifier of the target device.
     * @param keys A list of [DirectInputKey] values to be used for the postamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPostambleFromKeys(deviceId: String, keys: List<DirectInputKey>): CommandResponse

    /**
     * Sets the postamble formatting using a string.
     *
     * @param deviceId The identifier of the target device.
     * @param postamble A string to be used as the postamble.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPostambleFromString(deviceId: String, postamble: String): CommandResponse

    /**
     * Clears the postamble formatting on the device.
     *
     * @param deviceId The identifier of the target device.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun clearPostamble(deviceId: String): CommandResponse
}
