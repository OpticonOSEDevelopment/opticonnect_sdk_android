package com.opticon.opticonnect.sdk.api.enums

import com.polidea.rxandroidble3.RxBleConnection

/**
 * Enum representing the connection state of a BLE device.
 */
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
