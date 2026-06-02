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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect_sdk_example.ui.theme.Opticonnect_SDK_ExampleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class DeviceState(
    val connectedDeviceId: String = "",
    val connectionState: BleDeviceConnectionState = BleDeviceConnectionState.DISCONNECTED,
    val barcodeData: String? = null,
    val batteryPercentage: Int? = null,
    val isCharging: Boolean? = null
)

class MainActivity : ComponentActivity() {
    private var deviceState by mutableStateOf(DeviceState())
    private var userRequestedDisconnect by mutableStateOf(false)
    private var discoveryJob: Job? = null
    private var connectionStateJob: Job? = null
    private val deviceDataJobs = mutableListOf<Job>()

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
                ConnectionStatusScreen(deviceState) { disconnectDevice(it) }
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
        userRequestedDisconnect = false
        OptiConnect.bluetoothManager.startDiscovery()

        discoveryJob?.cancel()
        discoveryJob = lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { device ->
                if (!userRequestedDisconnect && deviceState.connectionState == BleDeviceConnectionState.DISCONNECTED) {
                    deviceState = deviceState.copy(
                        connectedDeviceId = device.deviceId,
                        connectionState = BleDeviceConnectionState.CONNECTING
                    )
                    connectToDevice(device.deviceId)
                }
            }
        }
    }

    private fun connectToDevice(deviceId: String) {
        lifecycleScope.launch {
            try {
                OptiConnect.bluetoothManager.connect(deviceId)
                startListeningToConnectionState(deviceId)
                startListeningToDeviceData(deviceId)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to connect: ${e.message}", Toast.LENGTH_SHORT).show()
                deviceState = DeviceState()
            }
        }
    }

    private fun startListeningToConnectionState(deviceId: String) {
        connectionStateJob?.cancel()
        connectionStateJob = lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { state ->
                deviceState = deviceState.copy(
                    connectionState = state,
                    connectedDeviceId = if (state == BleDeviceConnectionState.CONNECTED) deviceId else ""
                )

                Log.d("OptiConnect", "Device $deviceId state changed to: $state")

                if (state == BleDeviceConnectionState.DISCONNECTED) {
                    cancelDeviceListeners()
                    deviceState = DeviceState()
                }
            }
        }
    }

    private fun startListeningToDeviceData(deviceId: String) {
        cancelDeviceDataJobs()

        deviceDataJobs += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
                deviceState = deviceState.copy(barcodeData = barcode.data)
            }
        }

        deviceDataJobs += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryPercentage(deviceId).collect { battery ->
                deviceState = deviceState.copy(batteryPercentage = battery)
            }
        }

        deviceDataJobs += lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryStatus(deviceId).collect { status ->
                val isPoweredOrCharging = status.isCharging || status.isWiredCharging || status.isWirelessCharging
                Log.d("OptiConnect", "Battery status: $status")
                deviceState = deviceState.copy(isCharging = isPoweredOrCharging)
            }
        }
    }

    private fun cancelDeviceDataJobs() {
        deviceDataJobs.forEach { it.cancel() }
        deviceDataJobs.clear()
    }

    private fun cancelDeviceListeners() {
        connectionStateJob?.cancel()
        connectionStateJob = null
        cancelDeviceDataJobs()
    }

    private fun disconnectDevice(deviceId: String) {
        lifecycleScope.launch {
            userRequestedDisconnect = true
            discoveryJob?.cancel()
            discoveryJob = null
            cancelDeviceListeners()
            OptiConnect.bluetoothManager.stopDiscovery()
            OptiConnect.bluetoothManager.disconnect(deviceId)
            deviceState = DeviceState()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        discoveryJob?.cancel()
        cancelDeviceListeners()
        OptiConnect.close()
    }
}

@Composable
fun ConnectionStatusScreen(
    connectionState: DeviceState,
    onDisconnect: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            when (connectionState.connectionState) {
                BleDeviceConnectionState.CONNECTING -> {
                    Text("Connecting to device...", style = MaterialTheme.typography.headlineMedium)
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }

                BleDeviceConnectionState.CONNECTED -> {
                    Text(
                        "Connected to device: ${connectionState.connectedDeviceId}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text("Barcode Data: ${connectionState.barcodeData ?: "No barcode scanned yet."}")
                    Text("Battery: ${connectionState.batteryPercentage ?: "N/A"}%")
                    Text("Charging/USB power: ${if (connectionState.isCharging == true) "Yes" else "No"}")
                    Button(
                        onClick = { onDisconnect(connectionState.connectedDeviceId) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Disconnect")
                    }
                }

                BleDeviceConnectionState.DISCONNECTED -> {
                    Text("Searching for devices...", style = MaterialTheme.typography.headlineMedium)
                }

                BleDeviceConnectionState.DISCONNECTING -> {
                    Text("Disconnecting from device...", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
