package com.opticon.opticonnect.sdk.internal.services.ble.streams.battery

import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.constants.BatteryLevelStatusFlags
import com.polidea.rxandroidble3.RxBleConnection
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Closeable
import java.util.UUID

internal class BatteryListener(
    private val deviceId: String,
    private val batteryLevelUuid: UUID,
    private val batteryStatusUuid: UUID,
    private val connection: RxBleConnection
) : Closeable {

    private val compositeDisposable = CompositeDisposable()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var latestBatteryStatus: BatteryLevelStatus = BatteryLevelStatus(
        isBatteryPresent = false,
        isWirelessCharging = false,
        isWiredCharging = false,
        isCharging = false,
        isBatteryFaulty = false,
        percentage = -1
    )

    private var latestBatteryPercentage: Int = -1

    // SharedFlows for broadcasting data to multiple subscribers
    private val _batteryStatusStream = MutableSharedFlow<BatteryLevelStatus>(replay = 1)
    private val _batteryPercentageStream = MutableSharedFlow<Int>(replay = 1)

    // Exposing the SharedFlows as immutable flows
    val batteryStatusStream: SharedFlow<BatteryLevelStatus> get() = _batteryStatusStream
    val batteryPercentageStream: SharedFlow<Int> get() = _batteryPercentageStream

    fun getLatestBatteryStatus(): BatteryLevelStatus = latestBatteryStatus
    fun getLatestBatteryPercentage(): Int = latestBatteryPercentage

    suspend fun initialize() {
        try {
            // Wait for battery level and status to be initialized
            val batteryLevelInit = initializeBatteryLevel()
            val batteryStatusInit = initializeBatteryStatus()

            // Await both initializations to complete
            batteryLevelInit.await()
            batteryStatusInit.await()

            setupNotification(batteryLevelUuid) { data ->
                if (data.isNotEmpty()) {
                    scope.launch {
                        latestBatteryPercentage = data[0].toInt()
                        _batteryPercentageStream.emit(latestBatteryPercentage)
                    }
                }
            }

            setupNotification(batteryStatusUuid) { data ->
                if (data.isNotEmpty()) {
                    scope.launch {
                        val batteryStatus = parseBatteryLevelStatus(data)
                        latestBatteryStatus = batteryStatus
                        _batteryStatusStream.emit(batteryStatus)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize battery listener for device: $deviceId")
        }
    }

    private fun setupNotification(uuid: UUID, onNotificationReceived: (ByteArray) -> Unit) {
        connection.setupNotification(uuid)
            .flatMap { it }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { data ->
                    scope.launch {
                        try {
                            onNotificationReceived(data)
                            Timber.d("Notification received for UUID: $uuid on device: $deviceId")
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing notification for UUID: $uuid on device: $deviceId")
                        }
                    }
                },
                { error ->
                    Timber.e(error, "Error setting up notification for UUID: $uuid on device: $deviceId")
                }
            )
            .addTo(compositeDisposable)
    }

    private fun initializeBatteryLevel(): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()

        connection.readCharacteristic(batteryLevelUuid)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { value ->
                    if (value.isNotEmpty()) {
                        latestBatteryPercentage = value[0].toInt()
                        deferred.complete(Unit)
                    } else {
                        deferred.completeExceptionally(
                            Exception("Battery level characteristic is empty for device: $deviceId")
                        )
                    }
                },
                { error ->
                    Timber.e(error, "Error reading battery level for device: $deviceId")
                    latestBatteryPercentage = 0
                    deferred.completeExceptionally(error)
                }
            )
            .addTo(compositeDisposable)

        return deferred
    }

    private suspend fun initializeBatteryStatus(): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()

        connection.readCharacteristic(batteryStatusUuid)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { value ->
                    if (value.isNotEmpty()) {
                        latestBatteryStatus = parseBatteryLevelStatus(value)
                        deferred.complete(Unit)
                    } else {
                        deferred.completeExceptionally(
                            Exception("Battery status characteristic is empty for device: $deviceId")
                        )
                    }
                },
                { error ->
                    Timber.e(error, "Error reading battery status for device: $deviceId")
                    latestBatteryStatus = BatteryLevelStatus(
                        isBatteryPresent = false,
                        isWirelessCharging = false,
                        isWiredCharging = false,
                        isCharging = false,
                        isBatteryFaulty = false,
                        percentage = 0
                    )
                    deferred.completeExceptionally(error)
                }
            )
            .addTo(compositeDisposable)

        return deferred
    }

    private fun parseBatteryLevelStatus(data: ByteArray): BatteryLevelStatus {
        val powerState = (data[1].toInt() shl 8) or data[2].toInt()

        return BatteryLevelStatus(
            isBatteryPresent = (powerState and BatteryLevelStatusFlags.BLE_BAS_BATTERY_PRESENT_FLAG) != 0,
            isWirelessCharging = (powerState and BatteryLevelStatusFlags.BLE_BAS_WIRELESS_CHARGING_FLAG) != 0,
            isWiredCharging = (powerState and BatteryLevelStatusFlags.BLE_BAS_WIRED_CHARGING_FLAG) != 0,
            isCharging = (powerState and BatteryLevelStatusFlags.BLE_BAS_IS_CHARGING_FLAG) != 0,
            isBatteryFaulty = (powerState and BatteryLevelStatusFlags.BLE_BAS_BATTERY_FAULT_FLAG) != 0,
            percentage = if ((data[0].toInt() and BatteryLevelStatusFlags.BLE_BAS_BATTERY_LEVEL_FLAG) != 0) data[3].toInt() else -1
        )
    }

    override fun close() {
        compositeDisposable.clear()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                job.cancelAndJoin()
                Timber.d("Closed resources for device: $deviceId")
            } catch (e: CancellationException) {
                Timber.d("Battery listener job was cancelled for device: $deviceId")
            } catch (e: Exception) {
                Timber.e(e, "Error during battery listener job cancellation for device: $deviceId")
            }
        }
    }
}