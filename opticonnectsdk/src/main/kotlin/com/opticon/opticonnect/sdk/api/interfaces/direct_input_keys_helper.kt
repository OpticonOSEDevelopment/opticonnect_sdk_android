package com.opticon.opticonnect.sdk.api.interfaces

import com.opticon.opticonnect.sdk.api.enums.DirectInputKey

/**
 * Interface defining methods for converting between strings and direct input keys.
 *
 * This includes converting strings like 'Q0' to their corresponding [DirectInputKey],
 * converting from [DirectInputKey] to their respective string codes, and handling
 * conversions of lists of input keys.
 */
interface DirectInputKeysHelper {

    /**
     * Converts a given string (e.g., 'Q0') to its corresponding [DirectInputKey].
     *
     * @param input The string representation of the input key.
     * @return The corresponding [DirectInputKey], or null if no match is found.
     */
    fun stringToDirectInputKey(input: String): DirectInputKey?

    /**
     * Converts a [DirectInputKey] to its corresponding string representation (e.g., 'Q0').
     *
     * @param key The [DirectInputKey] to be converted.
     * @return The corresponding string code, or null if no match is found.
     */
    fun directInputKeyToString(key: DirectInputKey): String?

    /**
     * Converts an integer to a list of corresponding string representations of direct input keys.
     *
     * This method is useful for generating direct input key codes from numeric values.
     *
     * @param value The integer value to convert.
     * @return The list of corresponding string codes.
     */
    fun convertIntToDirectInputKeys(value: Int): List<String>

    /**
     * Converts a list of [DirectInputKey] to their corresponding string codes.
     *
     * @param keys The list of [DirectInputKey] to be converted.
     * @return The list of corresponding string codes.
     */
    fun convertKeysToCodes(keys: List<DirectInputKey>): List<String>

    /**
     * Converts a string to a list of corresponding string representations of direct input keys.
     *
     * This method is useful for generating a list of string codes from a string input.
     *
     * @param input The string to be converted.
     * @return The list of corresponding string codes.
     */
    fun convertStringToCodes(input: String): List<String>
}
