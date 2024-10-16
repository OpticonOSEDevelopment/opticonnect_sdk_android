package com.opticon.opticonnect.sdk.api.entities

/**
 * A class representing the RGB color values for the LED.
 */
data class LEDColor(
    /**
     * Red component of the RGB color, ranging from 0 to 255.
     */
    val red: Int,

    /**
     * Green component of the RGB color, ranging from 0 to 255.
     */
    val green: Int,

    /**
     * Blue component of the RGB color, ranging from 0 to 255.
     */
    val blue: Int
) {

    init {
        require(red in 0..255) { "The red value must be between 0 and 255." }
        require(green in 0..255) { "The green value must be between 0 and 255." }
        require(blue in 0..255) { "The blue value must be between 0 and 255." }
    }

    /**
     * Converts an integer value to a hexadecimal string.
     *
     * @param value The integer value to convert, which should represent one of the RGB color components.
     * @return A string in hexadecimal format, with a leading `$` and two digits.
     */
    private fun toHex(value: Int): String {
        return "$" + value.toString(16).padStart(2, '0').uppercase()
    }

    /**
     * Converts the RGB color values into a list of strings, each representing one of the color components in hexadecimal format.
     *
     * @return A list of hexadecimal strings representing the red, green, and blue components of the color.
     */
    fun toParameters(): List<String> {
        return listOf(toHex(red), toHex(green), toHex(blue))
    }
}