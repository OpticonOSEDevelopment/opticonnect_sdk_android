package com.opticon.opticonnect_sdk_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.entities.LEDColor
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect.sdk.api.enums.DirectInputKey
import com.opticon.opticonnect.sdk.api.enums.SymbologyType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.BuzzerType
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.GoodReadLedDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.PositiveAndNegativeBarcodesMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.VibratorDuration
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import com.opticon.opticonnect_sdk_example.ui.theme.Opticonnect_SDK_ExampleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ScannerUiState(
    val device: BleDiscoveredDevice,
    val connectionState: BleDeviceConnectionState = BleDeviceConnectionState.DISCONNECTED,
    val barcodeData: String? = null,
    val batteryPercentage: Int? = null,
    val isPoweredOrCharging: Boolean? = null,
    val settings: ScannerSettingsUiState = ScannerSettingsUiState(),
    val settingsLoaded: Boolean = false,
    val settingBusy: Boolean = false,
    val settingMessage: String? = null
)

data class ScannerSettingsUiState(
    val buzzerEnabled: Boolean = true,
    val buzzerVolume: Int = 100,
    val buzzerType: BuzzerType = BuzzerType.SINGLE_TONE_BUZZER,
    val buzzerDuration: BuzzerDuration = BuzzerDuration.DURATION_100_MS,
    val vibratorEnabled: Boolean = false,
    val vibratorDuration: VibratorDuration = VibratorDuration.DURATION_100_MS,
    val ledColor: LEDColor = LEDColor(0, 255, 0),
    val goodReadLedDuration: GoodReadLedDuration = GoodReadLedDuration.DURATION_60_MS,
    val readMode: ReadMode = ReadMode.SINGLE_READ,
    val readTime: ReadTime = ReadTime.FIVE_SECONDS,
    val illuminationMode: IlluminationMode = IlluminationMode.ENABLE_FLOODLIGHT,
    val positiveNegativeMode: PositiveAndNegativeBarcodesMode =
        PositiveAndNegativeBarcodesMode.POSITIVE_BARCODES,
    val aimingEnabled: Boolean = true,
    val triggerRepeatEnabled: Boolean = false,
    val deleteKeyEnabled: Boolean = false,
    val symbologies: Map<SymbologyType, Boolean> = defaultSymbologySelection.associateWith { false },
    val codabarMode: CodabarMode = CodabarMode.NORMAL,
    val codabarCheckDigitEnabled: Boolean = false,
    val preamble: List<DirectInputKey> = emptyList(),
    val prefixAllCodes: List<DirectInputKey> = emptyList(),
    val suffixAllCodes: List<DirectInputKey> = emptyList(),
    val postamble: List<DirectInputKey> = emptyList()
)

private val defaultSymbologySelection = listOf(
    SymbologyType.CODE_39,
    SymbologyType.CODE_128,
    SymbologyType.EAN_13,
    SymbologyType.CODABAR,
    SymbologyType.QR_CODE,
    SymbologyType.DATA_MATRIX
)

private val ledSwatches = listOf(
    "Green" to LEDColor(0, 255, 0),
    "Blue" to LEDColor(0, 0, 255),
    "Red" to LEDColor(255, 0, 0),
    "White" to LEDColor(255, 255, 255),
    "Off" to LEDColor(0, 0, 0)
)

