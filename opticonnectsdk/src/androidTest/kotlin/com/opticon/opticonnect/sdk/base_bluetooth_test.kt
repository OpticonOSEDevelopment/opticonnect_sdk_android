package com.opticon.opticonnect.sdk

import android.Manifest
import android.util.Log
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.entities.CommandData
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
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import timber.log.Timber

abstract class BaseBluetoothTest {

    companion object {
        private const val TEST_LOG_TAG = "OptiConnectTest"
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
            logTestStep("Initializing OptiConnect test SDK.")
            OptiConnect.initialize(context)
            OptiConnect.setDebugLoggingEnabled(true)
            OptiConnect.bluetoothManager.startDiscovery()
            OptiConnect.scannerFeedback.set(led = false, buzzer = false, vibration = false)
            logTestStep("Discovery started and scanner feedback disabled.")
        }

        @AfterClass
        @JvmStatic
        fun globalTeardown() {
            Thread.sleep(200)
            runCatching { OptiConnect.bluetoothManager.stopDiscovery() }
            runCatching { OptiConnect.close() }
        }

        fun logTestStep(message: String) {
            Log.i(TEST_LOG_TAG, message)
            Timber.i(message)
        }
    }

    @get:Rule
    val testLogger = object : TestWatcher() {
        override fun starting(description: Description) {
            logTestStep("Starting test: ${description.methodName}.")
            when (description.methodName) {
                "test1BarcodeDataStream" -> {
                    logTestStep("This test requires one barcode scan. Wait for the scan prompt.")
                }
                "testZReinitializeAfterCloseReadsBarcodeDataStream" -> {
                    logTestStep("This test requires two barcode scans. Wait for the scan prompts.")
                }
            }
        }

        override fun finished(description: Description) {
            logTestStep("Finished test: ${description.methodName}.")
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

        logTestStep("Waiting for BLE discovery of $deviceId.")
        val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()
        val collectionJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { discoveredDevice ->
                if (discoveredDevice.deviceId == deviceId) {
                    logTestStep("Discovered test scanner $deviceId.")
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
                        logTestStep("Stopping discovery before connecting to $deviceId.")
                        OptiConnect.bluetoothManager.stopDiscovery()
                        delay(500)
                    }
                    logTestStep("Connecting to test scanner $deviceId.")
                    OptiConnect.bluetoothManager.connect(deviceId)
                }
                BleDeviceConnectionState.DISCONNECTED -> {
                    logTestStep("Disconnecting test scanner $deviceId.")
                    OptiConnect.bluetoothManager.disconnect(deviceId)
                    delay(1000)
                }
                else -> throw IllegalArgumentException("Unsupported target state: $targetState")
            }

            // Wait for the desired state within a timeout period
            val connectionState = withTimeoutOrNull(20000) {
                connectionStateFlow.first { it == targetState }
            }

            logTestStep("Connection state for $deviceId is ${connectionState ?: "timeout waiting for $targetState"}.")
            connectionState == targetState
        } finally {
            connectionStateJob.cancel()
        }
    }

    suspend fun connectToTestDevice(): Boolean {
        val foundDevice = withTimeoutOrNull(10000) {
            discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        }
        if (foundDevice == null) {
            logTestStep("Discovery did not find $TEST_DEVICE_MAC_ADDRESS; trying direct MAC connection.")
            Timber.w("Device $TEST_DEVICE_MAC_ADDRESS was not discovered before direct connection attempt.")
        }

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val connected = runCatching {
            toggleDeviceConnectionState(
                TEST_DEVICE_MAC_ADDRESS,
                connectionStateFlow,
                BleDeviceConnectionState.CONNECTED
            )
        }.onFailure {
            Timber.w(it, "Failed to connect to test device $TEST_DEVICE_MAC_ADDRESS.")
        }.getOrDefault(false)
        if (connected) {
            logTestStep("Test scanner $TEST_DEVICE_MAC_ADDRESS connected.")
            delay(750)
        } else {
            logTestStep("Failed to connect to test scanner $TEST_DEVICE_MAC_ADDRESS.")
        }
        return connected
    }

    suspend fun waitForScannerSettingsToSettle() {
        logTestStep("Waiting for scanner settings to settle.")
        delay(9000)
    }

    suspend fun getSettingsFromConnectedTestDevice(): List<CommandData> {
        logTestStep("Fetching scanner settings.")
        return runCatching {
            OptiConnect.scannerSettings.getSettings(TEST_DEVICE_MAC_ADDRESS)
        }.getOrElse { error ->
            if (!error.message.orEmpty().contains("Command executor closed")) {
                throw error
            }

            logTestStep("Settings fetch found a closed executor; reconnecting once before retry.")
            Timber.w(error, "Settings fetch hit a closed command executor; reconnecting test device once.")
            OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
            delay(1000)

            check(connectToTestDevice()) {
                "Failed to reconnect to device with MAC address $TEST_DEVICE_MAC_ADDRESS before fetching settings."
            }

            logTestStep("Retrying scanner settings fetch after reconnect.")
            OptiConnect.scannerSettings.getSettings(TEST_DEVICE_MAC_ADDRESS)
        }
    }

    suspend fun awaitBarcodeData(
        prompt: String = "Waiting for barcode scan data. Please scan a barcode now.",
        timeoutMillis: Long = 30000
    ): BarcodeData? {
        logTestStep(prompt)
        val deferredBarcodeData = CompletableDeferred<BarcodeData>()
        val barcodeDataJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(TEST_DEVICE_MAC_ADDRESS)
                .collect { barcodeData ->
                    if (!deferredBarcodeData.isCompleted) {
                        logTestStep("Barcode data received: ${barcodeData.data}")
                        deferredBarcodeData.complete(barcodeData)
                    }
                }
        }

        return try {
            withTimeoutOrNull(timeoutMillis) { deferredBarcodeData.await() }.also { barcodeData ->
                if (barcodeData == null) {
                    logTestStep("Timed out waiting for barcode scan data.")
                }
            }
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
