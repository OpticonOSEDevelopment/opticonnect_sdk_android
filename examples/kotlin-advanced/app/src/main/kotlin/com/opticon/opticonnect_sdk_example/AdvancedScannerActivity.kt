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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect_sdk_example.ui.theme.Opticonnect_SDK_ExampleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ScannerUiState(
    val device: BleDiscoveredDevice,
    val connectionState: BleDeviceConnectionState = BleDeviceConnectionState.DISCONNECTED,
    val barcodeData: String? = null,
    val batteryPercentage: Int? = null,
    val isPoweredOrCharging: Boolean? = null,
    val buzzerEnabled: Boolean = true,
    val settingBusy: Boolean = false,
    val settingMessage: String? = null
)

class AdvancedScannerActivity : ComponentActivity() {
    private val scanners = mutableStateMapOf<String, ScannerUiState>()
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
                    scanners = scanners.values.sortedBy { it.device.name.ifBlank { it.device.deviceId } },
                    discoveryRunning = discoveryRunning,
                    onStartDiscovery = ::startDiscovery,
                    onStopDiscovery = ::stopDiscovery,
                    onConnect = ::connectToDevice,
                    onDisconnect = ::disconnectDevice,
                    onBuzzerChanged = ::setBuzzerEnabled
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
                startListeningToDeviceData(deviceId)
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

                if (state == BleDeviceConnectionState.DISCONNECTED) {
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
            }
        }
    }

    private fun startListeningToDeviceData(deviceId: String) {
        cancelDeviceDataJobs(deviceId)

        deviceDataJobs.getOrPut(deviceId) { mutableListOf() } += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
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

    private fun setBuzzerEnabled(deviceId: String, enabled: Boolean) {
        updateScanner(deviceId) {
            it.copy(settingBusy = true, settingMessage = null)
        }

        lifecycleScope.launch {
            val result = OptiConnect.scannerSettings.indicator.toggleBuzzer(deviceId, enabled)
            updateScanner(deviceId) {
                if (result.succeeded) {
                    it.copy(
                        buzzerEnabled = enabled,
                        settingBusy = false,
                        settingMessage = "Buzzer ${if (enabled) "enabled" else "disabled"}"
                    )
                } else {
                    it.copy(
                        settingBusy = false,
                        settingMessage = result.response.ifBlank { "Buzzer command failed." }
                    )
                }
            }
        }
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
        scanners[deviceId]?.let { scanners[deviceId] = update(it) }
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
    onBuzzerChanged: (String, Boolean) -> Unit
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
                    onBuzzerChanged = { onBuzzerChanged(scanner.device.deviceId, it) }
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
    onBuzzerChanged: (Boolean) -> Unit
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(scanner.device.deviceId, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "RSSI ${scanner.device.rssi} | ${scanner.connectionState}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.width(12.dp))

                if (isConnected) {
                    OutlinedButton(onClick = onDisconnect, enabled = !isBusy) { Text("Disconnect") }
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

                Text("Last data: ${scanner.barcodeData ?: "No barcode scanned yet."}")
                Text("Battery: ${scanner.batteryPercentage?.let { "$it%" } ?: "N/A"}")
                Text("Charging/USB power: ${if (scanner.isPoweredOrCharging == true) "Yes" else "No"}")

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Good-read buzzer")
                        scanner.settingMessage?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Switch(
                        checked = scanner.buzzerEnabled,
                        enabled = !scanner.settingBusy,
                        onCheckedChange = onBuzzerChanged
                    )
                }
            } else if (scanner.settingMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(scanner.settingMessage, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
