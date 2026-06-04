package com.opticon.opticonnect.sdk.internal.services.ble.streams

import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.internal.entities.CommandResponsePacket
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BleDevicesStreamsHandler @Inject constructor() {

    private val barcodeStreams = ConcurrentHashMap<String, MutableSharedFlow<BarcodeData>>()
    private val batteryStatusStreams = ConcurrentHashMap<String, MutableSharedFlow<BatteryLevelStatus>>()
    private val batteryPercentageStreams = ConcurrentHashMap<String, MutableSharedFlow<Int>>()
    private val commandStreams = ConcurrentHashMap<String, MutableSharedFlow<CommandResponsePacket>>()

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
        return barcodeStreams.computeIfAbsent(deviceId) { MutableSharedFlow(replay = 0) }
    }

    fun getOrCreateBatteryStatusStream(deviceId: String): MutableSharedFlow<BatteryLevelStatus> {
        return batteryStatusStreams.computeIfAbsent(deviceId) { MutableSharedFlow(replay = 1) }
    }

    fun getOrCreateBatteryPercentageStream(deviceId: String): MutableSharedFlow<Int> {
        return batteryPercentageStreams.computeIfAbsent(deviceId) { MutableSharedFlow(replay = 1) }
    }

    fun getOrCreateCommandStream(deviceId: String): MutableSharedFlow<CommandResponsePacket> {
        return commandStreams.computeIfAbsent(deviceId) { MutableSharedFlow(replay = 0) }
    }

    fun getLatestBatteryPercentage(deviceId: String): Int {
        return batteryPercentageStreams[deviceId]?.replayCache?.lastOrNull() ?: defaultBatteryPercentage
    }

    fun getLatestBatteryStatus(deviceId: String): BatteryLevelStatus {
        return batteryStatusStreams[deviceId]?.replayCache?.lastOrNull() ?: defaultBatteryStatus
    }
}
