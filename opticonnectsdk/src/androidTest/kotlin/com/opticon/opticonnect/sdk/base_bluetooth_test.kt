package com.opticon.opticonnect.sdk

import android.Manifest
import androidx.test.platform.app.InstrumentationRegistry
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass

abstract class BaseBluetoothTest {

    companion object {
        const val TEST_DEVICE_MAC_ADDRESS = "38:89:DC:0E:00:4F"
        lateinit var context: android.content.Context

        @BeforeClass
        @JvmStatic
        fun globalSetup() {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.BLUETOOTH_SCAN}"
            ).close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.BLUETOOTH_CONNECT}"
            ).close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.ACCESS_FINE_LOCATION}"
            ).close()

            context = instrumentation.targetContext
            OptiConnect.initialize(context)
            OptiConnect.bluetoothManager.startDiscovery()
            OptiConnect.scannerFeedback.set(led = false, buzzer = false, vibration = false)
        }

        @AfterClass
        @JvmStatic
        fun globalTeardown() {
            Thread.sleep(200)
            OptiConnect.bluetoothManager.stopDiscovery()
        }
    }

    @Before
    fun setup() {
        runBlocking {
            OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
            withTimeoutOrNull(2000) {
                OptiConnect.bluetoothManager.listenToConnectionState(TEST_DEVICE_MAC_ADDRESS)
                    .firstOrNull { it == BleDeviceConnectionState.DISCONNECTED }
            }
        }
    }

    @After
    fun teardown() {
        OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
    }

    suspend fun discoverDevice(deviceId: String): BleDiscoveredDevice? {
        val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()
        val collectionJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { discoveredDevice ->
                if (discoveredDevice.deviceId == deviceId) {
                    deferredDevice.complete(discoveredDevice)
                }
            }
        }

        return try {
            withTimeoutOrNull(60000) { deferredDevice.await() }
        } finally {
            collectionJob.cancel()
        }
    }

    suspend fun toggleDeviceConnectionState(
        deviceId: String,
        connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>,
        targetState: BleDeviceConnectionState
    ): Boolean {
        val connectionStateJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { connectionState ->
                connectionStateFlow.emit(connectionState)
            }
        }

        return try {
            // Attempt to connect or disconnect based on the target state
            delay(500)
            when (targetState) {
                BleDeviceConnectionState.CONNECTED -> OptiConnect.bluetoothManager.connect(deviceId)
                BleDeviceConnectionState.DISCONNECTED -> OptiConnect.bluetoothManager.disconnect(deviceId)
                else -> throw IllegalArgumentException("Unsupported target state: $targetState")
            }

            // Wait for the desired state within a timeout period
            val connectionState = withTimeoutOrNull(20000) {
                connectionStateFlow.first { it == targetState }
            }

            connectionState == targetState
        } finally {
            connectionStateJob.cancel()
        }
    }

    suspend fun assertDeviceStaysConnected(deviceId: String, connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>, delayTime: Long = 5000) {
        delay(delayTime)
        val latestConnectionState = connectionStateFlow.value
        assertEquals(
            "Device with MAC address $deviceId failed to stay connected.",
            BleDeviceConnectionState.CONNECTED,
            latestConnectionState
        )
    }
}
