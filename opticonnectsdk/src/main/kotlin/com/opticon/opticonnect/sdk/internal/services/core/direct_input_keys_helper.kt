package com.opticon.opticonnect.sdk.internal.services.core

import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.interfaces.DirectInputKeysHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DirectInputKeysHelperImpl @Inject constructor() : DirectInputKeysHelper {
    // Mapping from DirectInputKey to String code (e.g., 'Q0')
    private val directInputKeyToStringMap = mapOf(
        // Numeric digits
        DirectInputKey.DIGIT_0 to "Q0",
        DirectInputKey.DIGIT_1 to "Q1",
        DirectInputKey.DIGIT_2 to "Q2",
        DirectInputKey.DIGIT_3 to "Q3",
        DirectInputKey.DIGIT_4 to "Q4",
        DirectInputKey.DIGIT_5 to "Q5",
        DirectInputKey.DIGIT_6 to "Q6",
        DirectInputKey.DIGIT_7 to "Q7",
        DirectInputKey.DIGIT_8 to "Q8",
        DirectInputKey.DIGIT_9 to "Q9",

        // Uppercase Letters
        DirectInputKey.LETTER_A to "0A",
        DirectInputKey.LETTER_B to "0B",
        DirectInputKey.LETTER_C to "0C",
        DirectInputKey.LETTER_D to "0D",
        DirectInputKey.LETTER_E to "0E",
        DirectInputKey.LETTER_F to "0F",
        DirectInputKey.LETTER_G to "0G",
        DirectInputKey.LETTER_H to "0H",
        DirectInputKey.LETTER_I to "0I",
        DirectInputKey.LETTER_J to "0J",
        DirectInputKey.LETTER_K to "0K",
        DirectInputKey.LETTER_L to "0L",
        DirectInputKey.LETTER_M to "0M",
        DirectInputKey.LETTER_N to "0N",
        DirectInputKey.LETTER_O to "0O",
        DirectInputKey.LETTER_P to "0P",
        DirectInputKey.LETTER_Q to "0Q",
        DirectInputKey.LETTER_R to "0R",
        DirectInputKey.LETTER_S to "0S",
        DirectInputKey.LETTER_T to "0T",
        DirectInputKey.LETTER_U to "0U",
        DirectInputKey.LETTER_V to "0V",
        DirectInputKey.LETTER_W to "0W",
        DirectInputKey.LETTER_X to "0X",
        DirectInputKey.LETTER_Y to "0Y",
        DirectInputKey.LETTER_Z to "0Z",

        // Lowercase Letters
        DirectInputKey.LETTER_A_LOWER to "\$A",
        DirectInputKey.LETTER_B_LOWER to "\$B",
        DirectInputKey.LETTER_C_LOWER to "\$C",
        DirectInputKey.LETTER_D_LOWER to "\$D",
        DirectInputKey.LETTER_E_LOWER to "\$E",
        DirectInputKey.LETTER_F_LOWER to "\$F",
        DirectInputKey.LETTER_G_LOWER to "\$G",
        DirectInputKey.LETTER_H_LOWER to "\$H",
        DirectInputKey.LETTER_I_LOWER to "\$I",
        DirectInputKey.LETTER_J_LOWER to "\$J",
        DirectInputKey.LETTER_K_LOWER to "\$K",
        DirectInputKey.LETTER_L_LOWER to "\$L",
        DirectInputKey.LETTER_M_LOWER to "\$M",
        DirectInputKey.LETTER_N_LOWER to "\$N",
        DirectInputKey.LETTER_O_LOWER to "\$O",
        DirectInputKey.LETTER_P_LOWER to "\$P",
        DirectInputKey.LETTER_Q_LOWER to "\$Q",
        DirectInputKey.LETTER_R_LOWER to "\$R",
        DirectInputKey.LETTER_S_LOWER to "\$S",
        DirectInputKey.LETTER_T_LOWER to "\$T",
        DirectInputKey.LETTER_U_LOWER to "\$U",
        DirectInputKey.LETTER_V_LOWER to "\$V",
        DirectInputKey.LETTER_W_LOWER to "\$W",
        DirectInputKey.LETTER_X_LOWER to "\$X",
        DirectInputKey.LETTER_Y_LOWER to "\$Y",
        DirectInputKey.LETTER_Z_LOWER to "\$Z",

        // Function Keys
        DirectInputKey.FUNCTION_F1 to "8J",
        DirectInputKey.FUNCTION_F2 to "8K",
        DirectInputKey.FUNCTION_F3 to "8L",
        DirectInputKey.FUNCTION_F4 to "8M",
        DirectInputKey.FUNCTION_F5 to "8N",
        DirectInputKey.FUNCTION_F6 to "8O",
        DirectInputKey.FUNCTION_F7 to "8P",
        DirectInputKey.FUNCTION_F8 to "8Q",
        DirectInputKey.FUNCTION_F9 to "8R",
        DirectInputKey.FUNCTION_F10 to "8S",
        DirectInputKey.FUNCTION_F11 to "8T",
        DirectInputKey.FUNCTION_F12 to "8U",

        // Keyboard Keys
        DirectInputKey.BACKSPACE to "9X",
        DirectInputKey.TAB to "7H",
        DirectInputKey.RETURN_KEY to "7I",
        DirectInputKey.ENTER_NUMERIC_KEYPAD to "7Q",
        DirectInputKey.ESCAPE_KEY to "7J",
        DirectInputKey.ARROW_DOWN to "7K",
        DirectInputKey.ARROW_UP to "7L",
        DirectInputKey.ARROW_RIGHT to "7M",
        DirectInputKey.ARROW_LEFT to "7N",
        DirectInputKey.DELETE to "7T",
        DirectInputKey.INSERT to "VQ",
        DirectInputKey.HOME to "VR",
        DirectInputKey.END to "VS",
        DirectInputKey.PAGE_UP to "7O",
        DirectInputKey.PAGE_DOWN to "7P",
        DirectInputKey.LEFT_SHIFT to "7U",
        DirectInputKey.RIGHT_SHIFT to "7V",
        DirectInputKey.LEFT_CTRL to "7W",
        DirectInputKey.RIGHT_CTRL to "7X",
        DirectInputKey.LEFT_ALT to "7Y",
        DirectInputKey.RIGHT_ALT to "7Z",
        DirectInputKey.LEFT_GUI to "\$8",
        DirectInputKey.RIGHT_GUI to "\$9",
        DirectInputKey.CAPS_LOCK to "9S",

        // Numeric Keypad
        DirectInputKey.NUM_MINUS to "\$A9",
        DirectInputKey.NUM_DIVIDE to "\$D4",
        DirectInputKey.NUM_MULTIPLY to "\$D5",
        DirectInputKey.NUM_PLUS to "\$D7",
        DirectInputKey.NUM_DOT to "\$E3",

        // Special Characters
        DirectInputKey.SPACE to "5A",
        DirectInputKey.EXCLAMATION to "5B",
        DirectInputKey.DOUBLE_QUOTE to "5C",
        DirectInputKey.HASH to "5D",
        DirectInputKey.DOLLAR to "5E",
        DirectInputKey.PERCENT to "5F",
        DirectInputKey.AMPERSAND to "5G",
        DirectInputKey.SINGLE_QUOTE to "5H",
        DirectInputKey.OPEN_PAREN to "5I",
        DirectInputKey.CLOSE_PAREN to "5J",
        DirectInputKey.ASTERISK to "5K",
        DirectInputKey.PLUS to "5L",
        DirectInputKey.COMMA to "5M",
        DirectInputKey.MINUS to "5N",
        DirectInputKey.PERIOD to "5O",
        DirectInputKey.SLASH to "5P",
        DirectInputKey.COLON to "6A",
        DirectInputKey.SEMICOLON to "6B",
        DirectInputKey.LESS_THAN to "6C",
        DirectInputKey.EQUAL to "6D",
        DirectInputKey.GREATER_THAN to "6E",
        DirectInputKey.QUESTION_MARK to "6F",
        DirectInputKey.AT_SYMBOL to "6G",
        DirectInputKey.OPEN_BRACKET to "7A",
        DirectInputKey.BACKSLASH to "7B",
        DirectInputKey.CLOSE_BRACKET to "7C",
        DirectInputKey.CARET to "7D",
        DirectInputKey.UNDERSCORE to "7E",
        DirectInputKey.BACKTICK to "7F",
        DirectInputKey.OPEN_BRACE to "9T",
        DirectInputKey.PIPE to "9U",
        DirectInputKey.CLOSE_BRACE to "9V",
        DirectInputKey.TILDE to "9W",

        // Control Characters
        DirectInputKey.NULL_CHAR to "9G",
        DirectInputKey.SOH to "1A",
        DirectInputKey.STX to "1B",
        DirectInputKey.ETX to "1C",
        DirectInputKey.EOT to "1D",
        DirectInputKey.ENQ to "1E",
        DirectInputKey.ACK to "1F",
        DirectInputKey.BEL to "1G",
        DirectInputKey.BS to "1H",
        DirectInputKey.HT to "1I",
        DirectInputKey.LF to "1J",
        DirectInputKey.VT to "1K",
        DirectInputKey.FF to "1L",
        DirectInputKey.CR to "1M",
        DirectInputKey.SO to "1N",
        DirectInputKey.SI to "1O",
        DirectInputKey.DLE to "1P",
        DirectInputKey.DC1 to "1Q",
        DirectInputKey.DC2 to "1R",
        DirectInputKey.DC3 to "1S",
        DirectInputKey.DC4 to "1T",
        DirectInputKey.NAK to "1U",
        DirectInputKey.SYN to "1V",
        DirectInputKey.ETB to "1W",
        DirectInputKey.CAN to "1X",
        DirectInputKey.EM to "1Y",
        DirectInputKey.SUB to "1Z",
        DirectInputKey.ESCAPE_CHAR to "9A",
        DirectInputKey.FS to "9B",
        DirectInputKey.GS to "9C",
        DirectInputKey.RS to "9D",
        DirectInputKey.US to "9E",
        DirectInputKey.DEL_ASCII to "9F",

        // Code id/length
        DirectInputKey.CODE_IDENTIFICATION to "\$2",
        DirectInputKey.CODE_IDENTIFICATION_ISO to "\$1",
        DirectInputKey.CODE_IDENTIFICATION_BT to "\$BT",
        DirectInputKey.CODE_LENGTH_2_DIGITS to "\$3",
        DirectInputKey.CODE_LENGTH_6_DIGITS to "\$6",

        // Special
        DirectInputKey.READ_DIRECTION to "\$4",
        DirectInputKey.TIMESTAMP to "\$TM"
    )

    // Reverse mapping from string to DirectInputKey (e.g., 'Q0' -> DirectInputKey.DIGIT_0)
    private val stringToDirectInputKeyMap = directInputKeyToStringMap.entries.associate { (key, value) -> value to key }

    // Mapping of characters to DirectInputKey
    private val charToDirectInputKeyMap = mapOf(
        // Numeric digits
        '0' to DirectInputKey.DIGIT_0,
        '1' to DirectInputKey.DIGIT_1,
        '2' to DirectInputKey.DIGIT_2,
        '3' to DirectInputKey.DIGIT_3,
        '4' to DirectInputKey.DIGIT_4,
        '5' to DirectInputKey.DIGIT_5,
        '6' to DirectInputKey.DIGIT_6,
        '7' to DirectInputKey.DIGIT_7,
        '8' to DirectInputKey.DIGIT_8,
        '9' to DirectInputKey.DIGIT_9,

        // Uppercase Letters
        'A' to DirectInputKey.LETTER_A,
        'B' to DirectInputKey.LETTER_B,
        'C' to DirectInputKey.LETTER_C,
        'D' to DirectInputKey.LETTER_D,
        'E' to DirectInputKey.LETTER_E,
        'F' to DirectInputKey.LETTER_F,
        'G' to DirectInputKey.LETTER_G,
        'H' to DirectInputKey.LETTER_H,
        'I' to DirectInputKey.LETTER_I,
        'J' to DirectInputKey.LETTER_J,
        'K' to DirectInputKey.LETTER_K,
        'L' to DirectInputKey.LETTER_L,
        'M' to DirectInputKey.LETTER_M,
        'N' to DirectInputKey.LETTER_N,
        'O' to DirectInputKey.LETTER_O,
        'P' to DirectInputKey.LETTER_P,
        'Q' to DirectInputKey.LETTER_Q,
        'R' to DirectInputKey.LETTER_R,
        'S' to DirectInputKey.LETTER_S,
        'T' to DirectInputKey.LETTER_T,
        'U' to DirectInputKey.LETTER_U,
        'V' to DirectInputKey.LETTER_V,
        'W' to DirectInputKey.LETTER_W,
        'X' to DirectInputKey.LETTER_X,
        'Y' to DirectInputKey.LETTER_Y,
        'Z' to DirectInputKey.LETTER_Z,

        // Lowercase Letters
        'a' to DirectInputKey.LETTER_A_LOWER,
        'b' to DirectInputKey.LETTER_B_LOWER,
        'c' to DirectInputKey.LETTER_C_LOWER,
        'd' to DirectInputKey.LETTER_D_LOWER,
        'e' to DirectInputKey.LETTER_E_LOWER,
        'f' to DirectInputKey.LETTER_F_LOWER,
        'g' to DirectInputKey.LETTER_G_LOWER,
        'h' to DirectInputKey.LETTER_H_LOWER,
        'i' to DirectInputKey.LETTER_I_LOWER,
        'j' to DirectInputKey.LETTER_J_LOWER,
        'k' to DirectInputKey.LETTER_K_LOWER,
        'l' to DirectInputKey.LETTER_L_LOWER,
        'm' to DirectInputKey.LETTER_M_LOWER,
        'n' to DirectInputKey.LETTER_N_LOWER,
        'o' to DirectInputKey.LETTER_O_LOWER,
        'p' to DirectInputKey.LETTER_P_LOWER,
        'q' to DirectInputKey.LETTER_Q_LOWER,
        'r' to DirectInputKey.LETTER_R_LOWER,
        's' to DirectInputKey.LETTER_S_LOWER,
        't' to DirectInputKey.LETTER_T_LOWER,
        'u' to DirectInputKey.LETTER_U_LOWER,
        'v' to DirectInputKey.LETTER_V_LOWER,
        'w' to DirectInputKey.LETTER_W_LOWER,
        'x' to DirectInputKey.LETTER_X_LOWER,
        'y' to DirectInputKey.LETTER_Y_LOWER,
        'z' to DirectInputKey.LETTER_Z_LOWER,

        // Special Characters
        ' ' to DirectInputKey.SPACE,
        '!' to DirectInputKey.EXCLAMATION,
        '"' to DirectInputKey.DOUBLE_QUOTE,
        '#' to DirectInputKey.HASH,
        '$' to DirectInputKey.DOLLAR,
        '%' to DirectInputKey.PERCENT,
        '&' to DirectInputKey.AMPERSAND,
        '\'' to DirectInputKey.SINGLE_QUOTE,
        '(' to DirectInputKey.OPEN_PAREN,
        ')' to DirectInputKey.CLOSE_PAREN,
        '*' to DirectInputKey.ASTERISK,
        '+' to DirectInputKey.PLUS,
        ',' to DirectInputKey.COMMA,
        '-' to DirectInputKey.MINUS,
        '.' to DirectInputKey.PERIOD,
        '/' to DirectInputKey.SLASH,
        ':' to DirectInputKey.COLON,
        ';' to DirectInputKey.SEMICOLON,
        '<' to DirectInputKey.LESS_THAN,
        '=' to DirectInputKey.EQUAL,
        '>' to DirectInputKey.GREATER_THAN,
        '?' to DirectInputKey.QUESTION_MARK,
        '@' to DirectInputKey.AT_SYMBOL,
        '[' to DirectInputKey.OPEN_BRACKET,
        '\\' to DirectInputKey.BACKSLASH,
        ']' to DirectInputKey.CLOSE_BRACKET,
        '^' to DirectInputKey.CARET,
        '_' to DirectInputKey.UNDERSCORE,
        '`' to DirectInputKey.BACKTICK,
        '{' to DirectInputKey.OPEN_BRACE,
        '|' to DirectInputKey.PIPE,
        '}' to DirectInputKey.CLOSE_BRACE,
        '~' to DirectInputKey.TILDE,

        // Control Characters
        '\n' to DirectInputKey.LF,
        '\r' to DirectInputKey.CR,
        '\b' to DirectInputKey.BS
    )

    override fun stringToDirectInputKey(input: String): DirectInputKey? {
        return stringToDirectInputKeyMap[input]
    }

    override fun directInputKeyToString(key: DirectInputKey): String? {
        return directInputKeyToStringMap[key]
    }

    override fun convertIntToDirectInputKeys(value: Int): List<String> {
        return value.toString().mapNotNull { char ->
            stringToDirectInputKey(char.toString())?.let { directInputKey ->
                directInputKeyToString(directInputKey)
            }
        }
    }

    // Helper method to convert a list of DirectInputKeys to their corresponding string codes.
    override fun convertKeysToCodes(keys: List<DirectInputKey>): List<String> {
        return keys.mapNotNull { key -> directInputKeyToString(key) }
    }

    // Helper method to convert a string to the corresponding DirectInputKeys.
    override fun convertStringToCodes(input: String): List<String> {
        return input.mapNotNull { char ->
            charToDirectInputKeyMap[char]?.let { directInputKey ->
                directInputKeyToString(directInputKey)
            }
        }
    }
}