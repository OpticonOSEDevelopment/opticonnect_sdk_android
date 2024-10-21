package com.opticon.opticonnect.sdk.internal.services.ble.streams.data

import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber
import java.io.Closeable

@Singleton
class BleDevicesStreamsHandler @Inject constructor(
    val dataHandler: DataHandler
) : Closeable {

    override fun close() {
        try {
            dataHandler.close()
            Timber.d("BleDevicesStreamsHandler disposed successfully.")
        } catch (e: Exception) {
            Timber.e(e, "Error during BleDevicesStreamsHandler disposal.")
        }
    }

    fun close(deviceId: String) {
        try {
            dataHandler.close(deviceId)
            Timber.d("Disposed streams for device: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Error during disposal for device: $deviceId")
        }
    }
}
