package com.opticon.opticonnect.sdk.api.enums

/**
 * Enum representing various direct input keys used for keyboard and barcode input.
 *
 * This enum includes numeric, alphabetical (both upper and lower case), function keys,
 * keyboard control keys, special characters, control characters, and other input codes.
 */
enum class DirectInputKey {
    // Numeric digits
    /** The numeric digit 0. */
    DIGIT_0,

    /** The numeric digit 1. */
    DIGIT_1,

    /** The numeric digit 2. */
    DIGIT_2,

    /** The numeric digit 3. */
    DIGIT_3,

    /** The numeric digit 4. */
    DIGIT_4,

    /** The numeric digit 5. */
    DIGIT_5,

    /** The numeric digit 6. */
    DIGIT_6,

    /** The numeric digit 7. */
    DIGIT_7,

    /** The numeric digit 8. */
    DIGIT_8,

    /** The numeric digit 9. */
    DIGIT_9,

    // Uppercase Letters
    /** The uppercase letter A. */
    LETTER_A,

    /** The uppercase letter B. */
    LETTER_B,

    /** The uppercase letter C. */
    LETTER_C,

    /** The uppercase letter D. */
    LETTER_D,

    /** The uppercase letter E. */
    LETTER_E,

    /** The uppercase letter F. */
    LETTER_F,

    /** The uppercase letter G. */
    LETTER_G,

    /** The uppercase letter H. */
    LETTER_H,

    /** The uppercase letter I. */
    LETTER_I,

    /** The uppercase letter J. */
    LETTER_J,

    /** The uppercase letter K. */
    LETTER_K,

    /** The uppercase letter L. */
    LETTER_L,

    /** The uppercase letter M. */
    LETTER_M,

    /** The uppercase letter N. */
    LETTER_N,

    /** The uppercase letter O. */
    LETTER_O,

    /** The uppercase letter P. */
    LETTER_P,

    /** The uppercase letter Q. */
    LETTER_Q,

    /** The uppercase letter R. */
    LETTER_R,

    /** The uppercase letter S. */
    LETTER_S,

    /** The uppercase letter T. */
    LETTER_T,

    /** The uppercase letter U. */
    LETTER_U,

    /** The uppercase letter V. */
    LETTER_V,

    /** The uppercase letter W. */
    LETTER_W,

    /** The uppercase letter X. */
    LETTER_X,

    /** The uppercase letter Y. */
    LETTER_Y,

    /** The uppercase letter Z. */
    LETTER_Z,

    // Lowercase Letters
    /** The lowercase letter a. */
    LETTER_A_LOWER,

    /** The lowercase letter b. */
    LETTER_B_LOWER,

    /** The lowercase letter c. */
    LETTER_C_LOWER,

    /** The lowercase letter d. */
    LETTER_D_LOWER,

    /** The lowercase letter e. */
    LETTER_E_LOWER,

    /** The lowercase letter f. */
    LETTER_F_LOWER,

    /** The lowercase letter g. */
    LETTER_G_LOWER,

    /** The lowercase letter h. */
    LETTER_H_LOWER,

    /** The lowercase letter i. */
    LETTER_I_LOWER,

    /** The lowercase letter j. */
    LETTER_J_LOWER,

    /** The lowercase letter k. */
    LETTER_K_LOWER,

    /** The lowercase letter l. */
    LETTER_L_LOWER,

    /** The lowercase letter m. */
    LETTER_M_LOWER,

    /** The lowercase letter n. */
    LETTER_N_LOWER,

    /** The lowercase letter o. */
    LETTER_O_LOWER,

    /** The lowercase letter p. */
    LETTER_P_LOWER,

    /** The lowercase letter q. */
    LETTER_Q_LOWER,

    /** The lowercase letter r. */
    LETTER_R_LOWER,

    /** The lowercase letter s. */
    LETTER_S_LOWER,

    /** The lowercase letter t. */
    LETTER_T_LOWER,

    /** The lowercase letter u. */
    LETTER_U_LOWER,

    /** The lowercase letter v. */
    LETTER_V_LOWER,

    /** The lowercase letter w. */
    LETTER_W_LOWER,

    /** The lowercase letter x. */
    LETTER_X_LOWER,

    /** The lowercase letter y. */
    LETTER_Y_LOWER,

    /** The lowercase letter z. */
    LETTER_Z_LOWER,

    // Function Keys
    /** Function key F1. */
    FUNCTION_F1,

    /** Function key F2. */
    FUNCTION_F2,

    /** Function key F3. */
    FUNCTION_F3,

    /** Function key F4. */
    FUNCTION_F4,

    /** Function key F5. */
    FUNCTION_F5,

    /** Function key F6. */
    FUNCTION_F6,

    /** Function key F7. */
    FUNCTION_F7,

    /** Function key F8. */
    FUNCTION_F8,

    /** Function key F9. */
    FUNCTION_F9,

    /** Function key F10. */
    FUNCTION_F10,

    /** Function key F11. */
    FUNCTION_F11,

    /** Function key F12. */

    FUNCTION_F12,

    // Keyboard Keys
    /** The Backspace key. */
    BACKSPACE,

    /** The Tab key. */
    TAB,

    /** The Return key. */
    RETURN_KEY,

    /** The Enter key on the numeric keypad. */
    ENTER_NUMERIC_KEYPAD,

    /** The Escape key. */
    ESCAPE_KEY,

    /** The Down Arrow key. */
    ARROW_DOWN,

    /** The Up Arrow key. */
    ARROW_UP,

    /** The Right Arrow key. */
    ARROW_RIGHT,

    /** The Left Arrow key. */
    ARROW_LEFT,

