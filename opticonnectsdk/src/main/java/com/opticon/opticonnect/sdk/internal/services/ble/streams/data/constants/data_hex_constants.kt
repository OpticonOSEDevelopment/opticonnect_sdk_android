package com.opticon.opticonnect.sdk.internal.services.ble.streams.data.constants

const val ESC = '\u001B'
const val CR = '\u000D'
const val ACK = '\u0006'
const val NAK = '\u0015'
const val DLE = '\u0010'
const val STX = '\u0002'
const val ETX = '\u0003'
const val LF = '\u000A'
const val NUL = '\u0000'
const val DC1 = '\u0011'
const val DC2 = '\u0012'

// Change constants to UByte (not const because UByte can't be used as a compile-time constant)
val ESC_V: UByte = 0x1b.toUByte()
val CR_V: UByte = 0x0d.toUByte()
val ACK_V: UByte = 0x06.toUByte()
val NAK_V: UByte = 0x15.toUByte()
val DLE_V: UByte = 0x10.toUByte()
val STX_V: UByte = 0x02.toUByte()
val ETX_V: UByte = 0x03.toUByte()
val LF_V: UByte = 0x0A.toUByte()
val NUL_V: UByte = 0x00.toUByte()
val DC1_V: UByte = 0x11.toUByte()
val DC2_V: UByte = 0x12.toUByte()
val DC3_V: UByte = 0x13.toUByte()
