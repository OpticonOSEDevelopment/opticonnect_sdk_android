package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class BleDiscoveryTest {

    private val testDeviceMacAddress = "38:89:DC:0E:00:4F"  // Replace with the actual MAC Address

    @Before
    fun setup() {
        // Grant Bluetooth permissions for Android 12+ (S) and location for older versions
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()

        // Use UiAutomation to grant permissions
        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_SCAN}"
        ).close()

        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_CONNECT}"
        ).close()

        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.ACCESS_FINE_LOCATION}"
        ).close()

        val context = instrumentation.targetContext

        // Initialize the OptiConnect SDK
        runBlocking {
            OptiConnect.initialize(context)
        }

        // Start the discovery process via OptiConnect public API
        OptiConnect.bluetoothManager.startDiscovery(context)
    }

    @After
    fun teardown() {
        // Stop BLE discovery after the test
        OptiConnect.bluetoothManager.stopDiscovery()
    }

    @Test
    fun testDeviceDiscovery() {
        runBlocking {
            val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()

            // Launch a job to collect discovered devices from the discovery flow
            val collectionJob = launch {
                OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { discoveredDevice ->
                    Timber.i("Discovered device: ${discoveredDevice.deviceId} - ${discoveredDevice.name}")

                    if (discoveredDevice.deviceId == testDeviceMacAddress) {
                        deferredDevice.complete(discoveredDevice)  // Complete when the device is found
                    }
                }
            }

            try {
                // Await for the device to be discovered or time out after 10 seconds
                val foundDevice = withTimeoutOrNull(10000) { deferredDevice.await() }

                assertNotNull(
                    "Expected device with MAC address $testDeviceMacAddress was not found.",
                    foundDevice
                )
            } finally {
                collectionJob.cancel()  // Cancel the collection job after completion or timeout
            }
        }
    }

    @Test
    fun testDeviceConnection() {
        runBlocking {
            val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()

            // Launch a job to collect discovered devices from the discovery flow
            val collectionJob = launch {
                OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { discoveredDevice ->
                    Timber.i("Discovered device: ${discoveredDevice.deviceId} - ${discoveredDevice.name}")

                    if (discoveredDevice.deviceId == testDeviceMacAddress) {
                        deferredDevice.complete(discoveredDevice)  // Complete when the device is found
                    }
                }
            }

            try {
                // Await for the device to be discovered or time out after 10 seconds
                val foundDevice = withTimeoutOrNull(10000) { deferredDevice.await() }

                assertNotNull(
                    "Expected device with MAC address $testDeviceMacAddress was not found.",
                    foundDevice
                )

                // If the device is found, attempt to connect
                if (foundDevice != null) {
                    val connectionStateDeferred = CompletableDeferred<BleDeviceConnectionState?>()

                    // Launch a job to listen to the connection state of the device
                    val connectionStateJob = launch {
                        OptiConnect.bluetoothManager.listenToConnectionState(testDeviceMacAddress).collect { connectionState ->
                            Timber.i("Connection state: $connectionState for device $testDeviceMacAddress")
                            if (connectionState == BleDeviceConnectionState.CONNECTED) {
                                connectionStateDeferred.complete(connectionState)  // Complete when connected
                            }
                        }
                    }

                    // Attempt to connect to the device
                    try {
                        OptiConnect.bluetoothManager.connect(testDeviceMacAddress)

                        // Await for the device to connect or time out after 10 seconds
                        val connectionState = withTimeoutOrNull(10000) { connectionStateDeferred.await() }

                        // Assert that the device was successfully connected
                        assertEquals(
                            "Device with MAC address $testDeviceMacAddress failed to connect.",
                            BleDeviceConnectionState.CONNECTED,
                            connectionState
                        )
                    } finally {
                        // Disconnect the device and cancel the connection state collection job
                        OptiConnect.bluetoothManager.disconnect(testDeviceMacAddress)
                        connectionStateJob.cancel()
                    }
                }
            } finally {
                collectionJob.cancel()  // Cancel the discovery collection job after completion or timeout
            }
        }
    }
}
