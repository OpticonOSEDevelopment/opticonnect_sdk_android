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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect_sdk_example.ui.theme.Opticonnect_SDK_ExampleTheme
import kotlinx.coroutines.launch

// Holds device-specific connection and data state
data class DeviceState(
    val connectedDeviceId: String = "",
    val connectionState: BleDeviceConnectionState = BleDeviceConnectionState.DISCONNECTED,
    val barcodeData: String? = null,
    val batteryPercentage: Int? = null,
    val isCharging: Boolean? = null
)

class MainActivity : ComponentActivity() {
    private var deviceState by mutableStateOf(DeviceState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
        checkBluetoothPermissions()
    }

    // Sets up the main UI screen
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

    // Checks for required Bluetooth permissions and requests them if not granted
    private fun checkBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else listOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val toRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (toRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(toRequest.toTypedArray())
        } else initializeOptiConnectAndStartDiscovery()
    }

    // Launcher for permission requests
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) initializeOptiConnectAndStartDiscovery()
        else Toast.makeText(this, "Bluetooth permissions are required.", Toast.LENGTH_LONG).show()
    }

    // Initializes OptiConnect SDK and starts device discovery
    private fun initializeOptiConnectAndStartDiscovery() {
        OptiConnect.initialize(this).apply {
            OptiConnect.bluetoothManager.startDiscovery()
            lifecycleScope.launch {
                // Collects discovered devices and connects if disconnected
                OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { device ->
                    if (deviceState.connectionState == BleDeviceConnectionState.DISCONNECTED) {
                        deviceState = deviceState.copy(
                            connectedDeviceId = device.deviceId,
                            connectionState = BleDeviceConnectionState.CONNECTING
                        )
                        connectToDevice(device.deviceId)
                    }
                }
            }
        }
    }

    // Connects to the discovered device
    private fun connectToDevice(deviceId: String) {
        lifecycleScope.launch {
            OptiConnect.bluetoothManager.apply {
                try {
                    // Initiates the connection and listens to connection state
                    connect(deviceId)
                    startListeningToDeviceData(deviceId)

                    listenToConnectionState(deviceId).collect { state ->
                        deviceState = deviceState.copy(
                            connectionState = state,
                            connectedDeviceId = if (state == BleDeviceConnectionState.CONNECTED) deviceId else ""
                        )

                        Log.d("OptiConnect", "Device $deviceId state changed to: $state")

                        if (state == BleDeviceConnectionState.DISCONNECTED) {
                            deviceState = DeviceState() // Reset state on disconnect
                        }
                    }
                } catch (e: Exception) {
                    // Handle connection failure and reset device state
                    Toast.makeText(this@MainActivity, "Failed to connect: ${e.message}", Toast.LENGTH_SHORT).show()
                    deviceState = DeviceState()
                }
            }
        }
    }

    // Listens to data from the connected device (barcode, battery, charging status)
    private fun startListeningToDeviceData(deviceId: String) {
        lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
                deviceState = deviceState.copy(barcodeData = barcode.data)
            }
        }

        lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryPercentage(deviceId).collect { battery ->
                deviceState = deviceState.copy(batteryPercentage = battery)
            }
        }

        lifecycleScope.launch {
            OptiConnect.bluetoothManager.listenToBatteryStatus(deviceId).collect { status ->
                deviceState = deviceState.copy(isCharging = status.isCharging)
            }
        }
    }

    // Disconnects from the device and resets the state
    private fun disconnectDevice(deviceId: String) {
        lifecycleScope.launch {
            OptiConnect.bluetoothManager.disconnect(deviceId)
            deviceState = DeviceState()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OptiConnect.close()
    }
}

// UI for showing the connection status and device data
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
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            when (connectionState.connectionState) {
                BleDeviceConnectionState.CONNECTING -> {
                    Text("Connecting to device...", style = MaterialTheme.typography.headlineMedium)
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
                BleDeviceConnectionState.CONNECTED -> {
                    Text("Connected to device: ${connectionState.connectedDeviceId}", style = MaterialTheme.typography.headlineMedium)
                    Text("Barcode Data: ${connectionState.barcodeData ?: "No barcode scanned yet."}")
                    Text("Battery: ${connectionState.batteryPercentage ?: "N/A"}%")
                    Text("Charging: ${if (connectionState.isCharging == true) "Yes" else "No"}")
                    Button(onClick = { onDisconnect(connectionState.connectedDeviceId) }, modifier = Modifier.padding(top = 16.dp)) {
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
