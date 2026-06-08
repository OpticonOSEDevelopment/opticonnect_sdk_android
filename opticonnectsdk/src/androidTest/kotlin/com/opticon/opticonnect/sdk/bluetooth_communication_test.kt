package com.opticon.opticonnect.sdk

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.constants.commands.CodeSpecificCommands
import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.constants.commands.IndicatorCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SingleLetterCommands
import com.opticon.opticonnect.sdk.api.constants.commands.SymbologyCommands
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import junit.framework.TestCase.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            val receivedBarcodeData = awaitBarcodeData(
                prompt = "test1BarcodeDataStream: please scan one barcode now.",
                timeoutMillis = 10000
            )
            assertNotNull("Expected barcode data was not received.", receivedBarcodeData)
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
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
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
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
        if (isDeviceConnected) {
            Timber.d("Fetching device info for device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            val deviceInfo = OptiConnect.scannerInfo.getInfo(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected scanner info for $TEST_DEVICE_MAC_ADDRESS.", deviceInfo)
            deviceInfo!!
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
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_QR_CODE, ledFeedback = true, buzzerFeedback = true, vibrationFeedback = true))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_EAN_13, ledFeedback = true, buzzerFeedback = true, vibrationFeedback = true))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_DATA_MATRIX, ledFeedback = true, buzzerFeedback = true, vibrationFeedback = true))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_EAN_13_ONLY, ledFeedback = true, buzzerFeedback = true, vibrationFeedback = true))
            OptiConnect.scannerSettings.executeCommand(TEST_DEVICE_MAC_ADDRESS, ScannerCommand(SymbologyCommands.ENABLE_ALL_2D_CODES_ONLY, ledFeedback = true, buzzerFeedback = true, vibrationFeedback = true))
            waitForScannerSettingsToSettle()
            val settings = getSettingsFromConnectedTestDevice()
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
        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
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
            waitForScannerSettingsToSettle()
            val settings = getSettingsFromConnectedTestDevice()
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
    fun test5ASettingsGetterStateTest() = runBlocking {
        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

        val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
        val isDeviceConnected = toggleDeviceConnectionState(
            TEST_DEVICE_MAC_ADDRESS,
            connectionStateFlow,
            BleDeviceConnectionState.CONNECTED
        )
        if (isDeviceConnected) {
            OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)

            assertEquals(
                "Default buzzer type getter returned an unexpected value.",
                BuzzerType.HIGH_LOW_BUZZER,
                OptiConnect.scannerSettings.indicator.getBuzzerType(TEST_DEVICE_MAC_ADDRESS)
            )
            assertEquals(
                "Default read mode getter returned an unexpected value.",
                ReadMode.SINGLE_READ,
                OptiConnect.scannerSettings.readOptions.getReadMode(TEST_DEVICE_MAC_ADDRESS)
            )

            assertTrue(
                "Failed to set buzzer type.",
                OptiConnect.scannerSettings.indicator.setBuzzerType(
                    TEST_DEVICE_MAC_ADDRESS,
                    BuzzerType.LOW_HIGH_BUZZER
                ).succeeded
            )
            assertTrue(
                "Failed to set buzzer duration.",
                OptiConnect.scannerSettings.indicator.setBuzzerDuration(
                    TEST_DEVICE_MAC_ADDRESS,
                    BuzzerDuration.DURATION_75_MS
                ).succeeded
            )
            assertTrue(
                "Failed to set buzzer volume.",
                OptiConnect.scannerSettings.indicator.setBuzzerVolume(TEST_DEVICE_MAC_ADDRESS, 37).succeeded
            )
            assertTrue(
                "Failed to set LED color.",
                OptiConnect.scannerSettings.indicator.setLED(
                    TEST_DEVICE_MAC_ADDRESS,
                    LEDColor(0, 255, 0)
                ).succeeded
            )
            assertTrue(
                "Failed to set read mode.",
                OptiConnect.scannerSettings.readOptions.setReadMode(
                    TEST_DEVICE_MAC_ADDRESS,
                    ReadMode.MULTIPLE_READ
                ).succeeded
            )
            assertTrue(
                "Failed to set read time.",
                OptiConnect.scannerSettings.readOptions.setReadTime(
                    TEST_DEVICE_MAC_ADDRESS,
                    ReadTime.EIGHT_SECONDS
                ).succeeded
            )
            assertTrue(
                "Failed to set illumination mode.",
                OptiConnect.scannerSettings.readOptions.setIlluminationMode(
                    TEST_DEVICE_MAC_ADDRESS,
                    IlluminationMode.ALTERNATING_FLOODLIGHT
                ).succeeded
            )
            assertTrue(
                "Failed to disable aiming.",
                OptiConnect.scannerSettings.readOptions.setAiming(TEST_DEVICE_MAC_ADDRESS, false).succeeded
            )
            assertTrue(
                "Failed to enable trigger repeat.",
                OptiConnect.scannerSettings.readOptions.setTriggerRepeat(TEST_DEVICE_MAC_ADDRESS, true).succeeded
            )
            assertTrue(
                "Failed to disable delete key.",
                OptiConnect.scannerSettings.readOptions.setDeleteKey(TEST_DEVICE_MAC_ADDRESS, false).succeeded
            )

            assertConfiguredGetterState(
                context = "runtime state after successful setters",
                includeParameterSettings = true
            )

            waitForScannerSettingsToSettle()
            val fetchedSettings = getSettingsFromConnectedTestDevice()
            val fetchedBuzzerVolume = fetchedSettings.firstOrNull {
                it.command == IndicatorCommands.PERSISTENT_SET_BUZZER
            }
            val fetchedLed = fetchedSettings.firstOrNull {
                it.command == IndicatorCommands.PERSISTENT_SET_LED
            }
            BaseBluetoothTest.logTestStep("Fetched settings after getter state test: $fetchedSettings")
            BaseBluetoothTest.logTestStep(
                "Fetched buzzer volume setting after getter state test: $fetchedBuzzerVolume"
            )
            BaseBluetoothTest.logTestStep(
                "Fetched LED setting after getter state test: $fetchedLed"
            )
            assertNotNull(
                "Fetched settings did not include persistent buzzer volume (${IndicatorCommands.PERSISTENT_SET_BUZZER}).",
                fetchedBuzzerVolume
            )
            assertNotNull(
                "Fetched settings did not include persistent LED color (${IndicatorCommands.PERSISTENT_SET_LED}).",
                fetchedLed
            )
            assertEquals(
                "Fetched persistent buzzer volume did not include the expected direct-input parameters.",
                listOf("Q3", "Q7"),
                fetchedBuzzerVolume!!.parameters
            )
            assertEquals(
                "Fetched persistent LED color did not include the expected direct-input parameters.",
                listOf("Q0", "Q0", "\$F", "\$F", "Q0", "Q0"),
                fetchedLed!!.parameters
            )

            assertConfiguredGetterState(
                context = "rebuilt state after getSettings",
                includeParameterSettings = true
            )
        } else {
            fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
        }
    }

    @Test
    fun test6CodeSpecificTest() {
        runBlocking {
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull(
                "Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.",
                foundDevice
            )

            val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
            if (isDeviceConnected) {
                OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)

                val abcOnlyResponse = OptiConnect.scannerSettings.codeSpecific.codabar.setMode(
                    TEST_DEVICE_MAC_ADDRESS,
                    CodabarMode.ABC_CODE_ONLY
                )
                assertTrue("Failed to set Codabar mode ABC_CODE_ONLY.", abcOnlyResponse.succeeded)

                val abcAndCxResponse = OptiConnect.scannerSettings.codeSpecific.codabar.setMode(
                    TEST_DEVICE_MAC_ADDRESS,
                    CodabarMode.CODABAR_ABC_AND_CX
                )
                assertTrue("Failed to set Codabar mode CODABAR_ABC_AND_CX.", abcAndCxResponse.succeeded)

                val cxOnlyResponse = OptiConnect.scannerSettings.codeSpecific.codabar.setMode(
                    TEST_DEVICE_MAC_ADDRESS,
                    CodabarMode.CX_CODE_ONLY
                )
                assertTrue("Failed to set Codabar mode CX_CODE_ONLY.", cxOnlyResponse.succeeded)

                waitForScannerSettingsToSettle()

                OptiConnect.scannerSettings.getSettings(
                    TEST_DEVICE_MAC_ADDRESS
                ).let { settings ->
                    assertTrue(
                        "Settings compression test failed.",
                        settings.size == 2 &&
                                settings.any { it.command == CommunicationCommands.BLUETOOTH_LOW_ENERGY_DEFAULT } &&
                                settings.any { it.command == CodeSpecificCommands.CODABAR_CX_CODE_ONLY }
                    )
                }
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test7GetLatestBatteryPercentageTest() {
        runBlocking {
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
            if (isDeviceConnected) {
                val batteryPercentage = OptiConnect.bluetoothManager.getLatestBatteryPercentage(TEST_DEVICE_MAC_ADDRESS)
                assertNotNull("Expected latest battery percentage for $TEST_DEVICE_MAC_ADDRESS.", batteryPercentage)
                Timber.d("Battery percentage: $batteryPercentage%")
                batteryPercentage!!
                assert(batteryPercentage in 0..100)
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test8GetLatestBatteryStatusTest() {
        runBlocking {
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
            if (isDeviceConnected) {
                val batteryStatus = OptiConnect.bluetoothManager.getLatestBatteryStatus(TEST_DEVICE_MAC_ADDRESS)
                assertNotNull("Expected latest battery status for $TEST_DEVICE_MAC_ADDRESS.", batteryStatus)
                batteryStatus!!
                Timber.d("Latest battery status: ${batteryStatus.percentage}% - Charging: ${batteryStatus.isCharging}")
                assert(batteryStatus.percentage in 0..100)
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun test9BatteryStatusStream() {
        runBlocking {
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
            if (isDeviceConnected) {
                val deferredBatteryStatus = CompletableDeferred<BatteryLevelStatus>()
                val batteryStatusJob = launch {
                    OptiConnect.bluetoothManager.listenToBatteryStatus(TEST_DEVICE_MAC_ADDRESS)
                        .collect { batteryStatus ->
                            Timber.i("Battery status received: ${batteryStatus.percentage}% for device $TEST_DEVICE_MAC_ADDRESS. Is charging: ${batteryStatus.isCharging}")
                            deferredBatteryStatus.complete(batteryStatus)
                        }
                }

                try {
                    val receivedBatteryStatus = withTimeoutOrNull(20000) { deferredBatteryStatus.await() }
                    assertNotNull("Expected battery status data was not received.", receivedBatteryStatus)
                } finally {
                    batteryStatusJob.cancel()
                }
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun testAConnectionPoolSet() {
        runBlocking {
            val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
            assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

            val connectionStateFlow = MutableStateFlow(BleDeviceConnectionState.DISCONNECTED)
            val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                BleDeviceConnectionState.CONNECTED)
            if (isDeviceConnected) {
                try {
                    OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
                    val connectionPoolIdTarget = "4567"
                    OptiConnect.scannerSettings.connectionPool.setId(TEST_DEVICE_MAC_ADDRESS, connectionPoolIdTarget)
                    val isDeviceDisconnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                        BleDeviceConnectionState.DISCONNECTED)
                    if (isDeviceDisconnected) {
                        val foundDevice = discoverDevice(TEST_DEVICE_MAC_ADDRESS)
                        assertNotNull("Expected device with MAC address $TEST_DEVICE_MAC_ADDRESS was not found.", foundDevice)

                        Timber.d("Found device with MAC address $TEST_DEVICE_MAC_ADDRESS and connection pool ${foundDevice?.connectionPoolId}.")

                        val isDeviceConnected = toggleDeviceConnectionState(TEST_DEVICE_MAC_ADDRESS, connectionStateFlow,
                            BleDeviceConnectionState.CONNECTED)
                        if (isDeviceConnected) {
                            assertEquals(OptiConnect.scannerSettings.connectionPool.getId(TEST_DEVICE_MAC_ADDRESS), connectionPoolIdTarget)
                        }
                    }
                } finally {
                    runCatching {
                        OptiConnect.scannerSettings.connectionPool.resetId(TEST_DEVICE_MAC_ADDRESS)
                        waitForScannerSettingsToSettle()
                    }.onFailure {
                        Timber.w(it, "Failed to restore connection pool ID after test")
                    }
                }
            } else {
                fail("Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS.")
            }
        }
    }

    @Test
    fun testZReinitializeAfterCloseReadsBarcodeDataStream() = runBlocking {
        assertTrue(
            "Failed to connect to device with MAC address $TEST_DEVICE_MAC_ADDRESS before SDK teardown.",
            connectToTestDevice()
        )

        OptiConnect.scannerSettings.resetSettings(TEST_DEVICE_MAC_ADDRESS)
        val barcodeBeforeTeardown = awaitBarcodeData()
        assertNotNull(
            "Expected barcode data before SDK teardown. Scan a barcode within the test timeout.",
            barcodeBeforeTeardown
        )

        OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
        delay(1000)

        var sdkClosed = false
        try {
            OptiConnect.close()
            sdkClosed = true
            delay(1000)

            BaseBluetoothTest.initializeOptiConnectForTest()
            sdkClosed = false

            assertTrue(
                "Failed to reconnect to device with MAC address $TEST_DEVICE_MAC_ADDRESS after SDK reinitialization.",
                connectToTestDevice()
            )

            val barcodeAfterReinitialize = awaitBarcodeData()
            assertNotNull(
                "Expected barcode data after SDK reinitialization. Scan a barcode within the test timeout.",
                barcodeAfterReinitialize
            )
        } finally {
            if (sdkClosed) {
                BaseBluetoothTest.initializeOptiConnectForTest()
            }
        }
    }

    private fun assertConfiguredGetterState(context: String, includeParameterSettings: Boolean) {
        assertEquals(
            "Unexpected buzzer type in $context.",
            BuzzerType.LOW_HIGH_BUZZER,
            OptiConnect.scannerSettings.indicator.getBuzzerType(TEST_DEVICE_MAC_ADDRESS)
        )
        assertEquals(
            "Unexpected buzzer duration in $context.",
            BuzzerDuration.DURATION_75_MS,
            OptiConnect.scannerSettings.indicator.getBuzzerDuration(TEST_DEVICE_MAC_ADDRESS)
        )
        if (includeParameterSettings) {
            assertEquals(
                "Unexpected buzzer volume in $context.",
                37,
                OptiConnect.scannerSettings.indicator.getBuzzerVolume(TEST_DEVICE_MAC_ADDRESS)
            )
            assertEquals(
                "Unexpected LED color in $context.",
                LEDColor(0, 255, 0),
                OptiConnect.scannerSettings.indicator.getLED(TEST_DEVICE_MAC_ADDRESS)
            )
        }
        assertEquals(
            "Unexpected read mode in $context.",
            ReadMode.MULTIPLE_READ,
            OptiConnect.scannerSettings.readOptions.getReadMode(TEST_DEVICE_MAC_ADDRESS)
        )
        assertEquals(
            "Unexpected read time in $context.",
            ReadTime.EIGHT_SECONDS,
            OptiConnect.scannerSettings.readOptions.getReadTime(TEST_DEVICE_MAC_ADDRESS)
        )
        assertEquals(
            "Unexpected illumination mode in $context.",
            IlluminationMode.ALTERNATING_FLOODLIGHT,
            OptiConnect.scannerSettings.readOptions.getIlluminationMode(TEST_DEVICE_MAC_ADDRESS)
        )
        assertFalse(
            "Aiming should be disabled in $context.",
            OptiConnect.scannerSettings.readOptions.isAimingEnabled(TEST_DEVICE_MAC_ADDRESS)
        )
        assertTrue(
            "Trigger repeat should be enabled in $context.",
            OptiConnect.scannerSettings.readOptions.isTriggerRepeatEnabled(TEST_DEVICE_MAC_ADDRESS)
        )
        assertFalse(
            "Delete key should be disabled in $context.",
            OptiConnect.scannerSettings.readOptions.isDeleteKeyEnabled(TEST_DEVICE_MAC_ADDRESS)
        )
    }
}
