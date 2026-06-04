package com.opticon.opticonnect.sdk.internal.entities

internal data class CommandResponsePacket(
    val data: String,
    val sequenceNumber: Int?
)
