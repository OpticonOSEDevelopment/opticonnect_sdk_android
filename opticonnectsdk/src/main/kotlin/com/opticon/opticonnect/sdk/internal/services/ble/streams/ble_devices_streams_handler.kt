package com.opticon.opticonnect.sdk.internal.services.ble.streams

import com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.BatteryHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber
import java.io.Closeable

@Singleton
internal class BleDevicesStreamsHandler @Inject constructor(
    val dataHandler: DataHandler,
    val batteryHandler: BatteryHandler
) : Closeable {

    override fun close() {
        try {
            dataHandler.close()
            batteryHandler.close()
            Timber.d("BleDevicesStreamsHandler disposed successfully.")
        } catch (e: Exception) {
            Timber.e(e, "Error during BleDevicesStreamsHandler disposal.")
        }
    }

    fun close(deviceId: String) {
        try {
            dataHandler.close(deviceId)
            batteryHandler.close(deviceId)
            Timber.d("Disposed streams for device: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Error during disposal for device: $deviceId")
        }
    }
}
