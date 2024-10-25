package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import junit.framework.TestCase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            assertDeviceStaysConnected(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }
}
