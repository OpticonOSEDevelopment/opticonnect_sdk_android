package com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants

internal const val ESC = '\u001B'
internal const val CR = '\u000D'
internal const val ACK = '\u0006'
internal const val NAK = '\u0015'
internal const val DLE = '\u0010'
internal const val STX = '\u0002'
internal const val ETX = '\u0003'
internal const val LF = '\u000A'
internal const val NUL = '\u0000'
internal const val DC1 = '\u0011'
internal const val DC2 = '\u0012'

// Change constants to UByte (not const because UByte can't be used as a compile-time constant)
internal val ESC_V: UByte = 0x1b.toUByte()
internal val CR_V: UByte = 0x0d.toUByte()
internal val ACK_V: UByte = 0x06.toUByte()
internal val NAK_V: UByte = 0x15.toUByte()
internal val DLE_V: UByte = 0x10.toUByte()
internal val STX_V: UByte = 0x02.toUByte()
internal val ETX_V: UByte = 0x03.toUByte()
internal val LF_V: UByte = 0x0A.toUByte()
internal val NUL_V: UByte = 0x00.toUByte()
internal val DC1_V: UByte = 0x11.toUByte()
internal val DC2_V: UByte = 0x12.toUByte()
internal val DC3_V: UByte = 0x13.toUByte()
