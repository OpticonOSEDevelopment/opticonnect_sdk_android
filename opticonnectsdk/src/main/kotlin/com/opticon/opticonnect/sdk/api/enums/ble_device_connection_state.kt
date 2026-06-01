package com.opticon.opticonnect.sdk.api.enums

/**
 * Enum representing the connection state of a BLE device.
 */
@Suppress("unused")
enum class BleDeviceConnectionState {
    /** The device is disconnected. */
    DISCONNECTED,

    /** The device is in the process of connecting. */
    CONNECTING,

    /** The device is connected. */
    CONNECTED,

    /** The device is in the process of disconnecting. */
    DISCONNECTING
}
