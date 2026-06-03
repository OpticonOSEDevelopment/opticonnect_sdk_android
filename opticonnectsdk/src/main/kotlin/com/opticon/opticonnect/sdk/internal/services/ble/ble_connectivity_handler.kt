package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import com.opticon.opticonnect.sdk.api.exceptions.BleConnectionException
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.internal.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.battery.BatteryHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import com.opticon.opticonnect.sdk.internal.services.core.DevicesInfoManager
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BleConnectivityHandler @Inject constructor(
    private val bleClient: RxBleClient,
    private val dataHandler: DataHandler,
    private val batteryHandler: BatteryHandler,
    private val commandExecutorsManager: CommandExecutorsManager,
    private val devicesInfoManager: DevicesInfoManager,
    private val settingsHandler: SettingsHandler,
    private val context: Context
) : Closeable {

    private val connectionMutexes = ConcurrentHashMap<String, Mutex>()
    private val connectionStateFlows = ConcurrentHashMap<String, MutableSharedFlow<BleDeviceConnectionState>>()
    private val sessions = ConcurrentHashMap<String, BleDeviceSession>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getConnectionStateFlow(deviceId: String): MutableSharedFlow<BleDeviceConnectionState> {
        return connectionStateFlows.computeIfAbsent(deviceId) {
            MutableSharedFlow(replay = 1)
        }
    }

    suspend fun connect(deviceId: String) {
        val connectionMutex = connectionMutexes.computeIfAbsent(deviceId) { Mutex() }

        connectionMutex.withLock {
            val bleDevice = bleClient.getBleDevice(deviceId)
            val connectionStateFlow = getConnectionStateFlow(deviceId)

            connectionStateFlow.emit(BleDeviceConnectionState.CONNECTING)

            if (sessions.containsKey(deviceId) || bleDevice.connectionState == RxBleConnection.RxBleConnectionState.CONNECTED) {
                Timber.d("Device $deviceId already has an active session. Closing it before reconnecting.")
                closeSession(deviceId, emitDisconnecting = false)
            }

            val maxRetries = 3
            val retryDelayMillis = 500L
            val connectionTimeoutMillis = 7_000L
            var retryCount = 0
            var lastFailure: Throwable? = null

            while (true) {
                val connectionResult = CompletableDeferred<Result<Unit>>()
                val connectionDisposable = AtomicReference<Disposable?>()
                try {
                    Timber.d("Attempting to connect to device: $deviceId")
                    withTimeout(connectionTimeoutMillis) {
                        connectionDisposable.set(establishConnection(bleDevice, connectionResult))
                        connectionResult.await().getOrThrow()
                    }
                    break
                } catch (e: TimeoutCancellationException) {
                    lastFailure = e
                    Timber.d("Connection attempt timed out ($retryCount/$maxRetries): $e")
                    connectionDisposable.get()?.dispose()
                } catch (e: CancellationException) {
                    connectionDisposable.get()?.dispose()
                    connectionStateFlow.emit(BleDeviceConnectionState.DISCONNECTED)
                    throw e
                } catch (e: Exception) {
                    lastFailure = e
                    Timber.d("Connection attempt failed ($retryCount/$maxRetries): $e")
                    connectionDisposable.get()?.dispose()
                }
                retryCount++
                if (retryCount >= maxRetries) {
                    connectionStateFlow.emit(BleDeviceConnectionState.DISCONNECTED)
                    throw BleConnectionException(deviceId, retryCount, lastFailure)
                }
                delayForRetry(retryDelayMillis)
            }
        }
    }

    private fun establishConnection(
        bleDevice: RxBleDevice,
        connectionResult: CompletableDeferred<Result<Unit>>
    ): Disposable {
        val connectionDisposableRef = AtomicReference<Disposable?>()
        val connectionDisposable = bleDevice.establishConnection(false)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSubscribe {
                Timber.d("Establishing connection to device: ${bleDevice.macAddress}")
            }
            .subscribe(
                { connection ->
                    Timber.d("Connected to device: ${bleDevice.macAddress}")
                    // Emit the CONNECTED state
                    scope.launch {
                        val connectionDisposable = connectionDisposableRef.get()
                        if (connectionDisposable == null) {
                            connectionResult.complete(
                                Result.failure(
                                    IllegalStateException("Connection disposable was not available for ${bleDevice.macAddress}.")
                                )
                            )
                            return@launch
                        }

                        // Initialize the device after establishing the connection
                        val session = initializeDeviceSession(
                            deviceId = bleDevice.macAddress,
                            bleDevice = bleDevice,
                            connection = connection,
                            connectionDisposable = connectionDisposable
                        )
                        if (session != null) {
                            sessions[bleDevice.macAddress] = session
                            getConnectionStateFlow(bleDevice.macAddress).emit(BleDeviceConnectionState.CONNECTED)
                            connectionResult.complete(Result.success(Unit))
                        } else {
                            Timber.d("Failed to initialize device: ${bleDevice.macAddress}")
                            connectionResult.complete(
                                Result.failure(
                                    IllegalStateException("Failed to initialize BLE device session for ${bleDevice.macAddress}.")
                                )
                            )
                        }
                    }
                },
                { error ->
                    if (error is com.polidea.rxandroidble3.exceptions.BleDisconnectedException) {
                        Timber.d("Device ${bleDevice.macAddress} has disconnected.")
                    } else {
                        Timber.e(error, "Unexpected connection failure for device: ${bleDevice.macAddress}")
                    }
                    scope.launch {
                        closeSession(
                            deviceId = bleDevice.macAddress,
                            emitDisconnecting = false,
                            expectedConnectionDisposable = connectionDisposableRef.get()
                        )
                    }
                    connectionResult.complete(Result.failure(error))
                }
            )
        connectionDisposableRef.set(connectionDisposable)
        return connectionDisposable
    }

    private fun createConnectionStateSubscription(
        bleDevice: RxBleDevice,
        connectionDisposable: Disposable
    ): CompositeDisposable {
        val deviceId = bleDevice.macAddress
        val subscription = CompositeDisposable()

        bleDevice.observeConnectionStateChanges()
            .subscribe(
                { state ->
                    when (state) {
                        RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                            scope.launch {
                                closeSession(
                                    deviceId = deviceId,
                                    emitDisconnecting = false,
                                    expectedConnectionDisposable = connectionDisposable
                                )
                            }
                        }
                        else -> Unit
                    }
                },
                { error ->
                    Timber.e(error, "Error observing connection state for device: $deviceId")
                }
            ).addTo(subscription)

        return subscription
    }

    private suspend fun initializeDeviceSession(
        deviceId: String,
        bleDevice: RxBleDevice,
        connection: RxBleConnection,
        connectionDisposable: Disposable
    ): BleDeviceSession? {
        var connectionStateSubscription: CompositeDisposable? = null
        return try {
            Timber.d("Initializing services for device: $deviceId")

            //Add the data processor to process reads from and writes to the device.
            dataHandler.addDataProcessor(deviceId, connection)
            batteryHandler.addBatteryListener(deviceId, connection)

            // Warm settings data before command compression can use it.
            settingsHandler.initialize(context)

            // Add the command executor to send commands to the device and receive feedback for sent commands.
            commandExecutorsManager.createCommandExecutor(deviceId)

            devicesInfoManager.fetchInfo(deviceId)

            connectionStateSubscription = createConnectionStateSubscription(bleDevice, connectionDisposable)

            Timber.i("Successfully initialized services for device: $deviceId")
            BleDeviceSession(
                deviceId = deviceId,
                connectionDisposable = connectionDisposable,
                connectionStateSubscription = connectionStateSubscription,
                dataHandler = dataHandler,
                batteryHandler = batteryHandler,
                commandExecutorsManager = commandExecutorsManager
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize device: $deviceId")
            connectionStateSubscription?.dispose()
            commandExecutorsManager.close(deviceId)
            dataHandler.close(deviceId)
            batteryHandler.close(deviceId)
            connectionDisposable.dispose()
            null
        }
    }

    fun disconnect(deviceId: String) {
        try {
            closeSession(deviceId, emitDisconnecting = true)
            Timber.i("Disconnected from device: $deviceId")
        } catch (e: Exception) {
            Timber.e(e, "Error during disconnect for device: $deviceId")
        }
    }

    private fun closeSession(
        deviceId: String,
        emitDisconnecting: Boolean,
        expectedConnectionDisposable: Disposable? = null
    ) {
        val connectionStateFlow = getConnectionStateFlow(deviceId)
        if (emitDisconnecting) {
            connectionStateFlow.tryEmit(BleDeviceConnectionState.DISCONNECTING)
        }

        val sessionToClose = AtomicReference<BleDeviceSession?>()
        val ignoredStaleCallback = AtomicBoolean(false)

        sessions.compute(deviceId) { _, session ->
            when {
                session == null -> null
                expectedConnectionDisposable != null && !session.owns(expectedConnectionDisposable) -> {
                    ignoredStaleCallback.set(true)
                    session
                }
                else -> {
                    sessionToClose.set(session)
                    null
                }
            }
        }

        val session = sessionToClose.get()
        if (session != null) {
            session.close()
        } else if (ignoredStaleCallback.get()) {
            Timber.d("Ignoring stale disconnect callback for previous session: $deviceId")
            return
        } else {
            Timber.d("No active BLE device session found for device: $deviceId")
        }

        connectionStateFlow.tryEmit(BleDeviceConnectionState.DISCONNECTED)
    }

    private suspend fun delayForRetry(retryDelayMillis: Long) {
        delay(retryDelayMillis)
    }

    override fun close() {
        sessions.keys.toList().forEach { closeSession(it, emitDisconnecting = true) }
        sessions.clear()
        scope.cancel()
    }
}