class AdvancedScannerActivity : ComponentActivity() {
    private val scanners = mutableMapOf<String, ScannerUiState>()
    private var scannerList by mutableStateOf<List<ScannerUiState>>(emptyList())
    private var discoveryRunning by mutableStateOf(false)
    private var discoveryJob: Job? = null
    private val connectionStateJobs = mutableMapOf<String, Job>()
    private val deviceDataJobs = mutableMapOf<String, MutableList<Job>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
        checkBluetoothPermissions()
    }

    @Composable
    private fun MainScreen() {
        Opticonnect_SDK_ExampleTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ScannerListScreen(
                    scanners = scannerList,
                    discoveryRunning = discoveryRunning,
                    onStartDiscovery = ::startDiscovery,
                    onStopDiscovery = ::stopDiscovery,
                    onConnect = ::connectToDevice,
                    onDisconnect = ::disconnectDevice,
                    onRefreshSettings = ::refreshScannerSettings,
                    onBuzzerEnabledChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Buzzer ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.indicator.toggleBuzzer(deviceId, enabled)
                        }
                    },
                    onBuzzerVolumeChanged = { deviceId, volume ->
                        applySetting(deviceId, "Buzzer volume set to $volume") {
                            OptiConnect.scannerSettings.indicator.setBuzzerVolume(deviceId, volume)
                        }
                    },
                    onBuzzerTypeChanged = { deviceId, type ->
                        applySetting(deviceId, "Buzzer type set to ${type.label()}") {
                            OptiConnect.scannerSettings.indicator.setBuzzerType(deviceId, type)
                        }
                    },
                    onBuzzerDurationChanged = { deviceId, duration ->
                        applySetting(deviceId, "Buzzer duration set to ${duration.label()}") {
                            OptiConnect.scannerSettings.indicator.setBuzzerDuration(deviceId, duration)
                        }
                    },
                    onVibratorEnabledChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Vibrator ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.indicator.toggleVibrator(deviceId, enabled)
                        }
                    },
                    onVibratorDurationChanged = { deviceId, duration ->
                        applySetting(deviceId, "Vibration duration set to ${duration.label()}") {
                            OptiConnect.scannerSettings.indicator.setVibratorDuration(deviceId, duration)
                        }
                    },
                    onLedColorChanged = { deviceId, color ->
                        applySetting(deviceId, "LED color set to ${color.rgbLabel()}") {
                            OptiConnect.scannerSettings.indicator.setLED(deviceId, color)
                        }
                    },
                    onGoodReadLedDurationChanged = { deviceId, duration ->
                        applySetting(deviceId, "Good-read LED duration set to ${duration.label()}") {
                            OptiConnect.scannerSettings.indicator.setGoodReadLedDuration(deviceId, duration)
                        }
                    },
                    onReadModeChanged = { deviceId, mode ->
                        applySetting(deviceId, "Read mode set to ${mode.label()}") {
                            OptiConnect.scannerSettings.readOptions.setReadMode(deviceId, mode)
                        }
                    },
                    onReadTimeChanged = { deviceId, readTime ->
                        applySetting(deviceId, "Read time set to ${readTime.label()}") {
                            OptiConnect.scannerSettings.readOptions.setReadTime(deviceId, readTime)
                        }
                    },
                    onIlluminationModeChanged = { deviceId, mode ->
                        applySetting(deviceId, "Illumination set to ${mode.label()}") {
                            OptiConnect.scannerSettings.readOptions.setIlluminationMode(deviceId, mode)
                        }
                    },
                    onPositiveNegativeModeChanged = { deviceId, mode ->
                        applySetting(deviceId, "Barcode polarity set to ${mode.label()}") {
                            OptiConnect.scannerSettings.readOptions.setPositiveAndNegativeBarcodesMode(deviceId, mode)
                        }
                    },
                    onAimingChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Aiming ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.readOptions.setAiming(deviceId, enabled)
                        }
                    },
                    onTriggerRepeatChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Trigger repeat ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.readOptions.setTriggerRepeat(deviceId, enabled)
                        }
                    },
                    onDeleteKeyChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Delete key ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.readOptions.setDeleteKey(deviceId, enabled)
                        }
                    },
                    onSymbologyChanged = { deviceId, symbology, enabled ->
                        applySetting(deviceId, "${symbology.label()} ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.symbology.setSymbology(deviceId, symbology, enabled)
                        }
                    },
                    onCodabarModeChanged = { deviceId, mode ->
                        applySetting(deviceId, "Codabar mode set to ${mode.label()}") {
                            OptiConnect.scannerSettings.codeSpecific.codabar.setMode(deviceId, mode)
                        }
                    },
                    onCodabarCheckDigitChanged = { deviceId, enabled ->
                        applySetting(deviceId, "Codabar check digit ${if (enabled) "enabled" else "disabled"}") {
                            OptiConnect.scannerSettings.codeSpecific.codabar.setCheckCD(deviceId, enabled)
                        }
                    },
                    onSetPreambleDemo = { deviceId ->
                        applySetting(deviceId, "Preamble set") {
                            OptiConnect.scannerSettings.formatting.setPreambleFromKeys(
                                deviceId,
                                listOf(DirectInputKey.LETTER_S, DirectInputKey.LETTER_T)
                            )
                        }
                    },
                    onClearPreamble = { deviceId ->
                        applySetting(deviceId, "Preamble cleared") {
                            OptiConnect.scannerSettings.formatting.clearPreamble(deviceId)
                        }
                    },
                    onSetPrefixDemo = { deviceId ->
                        applySetting(deviceId, "All-codes prefix set") {
                            OptiConnect.scannerSettings.formatting.setPrefixFromKeys(
                                deviceId,
                                listOf(DirectInputKey.LETTER_P)
                            )
                        }
                    },
                    onClearPrefixes = { deviceId ->
                        applySetting(deviceId, "Prefixes cleared") {
                            OptiConnect.scannerSettings.formatting.clearAllPrefixes(deviceId)
                        }
                    },
                    onSetSuffixDemo = { deviceId ->
                        applySetting(deviceId, "All-codes suffix set") {
                            OptiConnect.scannerSettings.formatting.setSuffixFromKeys(
                                deviceId,
                                listOf(DirectInputKey.RETURN_KEY)
                            )
                        }
                    },
                    onClearSuffixes = { deviceId ->
                        applySetting(deviceId, "Suffixes cleared") {
                            OptiConnect.scannerSettings.formatting.clearAllSuffixes(deviceId)
                        }
                    },
                    onSetPostambleDemo = { deviceId ->
                        applySetting(deviceId, "Postamble set") {
                            OptiConnect.scannerSettings.formatting.setPostambleFromKeys(
                                deviceId,
                                listOf(DirectInputKey.LETTER_E, DirectInputKey.LETTER_N, DirectInputKey.LETTER_D)
                            )
                        }
                    },
                    onClearPostamble = { deviceId ->
                        applySetting(deviceId, "Postamble cleared") {
                            OptiConnect.scannerSettings.formatting.clearPostamble(deviceId)
                        }
                    }
                )
            }
        }
    }

    private fun checkBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (toRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(toRequest.toTypedArray())
        } else {
            initializeOptiConnectAndStartDiscovery()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            initializeOptiConnectAndStartDiscovery()
        } else {
            Toast.makeText(this, "Bluetooth permissions are required.", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeOptiConnectAndStartDiscovery() {
        OptiConnect.initialize(this)
        startDiscovery()
    }

    private fun startDiscovery() {
        if (discoveryRunning) return

        discoveryRunning = true
        OptiConnect.bluetoothManager.startDiscovery()
        discoveryJob = lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { device ->
                val current = scanners[device.deviceId]
                scanners[device.deviceId] = current?.copy(device = device) ?: ScannerUiState(device)
                publishScanners()
            }
        }
    }

    private fun stopDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = null
        discoveryRunning = false
        OptiConnect.bluetoothManager.stopDiscovery()
    }

    private fun connectToDevice(deviceId: String) {
        if (scanners[deviceId]?.connectionState == BleDeviceConnectionState.CONNECTING) return

        updateScanner(deviceId) {
            it.copy(connectionState = BleDeviceConnectionState.CONNECTING, settingMessage = null)
        }
        startConnectionStateListener(deviceId)

        lifecycleScope.launch {
            try {
                OptiConnect.bluetoothManager.connect(deviceId)
            } catch (e: Exception) {
                cancelDeviceListeners(deviceId)
                updateScanner(deviceId) {
                    it.copy(
                        connectionState = BleDeviceConnectionState.DISCONNECTED,
                        settingBusy = false,
                        settingMessage = e.message ?: "Connection failed."
                    )
                }
                Toast.makeText(this@AdvancedScannerActivity, "Failed to connect: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startConnectionStateListener(deviceId: String) {
        connectionStateJobs[deviceId]?.cancel()
        connectionStateJobs[deviceId] = lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { state ->
                updateScanner(deviceId) {
                    it.copy(connectionState = state)
                }

                Log.d("OptiConnect", "Device $deviceId state changed to: $state")

                when (state) {
                    BleDeviceConnectionState.CONNECTED -> {
                        if (!deviceDataJobs.containsKey(deviceId)) {
                            startListeningToDeviceData(deviceId)
                        }
                        if (scanners[deviceId]?.settingsLoaded != true) {
                            refreshScannerSettings(deviceId)
                        }
                    }
                    BleDeviceConnectionState.DISCONNECTED -> {
                        cancelDeviceDataJobs(deviceId)
                        updateScanner(deviceId) {
                            it.copy(
                                barcodeData = null,
                                batteryPercentage = null,
                                isPoweredOrCharging = null,
                                settingBusy = false
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun startListeningToDeviceData(deviceId: String) {
        cancelDeviceDataJobs(deviceId)

        deviceDataJobs.getOrPut(deviceId) { mutableListOf() } += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
                Log.d("OptiConnect", "Barcode data for $deviceId: ${barcode.data}")
                updateScanner(deviceId) { it.copy(barcodeData = barcode.data) }
            }
        }

        deviceDataJobs.getOrPut(deviceId) { mutableListOf() } += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryPercentage(deviceId).collect { battery ->
                updateScanner(deviceId) { it.copy(batteryPercentage = battery) }
            }
        }

        deviceDataJobs.getOrPut(deviceId) { mutableListOf() } += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryStatus(deviceId).collect { status ->
                val isPowered = status.isCharging || status.isWiredCharging || status.isWirelessCharging
                updateScanner(deviceId) { it.copy(isPoweredOrCharging = isPowered) }
                Log.d("OptiConnect", "Battery status for $deviceId: $status")
            }
        }
    }

    private fun refreshScannerSettings(deviceId: String) {
        updateScanner(deviceId) {
            it.copy(settingBusy = true, settingMessage = "Refreshing settings from scanner")
        }

        lifecycleScope.launch {
            try {
                OptiConnect.scannerSettings.getSettings(deviceId)
                updateScanner(deviceId) {
                    it.copy(
                        settings = readSettingsSnapshot(deviceId),
                        settingsLoaded = true,
                        settingBusy = false,
                        settingMessage = "Settings refreshed"
                    )
                }
            } catch (e: Exception) {
                updateScanner(deviceId) {
                    it.copy(
                        settingBusy = false,
                        settingMessage = e.message ?: "Could not refresh settings."
                    )
                }
            }
        }
    }

    private fun applySetting(
        deviceId: String,
        successMessage: String,
        command: suspend () -> CommandResponse
    ) {
        updateScanner(deviceId) {
            it.copy(settingBusy = true, settingMessage = null)
        }

        lifecycleScope.launch {
            try {
                val result = command()
                updateScanner(deviceId) {
                    if (result.succeeded) {
                        it.copy(
                            settings = readSettingsSnapshot(deviceId),
                            settingsLoaded = true,
                            settingBusy = false,
                            settingMessage = successMessage
                        )
                    } else {
                        Log.w(
                            "OptiConnect",
                            "Setting command failed for $deviceId. Response: '${result.response}'"
                        )
                        it.copy(
                            settingBusy = false,
                            settingMessage = result.failureMessage()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w("OptiConnect", "Setting command threw for $deviceId", e)
                updateScanner(deviceId) {
                    it.copy(
                        settingBusy = false,
                        settingMessage = e.message ?: "Setting command failed; scanner did not return a response."
                    )
                }
            }
        }
    }

    private fun readSettingsSnapshot(deviceId: String): ScannerSettingsUiState {
        val indicator = OptiConnect.scannerSettings.indicator
        val readOptions = OptiConnect.scannerSettings.readOptions
        val symbology = OptiConnect.scannerSettings.symbology
        val codabar = OptiConnect.scannerSettings.codeSpecific.codabar
        val formatting = OptiConnect.scannerSettings.formatting

        return ScannerSettingsUiState(
            buzzerEnabled = indicator.isBuzzerEnabled(deviceId),
            buzzerVolume = indicator.getBuzzerVolume(deviceId),
            buzzerType = indicator.getBuzzerType(deviceId),
            buzzerDuration = indicator.getBuzzerDuration(deviceId),
            vibratorEnabled = indicator.isVibratorEnabled(deviceId),
            vibratorDuration = indicator.getVibratorDuration(deviceId),
            ledColor = indicator.getLED(deviceId),
            goodReadLedDuration = indicator.getGoodReadLedDuration(deviceId),
            readMode = readOptions.getReadMode(deviceId),
            readTime = readOptions.getReadTime(deviceId),
            illuminationMode = readOptions.getIlluminationMode(deviceId),
            positiveNegativeMode = readOptions.getPositiveAndNegativeBarcodesMode(deviceId),
            aimingEnabled = readOptions.isAimingEnabled(deviceId),
            triggerRepeatEnabled = readOptions.isTriggerRepeatEnabled(deviceId),
            deleteKeyEnabled = readOptions.isDeleteKeyEnabled(deviceId),
            symbologies = defaultSymbologySelection.associateWith { type ->
                symbology.isSymbologyEnabled(deviceId, type)
            },
            codabarMode = codabar.getMode(deviceId),
            codabarCheckDigitEnabled = codabar.isCheckCDEnabled(deviceId),
            preamble = formatting.getPreamble(deviceId),
            prefixAllCodes = formatting.getPrefix(deviceId),
            suffixAllCodes = formatting.getSuffix(deviceId),
            postamble = formatting.getPostamble(deviceId)
        )
    }

    private fun disconnectDevice(deviceId: String) {
        updateScanner(deviceId) {
            it.copy(connectionState = BleDeviceConnectionState.DISCONNECTING, settingBusy = false)
        }
        cancelDeviceListeners(deviceId)
        OptiConnect.bluetoothManager.disconnect(deviceId)
        updateScanner(deviceId) {
            it.copy(
                connectionState = BleDeviceConnectionState.DISCONNECTED,
                barcodeData = null,
                batteryPercentage = null,
                isPoweredOrCharging = null
            )
        }
    }

    private fun updateScanner(deviceId: String, update: (ScannerUiState) -> ScannerUiState) {
        scanners[deviceId]?.let {
            scanners[deviceId] = update(it)
            publishScanners()
        }
    }

    private fun publishScanners() {
        scannerList = scanners.values.sortedBy { it.device.name.ifBlank { it.device.deviceId } }
    }

    private fun cancelDeviceListeners(deviceId: String) {
        connectionStateJobs.remove(deviceId)?.cancel()
        cancelDeviceDataJobs(deviceId)
    }

    private fun cancelDeviceDataJobs(deviceId: String) {
        deviceDataJobs.remove(deviceId)?.forEach { it.cancel() }
    }

    private fun cancelAllDeviceListeners() {
        connectionStateJobs.values.forEach { it.cancel() }
        connectionStateJobs.clear()
        deviceDataJobs.values.flatten().forEach { it.cancel() }
        deviceDataJobs.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDiscovery()
        cancelAllDeviceListeners()
        OptiConnect.close()
    }
}

@Composable
fun ScannerListScreen(
    scanners: List<ScannerUiState>,
    discoveryRunning: Boolean,
    onStartDiscovery: () -> Unit,
    onStopDiscovery: () -> Unit,
    onConnect: (String) -> Unit,
    onDisconnect: (String) -> Unit,
    onRefreshSettings: (String) -> Unit,
    onBuzzerEnabledChanged: (String, Boolean) -> Unit,
    onBuzzerVolumeChanged: (String, Int) -> Unit,
    onBuzzerTypeChanged: (String, BuzzerType) -> Unit,
    onBuzzerDurationChanged: (String, BuzzerDuration) -> Unit,
    onVibratorEnabledChanged: (String, Boolean) -> Unit,
    onVibratorDurationChanged: (String, VibratorDuration) -> Unit,
    onLedColorChanged: (String, LEDColor) -> Unit,
    onGoodReadLedDurationChanged: (String, GoodReadLedDuration) -> Unit,
    onReadModeChanged: (String, ReadMode) -> Unit,
    onReadTimeChanged: (String, ReadTime) -> Unit,
    onIlluminationModeChanged: (String, IlluminationMode) -> Unit,
    onPositiveNegativeModeChanged: (String, PositiveAndNegativeBarcodesMode) -> Unit,
    onAimingChanged: (String, Boolean) -> Unit,
    onTriggerRepeatChanged: (String, Boolean) -> Unit,
    onDeleteKeyChanged: (String, Boolean) -> Unit,
    onSymbologyChanged: (String, SymbologyType, Boolean) -> Unit,
    onCodabarModeChanged: (String, CodabarMode) -> Unit,
    onCodabarCheckDigitChanged: (String, Boolean) -> Unit,
    onSetPreambleDemo: (String) -> Unit,
    onClearPreamble: (String) -> Unit,
    onSetPrefixDemo: (String) -> Unit,
    onClearPrefixes: (String) -> Unit,
    onSetSuffixDemo: (String) -> Unit,
    onClearSuffixes: (String) -> Unit,
    onSetPostambleDemo: (String) -> Unit,
    onClearPostamble: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("OptiConnect scanners", style = MaterialTheme.typography.headlineSmall)
                Text(
                    if (discoveryRunning) "Discovery running" else "Discovery stopped",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (discoveryRunning) {
                OutlinedButton(onClick = onStopDiscovery) { Text("Stop") }
            } else {
                Button(onClick = onStartDiscovery) { Text("Scan") }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (scanners.isEmpty()) {
            Text("No scanners discovered yet.")
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(scanners, key = { it.device.deviceId }) { scanner ->
                ScannerCard(
                    scanner = scanner,
                    onConnect = { onConnect(scanner.device.deviceId) },
                    onDisconnect = { onDisconnect(scanner.device.deviceId) },
                    onRefreshSettings = { onRefreshSettings(scanner.device.deviceId) },
                    onBuzzerEnabledChanged = { onBuzzerEnabledChanged(scanner.device.deviceId, it) },
                    onBuzzerVolumeChanged = { onBuzzerVolumeChanged(scanner.device.deviceId, it) },
                    onBuzzerTypeChanged = { onBuzzerTypeChanged(scanner.device.deviceId, it) },
                    onBuzzerDurationChanged = { onBuzzerDurationChanged(scanner.device.deviceId, it) },
                    onVibratorEnabledChanged = { onVibratorEnabledChanged(scanner.device.deviceId, it) },
                    onVibratorDurationChanged = { onVibratorDurationChanged(scanner.device.deviceId, it) },
                    onLedColorChanged = { onLedColorChanged(scanner.device.deviceId, it) },
                    onGoodReadLedDurationChanged = { onGoodReadLedDurationChanged(scanner.device.deviceId, it) },
                    onReadModeChanged = { onReadModeChanged(scanner.device.deviceId, it) },
                    onReadTimeChanged = { onReadTimeChanged(scanner.device.deviceId, it) },
                    onIlluminationModeChanged = { onIlluminationModeChanged(scanner.device.deviceId, it) },
                    onPositiveNegativeModeChanged = { onPositiveNegativeModeChanged(scanner.device.deviceId, it) },
                    onAimingChanged = { onAimingChanged(scanner.device.deviceId, it) },
                    onTriggerRepeatChanged = { onTriggerRepeatChanged(scanner.device.deviceId, it) },
                    onDeleteKeyChanged = { onDeleteKeyChanged(scanner.device.deviceId, it) },
                    onSymbologyChanged = { symbology, enabled ->
                        onSymbologyChanged(scanner.device.deviceId, symbology, enabled)
                    },
                    onCodabarModeChanged = { onCodabarModeChanged(scanner.device.deviceId, it) },
                    onCodabarCheckDigitChanged = { onCodabarCheckDigitChanged(scanner.device.deviceId, it) },
                    onSetPreambleDemo = { onSetPreambleDemo(scanner.device.deviceId) },
                    onClearPreamble = { onClearPreamble(scanner.device.deviceId) },
                    onSetPrefixDemo = { onSetPrefixDemo(scanner.device.deviceId) },
                    onClearPrefixes = { onClearPrefixes(scanner.device.deviceId) },
                    onSetSuffixDemo = { onSetSuffixDemo(scanner.device.deviceId) },
                    onClearSuffixes = { onClearSuffixes(scanner.device.deviceId) },
                    onSetPostambleDemo = { onSetPostambleDemo(scanner.device.deviceId) },
                    onClearPostamble = { onClearPostamble(scanner.device.deviceId) }
                )
            }
        }
    }
}

@Composable
fun ScannerCard(
    scanner: ScannerUiState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onRefreshSettings: () -> Unit,
    onBuzzerEnabledChanged: (Boolean) -> Unit,
    onBuzzerVolumeChanged: (Int) -> Unit,
    onBuzzerTypeChanged: (BuzzerType) -> Unit,
    onBuzzerDurationChanged: (BuzzerDuration) -> Unit,
    onVibratorEnabledChanged: (Boolean) -> Unit,
    onVibratorDurationChanged: (VibratorDuration) -> Unit,
    onLedColorChanged: (LEDColor) -> Unit,
    onGoodReadLedDurationChanged: (GoodReadLedDuration) -> Unit,
    onReadModeChanged: (ReadMode) -> Unit,
    onReadTimeChanged: (ReadTime) -> Unit,
    onIlluminationModeChanged: (IlluminationMode) -> Unit,
    onPositiveNegativeModeChanged: (PositiveAndNegativeBarcodesMode) -> Unit,
    onAimingChanged: (Boolean) -> Unit,
    onTriggerRepeatChanged: (Boolean) -> Unit,
    onDeleteKeyChanged: (Boolean) -> Unit,
    onSymbologyChanged: (SymbologyType, Boolean) -> Unit,
    onCodabarModeChanged: (CodabarMode) -> Unit,
    onCodabarCheckDigitChanged: (Boolean) -> Unit,
    onSetPreambleDemo: () -> Unit,
    onClearPreamble: () -> Unit,
    onSetPrefixDemo: () -> Unit,
    onClearPrefixes: () -> Unit,
    onSetSuffixDemo: () -> Unit,
    onClearSuffixes: () -> Unit,
    onSetPostambleDemo: () -> Unit,
    onClearPostamble: () -> Unit
) {
    val isConnected = scanner.connectionState == BleDeviceConnectionState.CONNECTED
    val isBusy = scanner.connectionState == BleDeviceConnectionState.CONNECTING ||
        scanner.connectionState == BleDeviceConnectionState.DISCONNECTING

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        scanner.device.name.ifBlank { "Unnamed scanner" },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(scanner.device.deviceId, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "RSSI ${scanner.device.rssi} | ${scanner.connectionState}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.width(12.dp))

                if (isConnected) {
                    OutlinedButton(onClick = onDisconnect) { Text("Disconnect") }
                } else {
                    Button(onClick = onConnect, enabled = !isBusy) { Text("Connect") }
                }
            }

            if (isBusy) {
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator()
            }

            if (isConnected) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                DeviceDataPanel(scanner)
                Spacer(Modifier.height(12.dp))
                SettingsPanel(
                    scanner = scanner,
                    onRefreshSettings = onRefreshSettings,
                    onBuzzerEnabledChanged = onBuzzerEnabledChanged,
                    onBuzzerVolumeChanged = onBuzzerVolumeChanged,
                    onBuzzerTypeChanged = onBuzzerTypeChanged,
                    onBuzzerDurationChanged = onBuzzerDurationChanged,
                    onVibratorEnabledChanged = onVibratorEnabledChanged,
                    onVibratorDurationChanged = onVibratorDurationChanged,
                    onLedColorChanged = onLedColorChanged,
                    onGoodReadLedDurationChanged = onGoodReadLedDurationChanged,
                    onReadModeChanged = onReadModeChanged,
                    onReadTimeChanged = onReadTimeChanged,
                    onIlluminationModeChanged = onIlluminationModeChanged,
                    onPositiveNegativeModeChanged = onPositiveNegativeModeChanged,
                    onAimingChanged = onAimingChanged,
                    onTriggerRepeatChanged = onTriggerRepeatChanged,
                    onDeleteKeyChanged = onDeleteKeyChanged,
                    onSymbologyChanged = onSymbologyChanged,
                    onCodabarModeChanged = onCodabarModeChanged,
                    onCodabarCheckDigitChanged = onCodabarCheckDigitChanged,
                    onSetPreambleDemo = onSetPreambleDemo,
                    onClearPreamble = onClearPreamble,
                    onSetPrefixDemo = onSetPrefixDemo,
                    onClearPrefixes = onClearPrefixes,
                    onSetSuffixDemo = onSetSuffixDemo,
                    onClearSuffixes = onClearSuffixes,
                    onSetPostambleDemo = onSetPostambleDemo,
                    onClearPostamble = onClearPostamble
                )
            } else if (scanner.settingMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(scanner.settingMessage, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun DeviceDataPanel(scanner: ScannerUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Last data: ${scanner.barcodeData ?: "No barcode scanned yet."}")
        Text("Battery: ${scanner.batteryPercentage?.let { "$it%" } ?: "N/A"}")
        Text("Charging/USB power: ${if (scanner.isPoweredOrCharging == true) "Yes" else "No"}")
    }
}

@Composable
private fun SettingsPanel(
    scanner: ScannerUiState,
    onRefreshSettings: () -> Unit,
    onBuzzerEnabledChanged: (Boolean) -> Unit,
    onBuzzerVolumeChanged: (Int) -> Unit,
    onBuzzerTypeChanged: (BuzzerType) -> Unit,
    onBuzzerDurationChanged: (BuzzerDuration) -> Unit,
    onVibratorEnabledChanged: (Boolean) -> Unit,
    onVibratorDurationChanged: (VibratorDuration) -> Unit,
    onLedColorChanged: (LEDColor) -> Unit,
    onGoodReadLedDurationChanged: (GoodReadLedDuration) -> Unit,
    onReadModeChanged: (ReadMode) -> Unit,
    onReadTimeChanged: (ReadTime) -> Unit,
    onIlluminationModeChanged: (IlluminationMode) -> Unit,
    onPositiveNegativeModeChanged: (PositiveAndNegativeBarcodesMode) -> Unit,
    onAimingChanged: (Boolean) -> Unit,
    onTriggerRepeatChanged: (Boolean) -> Unit,
    onDeleteKeyChanged: (Boolean) -> Unit,
    onSymbologyChanged: (SymbologyType, Boolean) -> Unit,
    onCodabarModeChanged: (CodabarMode) -> Unit,
    onCodabarCheckDigitChanged: (Boolean) -> Unit,
    onSetPreambleDemo: () -> Unit,
    onClearPreamble: () -> Unit,
    onSetPrefixDemo: () -> Unit,
    onClearPrefixes: () -> Unit,
    onSetSuffixDemo: () -> Unit,
    onClearSuffixes: () -> Unit,
    onSetPostambleDemo: () -> Unit,
    onClearPostamble: () -> Unit
) {
    val settings = scanner.settings
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Scanner settings", style = MaterialTheme.typography.titleMedium)
                Text(
                    scanner.settingMessage ?: if (scanner.settingsLoaded) "Current settings loaded" else "Loading settings",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            OutlinedButton(
                onClick = onRefreshSettings,
                enabled = !scanner.settingBusy
            ) {
                Text("Refresh")
            }
        }

        if (!scanner.settingsLoaded || scanner.settingBusy) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
        }

        SettingsSection("Indicators") {
            SettingSwitch("Good-read buzzer", settings.buzzerEnabled, scanner.settingBusy, onBuzzerEnabledChanged)
            VolumeSetting(settings.buzzerVolume, scanner.settingBusy, onBuzzerVolumeChanged)
            EnumSetting("Buzzer type", settings.buzzerType, scanner.settingBusy, onBuzzerTypeChanged)
            EnumSetting("Buzzer duration", settings.buzzerDuration, scanner.settingBusy, onBuzzerDurationChanged)
            SettingSwitch("Vibrator", settings.vibratorEnabled, scanner.settingBusy, onVibratorEnabledChanged)
            EnumSetting("Vibration duration", settings.vibratorDuration, scanner.settingBusy, onVibratorDurationChanged)
            EnumSetting("Good-read LED duration", settings.goodReadLedDuration, scanner.settingBusy, onGoodReadLedDurationChanged)
            LedSwatches(settings.ledColor, scanner.settingBusy, onLedColorChanged)
        }

        SettingsSection("Read options") {
            EnumSetting("Read mode", settings.readMode, scanner.settingBusy, onReadModeChanged)
            EnumSetting("Read time", settings.readTime, scanner.settingBusy, onReadTimeChanged)
            EnumSetting("Illumination", settings.illuminationMode, scanner.settingBusy, onIlluminationModeChanged)
            EnumSetting("Barcode polarity", settings.positiveNegativeMode, scanner.settingBusy, onPositiveNegativeModeChanged)
            SettingSwitch("Aiming", settings.aimingEnabled, scanner.settingBusy, onAimingChanged)
            SettingSwitch("Trigger repeat", settings.triggerRepeatEnabled, scanner.settingBusy, onTriggerRepeatChanged)
            SettingSwitch("Delete key", settings.deleteKeyEnabled, scanner.settingBusy, onDeleteKeyChanged)
        }

        SettingsSection("Readable codes") {
            defaultSymbologySelection.forEach { type ->
                SettingSwitch(
                    label = type.label(),
                    checked = settings.symbologies[type] == true,
                    busy = scanner.settingBusy,
                    onChanged = { onSymbologyChanged(type, it) }
                )
            }
        }

        SettingsSection("Codabar") {
            EnumSetting("Mode", settings.codabarMode, scanner.settingBusy, onCodabarModeChanged)
            SettingSwitch("Check digit", settings.codabarCheckDigitEnabled, scanner.settingBusy, onCodabarCheckDigitChanged)
        }

        SettingsSection("Formatting") {
            FormattingRow(
                label = "Preamble",
                keys = settings.preamble,
                busy = scanner.settingBusy,
                onSetDemo = onSetPreambleDemo,
                onClear = onClearPreamble
            )
            FormattingRow(
                label = "Prefix all",
                keys = settings.prefixAllCodes,
                busy = scanner.settingBusy,
                onSetDemo = onSetPrefixDemo,
                onClear = onClearPrefixes
            )
            FormattingRow(
                label = "Suffix all",
                keys = settings.suffixAllCodes,
                busy = scanner.settingBusy,
                onSetDemo = onSetSuffixDemo,
                onClear = onClearSuffixes
            )
            FormattingRow(
                label = "Postamble",
                keys = settings.postamble,
                busy = scanner.settingBusy,
                onSetDemo = onSetPostambleDemo,
                onClear = onClearPostamble
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
private fun SettingSwitch(
    label: String,
    checked: Boolean,
    busy: Boolean,
    onChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            enabled = !busy,
            onCheckedChange = onChanged
        )
    }
}

@Composable
private fun FormattingRow(
    label: String,
    keys: List<DirectInputKey>,
    busy: Boolean,
    onSetDemo: () -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label)
                Text(
                    keys.directInputLabel(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onSetDemo, enabled = !busy) {
                Text("Set")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onClear, enabled = !busy && keys.isNotEmpty()) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun VolumeSetting(
    volume: Int,
    busy: Boolean,
    onApply: (Int) -> Unit
) {
    var draftVolume by remember(volume) { mutableFloatStateOf(volume.toFloat()) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Buzzer volume")
            Text("${draftVolume.toInt()}%")
        }
        Slider(
            value = draftVolume,
            onValueChange = { draftVolume = it },
            valueRange = 0f..100f,
            steps = 99,
            enabled = !busy
        )
        OutlinedButton(
            onClick = { onApply(draftVolume.toInt()) },
            enabled = !busy && draftVolume.toInt() != volume
        ) {
            Text("Apply volume")
        }
    }
}

@Composable
private inline fun <reified T : Enum<T>> EnumSetting(
    label: String,
    selected: T,
    busy: Boolean,
    noinline onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = !busy
            ) {
                Text(selected.label())
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                enumValues<T>().forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value.label()) },
                        onClick = {
                            expanded = false
                            if (value != selected) onSelected(value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LedSwatches(
    selectedColor: LEDColor,
    busy: Boolean,
    onSelected: (LEDColor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Good-read LED")
            Text(selectedColor.rgbLabel(), style = MaterialTheme.typography.bodySmall)
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ledSwatches.forEach { (name, color) ->
                val selected = color == selectedColor
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable(enabled = !busy) {
                            if (color != selectedColor) onSelected(color)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(color.red, color.green, color.blue))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(name)
                }
            }
        }
    }
}

private fun Enum<*>.label(): String {
    return name.lowercase()
        .split("_")
        .joinToString(" ") { word ->
            word.replaceFirstChar { char -> char.uppercase() }
        }
        .replace("Ms", "ms")
        .replace("Gs1", "GS1")
        .replace("Upc", "UPC")
        .replace("Ean", "EAN")
        .replace("Qr", "QR")
}

private fun LEDColor.rgbLabel(): String {
    return "#%02X%02X%02X".format(red, green, blue)
}

private fun List<DirectInputKey>.directInputLabel(): String {
    return if (isEmpty()) {
        "Empty"
    } else {
        joinToString(", ") { key -> key.label() }
    }
}

private fun CommandResponse.failureMessage(): String {
    return response.ifBlank {
        "Setting command failed; scanner did not return a response."
    }
}
