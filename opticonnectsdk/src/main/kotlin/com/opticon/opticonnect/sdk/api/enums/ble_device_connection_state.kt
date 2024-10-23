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

/**
 * Maps a [RxBleConnection.RxBleConnectionState] to a custom [BleDeviceConnectionState].
 *
 * This function converts the connection state returned by RxAndroidBle into a custom enum.
 *
 * @param state The [RxBleConnection.RxBleConnectionState] provided by RxAndroidBle.
 * @return The corresponding [BleDeviceConnectionState] based on the [RxBleConnection.RxBleConnectionState].
 */
fun mapBluetoothConnectionState(state: RxBleConnection.RxBleConnectionState): BleDeviceConnectionState {
    return when (state) {
        RxBleConnection.RxBleConnectionState.CONNECTED -> BleDeviceConnectionState.CONNECTED
        RxBleConnection.RxBleConnectionState.DISCONNECTED -> BleDeviceConnectionState.DISCONNECTED
        RxBleConnection.RxBleConnectionState.CONNECTING -> BleDeviceConnectionState.CONNECTING
        RxBleConnection.RxBleConnectionState.DISCONNECTING -> BleDeviceConnectionState.DISCONNECTING
        else -> BleDeviceConnectionState.DISCONNECTED
    }
}
