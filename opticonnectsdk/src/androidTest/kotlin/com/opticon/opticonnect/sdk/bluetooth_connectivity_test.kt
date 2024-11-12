package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import junit.framework.TestCase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class BluetoothConnectivityTest : BaseBluetoothTest() {

    // Test to check if the device is discovered
    @Test
    fun test1DeviceDiscovery() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)
    }

    // Test to check if the device stays connected
    @Test
    fun test2DeviceConnection() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
            BleDeviceConnectionState.CONNECTED)
        if (isDeviceConnected) {
            assertDeviceStaysConnected(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    @Test
    fun test3StartAndStopDiscovery() = runBlocking {
        // Stop discovery and verify it's stopped
        OptiConnect.bluetoothManager.stopDiscovery()
        delay(1000) // Short wait to ensure the operation completes
        assertFalse("Discovery is still active after calling stopDiscovery.", OptiConnect.bluetoothManager.isDiscovering)

        // Verify that no devices are discovered when discovery is stopped
        val noDevicesFound = withTimeoutOrNull(3000) {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().firstOrNull()
        }
        assertNull("Devices were discovered even after stopping discovery.", noDevicesFound)

        // Start discovery and verify it's started
        OptiConnect.bluetoothManager.startDiscovery()
        delay(1000) // Short wait to ensure the operation completes
        assertTrue("Discovery is not active after calling startDiscovery.", OptiConnect.bluetoothManager.isDiscovering)

        val devicesFound = withTimeoutOrNull(20000) {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().firstOrNull()
        }
        assertNotNull("No devices where discovered after starting discovery.", devicesFound)
    }
}
