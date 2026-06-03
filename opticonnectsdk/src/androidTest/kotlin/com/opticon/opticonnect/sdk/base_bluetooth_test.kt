package com.opticon.opticonnect.sdk

import android.Manifest
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
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
        const val TEST_DEVICE_MAC_ADDRESS = "38:89:DC:00:00:3E"
        lateinit var context: android.content.Context

        @BeforeClass
        @JvmStatic
        fun globalSetup() {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val packageName = instrumentation.targetContext.packageName
            val runtimePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            runtimePermissions.forEach { permission ->
                instrumentation.uiAutomation.executeShellCommand(
                    "pm grant $packageName $permission"
                ).close()
            }

            context = instrumentation.targetContext
            initializeOptiConnectForTest()
        }

        fun initializeOptiConnectForTest() {
            OptiConnect.initialize(context)
            OptiConnect.setDebugLoggingEnabled(true)
            OptiConnect.bluetoothManager.startDiscovery()
            OptiConnect.scannerFeedback.set(led = false, buzzer = false, vibration = false)
        }

        @AfterClass
        @JvmStatic
        fun globalTeardown() {
            Thread.sleep(200)
            runCatching { OptiConnect.bluetoothManager.stopDiscovery() }
            runCatching { OptiConnect.close() }
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
            delay(1000)
        }
    }

    @After
    fun teardown() {
        OptiConnect.initialize(context)
        OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
        Thread.sleep(1000)
    }

    suspend fun discoverDevice(deviceId: String): BleDiscoveredDevice? {
        if (!OptiConnect.bluetoothManager.isDiscovering) {
            OptiConnect.bluetoothManager.startDiscovery()
        }

        val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()
        val collectionJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { discoveredDevice ->
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
            delay(750)
            when (targetState) {
                BleDeviceConnectionState.CONNECTED -> {
                    if (OptiConnect.bluetoothManager.isDiscovering) {
                        OptiConnect.bluetoothManager.stopDiscovery()
                        delay(500)
                    }
                    OptiConnect.bluetoothManager.connect(deviceId)
                }
                BleDeviceConnectionState.DISCONNECTED -> {
                    OptiConnect.bluetoothManager.disconnect(deviceId)
                    delay(1000)
                }
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

    suspend fun connectToTestDevice(): Boolean {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        if (foundDevice == null) return false

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val connected = toggleDeviceConnectionState(
            TEST_DEVICE_MAC_ADDRESS,
            connectionStateFlow,
            BleDeviceConnectionState.CONNECTED
        )
        if (connected) {
            delay(750)
        }
        return connected
    }

    suspend fun waitForScannerSettingsToSettle() {
        delay(9000)
    }

    suspend fun awaitBarcodeData(timeoutMillis: Long = 30000): BarcodeData? {
        val deferredBarcodeData = CompletableDeferred<BarcodeData>()
        val barcodeDataJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(TEST_DEVICE_MAC_ADDRESS)
                .collect { barcodeData ->
                    if (!deferredBarcodeData.isCompleted) {
                        deferredBarcodeData.complete(barcodeData)
                    }
                }
        }

        return try {
            withTimeoutOrNull(timeoutMillis) { deferredBarcodeData.await() }
        } finally {
            barcodeDataJob.cancel()
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
