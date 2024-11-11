package com.opticon.opticonnect.sdk.internal.services.ble.streams

import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BleDevicesStreamsHandler @Inject constructor() {

    private val barcodeStreams = mutableMapOf<String, MutableSharedFlow<BarcodeData>>()
    private val batteryStatusStreams = mutableMapOf<String, MutableSharedFlow<BatteryLevelStatus>>()
    private val batteryPercentageStreams = mutableMapOf<String, MutableSharedFlow<Int>>()
    private val commandStreams = mutableMapOf<String, MutableSharedFlow<String>>()

    private val defaultBatteryStatus = BatteryLevelStatus(
        isBatteryPresent = false,
        isWirelessCharging = false,
        isWiredCharging = false,
        isCharging = false,
        isBatteryFaulty = false,
        percentage = -1
    )
    private val defaultBatteryPercentage = -1

    fun getOrCreateBarcodeStream(deviceId: String): MutableSharedFlow<BarcodeData> {
        return barcodeStreams.getOrPut(deviceId) { MutableSharedFlow(replay = 0) }
    }

    fun getOrCreateBatteryStatusStream(deviceId: String): MutableSharedFlow<BatteryLevelStatus> {
        return batteryStatusStreams.getOrPut(deviceId) { MutableSharedFlow(replay = 1) }
    }

    fun getOrCreateBatteryPercentageStream(deviceId: String): MutableSharedFlow<Int> {
        return batteryPercentageStreams.getOrPut(deviceId) { MutableSharedFlow(replay = 1) }
    }

    fun getOrCreateCommandStream(deviceId: String): MutableSharedFlow<String> {
        return commandStreams.getOrPut(deviceId) { MutableSharedFlow(replay = 0) }
    }

    fun getLatestBatteryPercentage(deviceId: String): Int {
        return batteryPercentageStreams[deviceId]?.replayCache?.lastOrNull() ?: defaultBatteryPercentage
    }

    fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus {
        return batteryStatusStreams[deviceId]?.replayCache?.lastOrNull() ?: defaultBatteryStatus
    }
}
