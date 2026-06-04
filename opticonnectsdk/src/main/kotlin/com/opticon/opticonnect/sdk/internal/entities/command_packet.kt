package com.opticon.opticonnect.sdk.internal.entities

internal data class CommandPacket(
    val bytes: ByteArray,
    val sequenceNumber: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommandPacket) return false

        if (!bytes.contentEquals(other.bytes)) return false
        if (sequenceNumber != other.sequenceNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + (sequenceNumber ?: 0)
        return result
    }
}