    /** The Delete key. */
    DELETE,

    /** The Insert key. */
    INSERT,

    /** The Home key. */
    HOME,

    /** The End key. */
    END,

    /** The Page Up key. */
    PAGE_UP,

    /** The Page Down key. */
    PAGE_DOWN,

    /** The Left Shift key. */
    LEFT_SHIFT,

    /** The Right Shift key. */
    RIGHT_SHIFT,

    /** The Left Control key. */
    LEFT_CTRL,

    /** The Right Control key. */
    RIGHT_CTRL,

    /** The Left Alt key. */
    LEFT_ALT,

    /** The Right Alt key. */
    RIGHT_ALT,

    /** The Left GUI (Windows/Command) key. */
    LEFT_GUI,

    /** The Right GUI (Windows/Command) key. */
    RIGHT_GUI,

    /** The Caps Lock key. */
    CAPS_LOCK,

    // Numeric Keypad
    /** The Minus key on the numeric keypad. */
    NUM_MINUS,

    /** The Divide key on the numeric keypad. */
    NUM_DIVIDE,

    /** The Multiply key on the numeric keypad. */
    NUM_MULTIPLY,

    /** The Plus key on the numeric keypad. */
    NUM_PLUS,

    /** The Dot (Decimal) key on the numeric keypad. */
    NUM_DOT,

    // Special Characters
    /** The Space key. */
    SPACE,

    /** The Exclamation mark (!). */
    EXCLAMATION,

    /** The Double Quote ("). */
    DOUBLE_QUOTE,

    /** The Hash symbol (#). */
    HASH,

    /** The Dollar sign ($). */
    DOLLAR,

    /** The Percent symbol (%). */
    PERCENT,

    /** The Ampersand (&). */
    AMPERSAND,

    /** The Single Quote ('). */
    SINGLE_QUOTE,

    /** The Open Parenthesis ((). */
    OPEN_PAREN,

    /** The Close Parenthesis ()). */
    CLOSE_PAREN,

    /** The Asterisk (*). */
    ASTERISK,

    /** The Plus symbol (+). */
    PLUS,

    /** The Comma (,). */
    COMMA,

    /** The Minus symbol (-). */
    MINUS,

    /** The Period or Dot (.). */
    PERIOD,

    /** The Slash (/). */
    SLASH,

    /** The Colon (:). */
    COLON,

    /** The Semicolon (;). */
    SEMICOLON,

    /** The Less Than symbol (<). */
    LESS_THAN,

    /** The Equal sign (=). */
    EQUAL,

    /** The Greater Than symbol (>). */
    GREATER_THAN,

    /** The Question Mark (?). */
    QUESTION_MARK,

    /** The At symbol (@). */
    AT_SYMBOL,

    /** The Open Bracket ([). */
    OPEN_BRACKET,

    /** The Backslash (\). */
    BACKSLASH,

    /** The Close Bracket (]). */
    CLOSE_BRACKET,

    /** The Caret (^). */
    CARET,

    /** The Underscore (_). */
    UNDERSCORE,

    /** The Backtick (`). */
    BACKTICK,

    /** The Open Brace ({). */
    OPEN_BRACE,

    /** The Pipe (|). */
    PIPE,

    /** The Close Brace (}). */
    CLOSE_BRACE,

    /** The Tilde (~). */
    TILDE,

    // Control Characters
    /** The Null character (NUL). */
    NULL_CHAR,

    /** Start of Header (SOH). */
    SOH,

    /** Start of Text (STX). */
    STX,

    /** End of Text (ETX). */
    ETX,

    /** End of Transmission (EOT). */
    EOT,

    /** Enquiry (ENQ). */
    ENQ,

    /** Acknowledge (ACK). */
    ACK,

    /** Bell (BEL). */
    BEL,

    /** Backspace (BS). */
    BS,

    /** Horizontal Tab (HT). */
    HT,

    /** Line Feed (LF). */
    LF,

    /** Vertical Tab (VT). */
    VT,

    /** Form Feed (FF). */
    FF,

    /** Carriage Return (CR). */
    CR,

    /** Shift Out (SO). */
    SO,

    /** Shift In (SI). */
    SI,

    /** Data Link Escape (DLE). */
    DLE,

    /** Device Control 1 (DC1). */
    DC1,

    /** Device Control 2 (DC2). */
    DC2,

    /** Device Control 3 (DC3). */
    DC3,

    /** Device Control 4 (DC4). */
    DC4,

    /** Negative Acknowledge (NAK). */
    NAK,

    /** Synchronous Idle (SYN). */
    SYN,

    /** End of Transmission Block (ETB). */
    ETB,

    /** Cancel (CAN). */
    CAN,

    /** End of Medium (EM). */
    EM,

    /** Substitute (SUB). */
    SUB,

    /** Escape (ESC). */
    ESCAPE_CHAR,

    /** File Separator (FS). */
    FS,

    /** Group Separator (GS). */
    GS,

    /** Record Separator (RS). */
    RS,

    /** Unit Separator (US). */
    US,

    /** Delete (DEL). */
    DEL_ASCII,

    // Code identification and length
    /** Identifies the code type. */
    CODE_IDENTIFICATION,

    /** Identifies the ISO code. */
    CODE_IDENTIFICATION_ISO,

    /** Identifies the BT code. */
    CODE_IDENTIFICATION_BT,

    /** Specifies a 2-digit code length. */
    CODE_LENGTH_2_DIGITS,

    /** Specifies a 6-digit code length. */
    CODE_LENGTH_6_DIGITS,

    // Special
    /** Represents the read direction of the input. */
    READ_DIRECTION,

    /** Represents the timestamp of the input. */
    TIMESTAMP
}
