package com.opticon.opticonnect.sdk.internal.services.ble

import com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.BatteryHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

internal class BleDeviceSession(
    private val deviceId: String,
    private val connectionDisposable: Disposable,
    private val connectionStateSubscription: CompositeDisposable,
    private val dataHandler: DataHandler,
    private val batteryHandler: BatteryHandler,
    private val commandExecutorsManager: CommandExecutorsManager
) : Closeable {

    private val closed = AtomicBoolean(false)

    fun owns(connectionDisposable: Disposable): Boolean {
        return this.connectionDisposable == connectionDisposable
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return

        connectionStateSubscription.dispose()
        commandExecutorsManager.close(deviceId)
        dataHandler.close(deviceId)
        batteryHandler.close(deviceId)
        connectionDisposable.dispose()

        Timber.d("Closed BLE device session for device: $deviceId")
    }
}
