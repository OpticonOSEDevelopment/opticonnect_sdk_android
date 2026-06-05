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
     * Converts the RGB color values into scanner direct input key parameters.
     *
     * For example, green (00FF00) becomes Q0, Q0, $F, $F, Q0, Q0.
     */
    fun toParameters(): List<String> {
        return listOf(red, green, blue)
            .joinToString(separator = "") { component ->
                component.toString(16).padStart(2, '0')
            }
            .mapNotNull { char -> char.toDirectInputKeyOrNull() }
    }

    private fun Char.toDirectInputKeyOrNull(): String? {
        return when (this) {
            in '0'..'9' -> "Q$this"
            in 'a'..'z' -> "\$${uppercaseChar()}"
            in 'A'..'Z' -> "0$this"
            else -> null
        }
    }
}
