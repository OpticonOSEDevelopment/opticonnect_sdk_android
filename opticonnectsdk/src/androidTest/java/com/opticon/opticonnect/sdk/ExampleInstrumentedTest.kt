package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.OptiConnect.bleDevicesDiscoverer
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
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

        runBlocking {
            bleDevicesDiscoverer.startDiscovery(context)
        }
    }

    @After
    fun teardown() {
        // Stop BLE discovery after the test
        bleDevicesDiscoverer.stopDiscovery()
    }

    @Test
    fun testDeviceDiscovery() {
        runBlocking {
            val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()

            // Launch a job to collect discovered devices from the discovery flow
            val collectionJob = launch {
                bleDevicesDiscoverer.getDeviceDiscoveryFlow().collect { discoveredDevice ->
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
}
