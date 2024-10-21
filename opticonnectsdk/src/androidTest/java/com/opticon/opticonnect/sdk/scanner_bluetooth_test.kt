package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.GOOD_READ_BUZZER
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.di.DaggerTestComponent
import com.opticon.opticonnect.sdk.di.TestComponent
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import timber.log.Timber

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class ScannerBluetoothTest {
    companion object {
        private const val TEST_DEVICE_MAC_ADDRESS = "38:89:DC:0E:00:4F"  // Set the MAC address of the test device
        private lateinit var context: android.content.Context

        private lateinit var testComponent: TestComponent

        @BeforeClass
        @JvmStatic
        fun globalSetup() {
            val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()

            // Grant Bluetooth permissions for Android 12+ (S) and location for older versions
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_SCAN}").close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_CONNECT}").close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.ACCESS_FINE_LOCATION}").close()

            context = instrumentation.targetContext

            testComponent = DaggerTestComponent.builder()
                .context(context)
                .build()

            // Inject dependencies for the static companion object
            val tempInstance = ScannerBluetoothTest()
            testComponent.inject(tempInstance)

            runBlocking {
                OptiConnect.initialize(context)
            }
            OptiConnect.bluetoothManager.startDiscovery(context)
        }

        @AfterClass
        @JvmStatic
        fun globalTeardown() {
            OptiConnect.bluetoothManager.stopDiscovery()
        }
    }

    @After
    fun teardown() {
        // Stop BLE discovery after each test and disconnect from the device (if it was already disconnected, it will be handled gracefully)
        OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
    }

    suspend fun discoverDevice(deviceId: String): BleDiscoveredDevice? {
        val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()

        // Launch a job to collect discovered devices from the discovery flow
        val collectionJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { discoveredDevice ->
                if (discoveredDevice.deviceId == deviceId) {
                    deferredDevice.complete(discoveredDevice)  // Complete when the device is found
                }
            }
        }

        return try {
            // Await for the device to be discovered or time out after 10 seconds
            withTimeoutOrNull(20000) { deferredDevice.await() }
        } finally {
            collectionJob.cancel()  // Cancel the collection job after completion or timeout
        }
    }

    // Connect to the device
    suspend fun connectDevice(deviceId: String, connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>): Boolean {
        val connectionStateDeferred = CompletableDeferred<BleDeviceConnectionState?>()

        // Launch a job to listen to the connection state of the device
        val connectionStateJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { connectionState ->
                Timber.i("Connection state: $connectionState for device $deviceId")
                connectionStateFlow.emit(connectionState)  // Update the connectionStateFlow with the emitted value
            }
        }

        return try {
            // Attempt to connect to the device
            OptiConnect.bluetoothManager.connect(deviceId)

            // Await for the device to connect or time out after 10 seconds
            val connectionState = withTimeoutOrNull(20000) {
                connectionStateFlow.first { it == BleDeviceConnectionState.CONNECTED }
            }

            if (connectionState == null || connectionState != BleDeviceConnectionState.CONNECTED) {
                fail("Device with MAC address $deviceId failed to connect or is in an invalid state.")
                return false
            }

            return true
        } finally {
            connectionStateJob.cancel()  // Cancel the connection state listening job
        }
    }

    suspend fun assertDeviceStaysConnected(deviceId: String, connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>) {
        // Delay for 5 seconds to test if the device stays connected for at least 5 seconds.
        delay(5000)
        val latestConnectionState = connectionStateFlow.value
        assertEquals(
            "Device with MAC address $deviceId failed to stay connected.",
            BleDeviceConnectionState.CONNECTED,
            latestConnectionState
        )
    }

    // Test to check if device is discovered
    @Test
    fun test1DeviceDiscovery() {
        runBlocking {
            // Use the reusable discoverDevice method to find the device
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)

            // Assert that the device was discovered
            assertNotNull(
                "Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.",
                foundDevice
            )
        }
    }

    // Test to check if device stays connected
    @Test
    fun test2DeviceConnection() {
        runBlocking {
            // Discover the device
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            // Connect to the device
            val connectionStateFlow = MutableStateFlow<BleDeviceConnectionState>(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
            if (isDeviceConnected) {
                try {
                    // Assert that the device stays connected for 5 seconds
                    assertDeviceStaysConnected(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
                }
                catch (e: Exception) {
                    fail("Device with MAC address $TEST_DEVICE_MAC_ADDRESS failed to stay connected.")
                }
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test3BarcodeDataStream() {
        runBlocking {
            // Discover the device
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            // Connect to the device
            val connectionStateFlow = MutableStateFlow<BleDeviceConnectionState>(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
            if (isDeviceConnected) {
                val deferredBarcodeData = CompletableDeferred<BarcodeData?>()

                // Launch a job to listen to the barcode data stream
                val barcodeDataJob = launch {
                    OptiConnect.bluetoothManager.subscribeToBarcodeDataStream(TEST_DEVICE_MAC_ADDRESS).collect { barcodeData ->
                        Timber.i("Barcode data received: ${barcodeData.data} for device $TEST_DEVICE_MAC_ADDRESS")
                        deferredBarcodeData.complete(barcodeData)  // Complete when barcode data is received
                    }
                }

                try {
                    // Wait for barcode data or time out after 10 seconds
                    val receivedBarcodeData = withTimeoutOrNull(10000) { deferredBarcodeData.await() }
                    assertNotNull("Expected barcode data was not received.", receivedBarcodeData)
                } finally {
                    barcodeDataJob.cancel()  // Cancel barcode data job
                }
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test4BuzzerCommand() {
        runBlocking {
            // Discover the device
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            // Connect to the device
            val connectionStateFlow = MutableStateFlow<BleDeviceConnectionState>(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
            if (isDeviceConnected) {
                var response = OptiConnect.scannerSettings.executeCommand((TEST_DEVICE_MAC_ADDRESS), ScannerCommand(GOOD_READ_BUZZER, sendFeedback = false))
                delay(1000)
                assert(response.succeeded)
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test5FetchDeviceInfo() {
        runBlocking {
            // Discover the device
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            // Connect to the device
            val connectionStateFlow = MutableStateFlow<BleDeviceConnectionState>(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
            if (isDeviceConnected) {
                Timber.d("Fetching device info for device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
                OptiConnect.devicesInfoManager.fetchInfo(TEST_DEVICE_MAC_ADDRESS)
                val deviceInfo = OptiConnect.devicesInfoManager.getInfo(TEST_DEVICE_MAC_ADDRESS)
                delay(1000)
                Timber.d("Device MAC: ${deviceInfo.macAddress}")
                Timber.d("Device Local Name: ${deviceInfo.localName}")
                Timber.d("Device Serial Number: ${deviceInfo.serialNumber}")
                Timber.d("Device Firmware Version: ${deviceInfo.firmwareVersion}")
                assert(deviceInfo.macAddress == TEST_DEVICE_MAC_ADDRESS && deviceInfo.localName.isNotEmpty()
                    && deviceInfo.serialNumber.isNotEmpty() && deviceInfo.firmwareVersion.isNotEmpty())
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }
}
