package com.opticon.opticonnect.sdk.api.exceptions

/**
 * Thrown when the SDK cannot establish a BLE connection to the requested scanner.
 */
class BleConnectionException(
    val deviceId: String,
    val attempts: Int,
    cause: Throwable? = null
) : RuntimeException(
    "Failed to connect to BLE device $deviceId after $attempts attempts.",
    cause
)
