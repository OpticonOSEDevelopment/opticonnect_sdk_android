package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.constants.commands.code_specific.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.communication.CommunicationCommands
import com.opticon.opticonnect.sdk.api.constants.commands.indicator.IndicatorCommands
import com.opticon.opticonnect.sdk.api.constants.commands.single_letter.SingleLetterCommands
import com.opticon.opticonnect.sdk.api.constants.commands.symbology.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.BarcodeData
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import junit.framework.TestCase.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import timber.log.Timber

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class BluetoothCommunicationTest : BaseBluetoothTest() {
    // Test to check barcode data stream
    @Test
    fun test1BarcodeDataStream() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            val deferredBarcodeData = CompletableDeferred<BarcodeData>()
            val barcodeDataJob = launch {
                OptiConnect.bluetoothManager.subscribeToBarcodeDataStream(TEST_DEVICE_MAC_ADDRESS)
                    .collect { barcodeData ->
                    Timber.i("Barcode data received: ${barcodeData.data} for device $TEST_DEVICE_MAC_ADDRESS")
                    deferredBarcodeData.complete(barcodeData)
                }
            }

            try {
                val receivedBarcodeData = withTimeoutOrNull(10000) { deferredBarcodeData.await() }
                assertNotNull("Expected barcode data was not received.", receivedBarcodeData)
            } finally {
                barcodeDataJob.cancel()
            }
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    // Test to check the buzzer command
    @Test
    fun test2BuzzerCommand() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            val response = OptiConnect.scannerSettings.executeCommand(
                TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SingleLetterCommands.GOOD_READ_BUZZER)
            )
            delay(1000)
            assertTrue("Buzzer command failed.", response.succeeded)
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    // Test to fetch device info
    @Test
    fun test3FetchDeviceInfo() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            Timber.d("Fetching device info for device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            val deviceInfo = OptiConnect.scannerInfo.getInfo(TEST_DEVICE_MAC_ADDRESS)
            delay(1000)
            Timber.d("Device MAC: ${deviceInfo.macAddress}")
            Timber.d("Device Local Name: ${deviceInfo.localName}")
            Timber.d("Device Serial Number: ${deviceInfo.serialNumber}")
            Timber.d("Device Firmware Version: ${deviceInfo.firmwareVersion}")

            assertTrue(
                "Device info check failed.",
                deviceInfo.macAddress == TEST_DEVICE_MAC_ADDRESS &&
                        deviceInfo.localName.isNotEmpty() &&
                        deviceInfo.serialNumber.isNotEmpty() &&
                        deviceInfo.firmwareVersion.isNotEmpty()
            )
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    @Test
    fun test4SettingsCompressionTest1()  = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_QR_CODE))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_EAN_13))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_DATA_MATRIX))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_EAN_13_ONLY))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_ALL_2D_CODES_ONLY))
            var settings = OptiConnect.scannerSettings.getSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            Timber.d("Compressed settings: $settings")
            assertTrue("Settings compression test failed.", settings.size == 2
                    && settings.any { it.command == CommunicationCommands.BLUETOOTH_LOW_ENERGY_DEFAULT }
                    && settings.any { it.command == SymbologyCommands.ENABLE_ALL_2D_CODES_ONLY })
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    @Test
    fun test5SettingsCompressionTest2() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(IndicatorCommands.SINGLE_TONE_BUZZER))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(IndicatorCommands.HIGH_LOW_BUZZER))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_11))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_10_MOD_10))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(IndicatorCommands.LOW_HIGH_BUZZER))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(IndicatorCommands.HIGH_LOW_BUZZER))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.CODE_39_MIN_LENGTH_1_DIGIT))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.CODE_39_MIN_LENGTH_3_DIGITS))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.TELEPEN_NUMERIC_MODE))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.TELEPEN_ASCII_MODE))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(CodeSpecificCommands.CODE_39_MIN_LENGTH_1_DIGIT))
            var settings = OptiConnect.scannerSettings.getSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            Timber.d("Compressed settings: $settings")
            assertTrue("Settings compression test failed.", settings.size == 5 &&
                settings.any { it.command == CommunicationCommands.BLUETOOTH_LOW_ENERGY_DEFAULT } &&
                settings.any { it.command == IndicatorCommands.HIGH_LOW_BUZZER } &&
                settings.any { it.command == CodeSpecificCommands.MSI_PLESSEY_CHECK_2_CDS_MOD_11_MOD_10 } &&
                settings.any { it.command == CodeSpecificCommands.CODE_39_MIN_LENGTH_1_DIGIT } &&
                settings.any { it.command == CodeSpecificCommands.TELEPEN_ASCII_MODE })
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    @Test
    fun test6CodeSpecificTest() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = connectDevice(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow)
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.codeSpecific.codabar.setMode(TEST_DEVICE_MAC_ADDRESS, CodabarMode.ABC_CODE_ONLY)
            OptiConnect.scannerSettings.codeSpecific.codabar.setMode(TEST_DEVICE_MAC_ADDRESS, CodabarMode.CODABAR_ABC_AND_CX)
            OptiConnect.scannerSettings.codeSpecific.codabar.setMode(TEST_DEVICE_MAC_ADDRESS, CodabarMode.CX_CODE_ONLY)
            var settings = OptiConnect.scannerSettings.getSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            assertTrue("Settings compression test failed.", settings.size == 2 &&
                    settings.any { it.command == CommunicationCommands.BLUETOOTH_LOW_ENERGY_DEFAULT } &&
                    settings.any { it.command == CodeSpecificCommands.CODABAR_CX_CODE_ONLY})
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }
}