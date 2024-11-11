package com.opticon.opticonnect.sdk.internal.services.ble.streams.battery

import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants
import com.opticon.opticonnect.sdk.internal.services.ble.streams.BleDevicesStreamsHandler
import com.polidea.rxandroidble3.RxBleConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BatteryHandler @Inject constructor(
    private val bleDevicesStreamsHandler: BleDevicesStreamsHandler
) : Closeable {

    private val batteryListeners = mutableMapOf<String, BatteryListener>()
    private val mutex = Mutex()

    fun getBatteryPercentageStream(deviceId: String): Flow<Int> {
        return bleDevicesStreamsHandler.getOrCreateBatteryPercentageStream(deviceId)
    }

    fun getBatteryStatusStream(deviceId: String): Flow<BatteryLevelStatus> {
        return bleDevicesStreamsHandler.getOrCreateBatteryStatusStream(deviceId)
    }

    fun getLatestBatteryPercentage(deviceId: String): Int {
        val batteryListener = getBatteryListener(deviceId)
        return batteryListener?.getLatestBatteryPercentage() ?: run {
            Timber.e("Battery listener not found for device: $deviceId")
            -1
        }
    }

    fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus {
        val batteryListener = getBatteryListener(deviceId)
        return batteryListener?.getLatestBatteryStatus() ?: run {
            Timber.e("Battery listener not found for device: $deviceId")
            BatteryLevelStatus(
                isBatteryPresent = false,
                isWirelessCharging = false,
                isWiredCharging = false,
                isCharging = false,
                isBatteryFaulty = false,
                percentage = -1
            ) // Default status
        }
    }

    suspend fun addBatteryListener(deviceId: String, connection: RxBleConnection): BatteryListener {
        return mutex.withLock {
            try {
                // Create and initialize the BatteryListener
                val batteryListener = BatteryListener(
                    deviceId = deviceId,
                    batteryLevelUuid = UuidConstants.BATTERY_LEVEL_CHARACTERISTIC_UUID,
                    batteryStatusUuid = UuidConstants.BATTERY_LEVEL_STATUS_CHARACTERISTIC_UUID,
                    connection = connection,
                    bleDevicesStreamsHandler = bleDevicesStreamsHandler
                )

                batteryListener.initialize()

                batteryListeners[deviceId] = batteryListener
                return batteryListener
            } catch (e: Exception) {
                val msg = "Failed to initialize battery listener for device $deviceId: $e"
                Timber.e(msg)
                throw Exception(msg)
            }
        }
    }

    private fun getBatteryListener(deviceId: String): BatteryListener? {
        return batteryListeners[deviceId]
    }

    override fun close() {
        batteryListeners.values.forEach { it.close() }
        batteryListeners.clear()
        Timber.d("All battery listeners disposed and cleared.")
    }

    fun close(deviceId: String) {
        batteryListeners[deviceId]?.close()
        batteryListeners.remove(deviceId)
        Timber.d("Disposed and removed battery listener for device: $deviceId")
    }
}
