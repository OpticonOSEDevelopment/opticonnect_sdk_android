# Module opticonnect sdk

OptiConnect SDK enables seamless integration with [Opticon](https://opticon.com/)'s BLE [OPN-2500](https://opticon.com/product/opn-2500/) and [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanners. This SDK allows you to manage Bluetooth Low Energy (BLE) connections, handle scanner data streams, and programmatically control scanner settings via commands.

## Features

- Bluetooth discovery and connection management for OPN-2500 and OPN-6000 BLE scanners.
- Real-time data streaming, including barcode data reception and BLE device state monitoring.
- Programmatic control of scanner settings (e.g., scan modes, illumination, connection pooling, etc.).
- Exclusive connection management: Ensure stable device pairing in multi-device environments by assigning unique connection pool IDs, preventing previously paired devices from hijacking active connections.
- Command management and customization for BLE services and scanner configurations.

## Getting Started

### 1. Prerequisites

At least one of the following Opticon BLE barcode scanners is required:

<table style="width: 100%; text-align: center; table-layout: fixed; margin-top: 10px;">
    <tr>
        <td style="width: 50%; border: 1px solid #ddd; border-radius: 8px; padding: 10px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1); vertical-align: middle;">
            <div style="display: flex; flex-direction: column; align-items: center; height: 200px; position: relative;">
                <div style="flex-grow: 1; display: flex; align-items: center; justify-content: center;">                    
					<img src="opticonnectsdk\build\dokka\html\images\OPN-2500.png" alt="OPN-2500" style="max-width: 150px; height: auto;">
                </div>
                <div style="position: absolute; bottom: 0px; font-weight: bold;">
                    <a href="https://opticon.com/product/opn-2500/" target="_blank" style="text-decoration: none; color: inherit;">OPN-2500</a>
                </div>
            </div>
        </td>
        <td style="width: 50%; border: 1px solid #ddd; border-radius: 8px; padding: 10px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1); vertical-align: middle;">
            <div style="display: flex; flex-direction: column; align-items: center; height: 200px; position: relative;">
                <div style="flex-grow: 1; display: flex; align-items: center; justify-content: center;">                    
					<img src="opticonnectsdk\build\dokka\html\images\OPN-6000.png" alt="OPN-6000" style="max-width: 150px; height: auto;">
                </div>
                <div style="position: absolute; bottom: 0px; font-weight: bold;">
                    <a href="https://opticon.com/product/opn-6000/" target="_blank" style="text-decoration: none; color: inherit;">OPN-6000</a>
                </div>
            </div>
        </td>
    </tr>
</table>

### 2. Building the .aar library

To build the `.aar` file for the OptiConnect SDK with shadowed dependencies, follow these steps:

1. Run the shadowJar task: `./gradlew shadowJar`
2. Package the final `.aar`: `./gradlew bundleShadowedReleaseAar`

The generated `.aar` file will be located in `build/outputs/aar/`.

### 3. Adding the `.aar` library to your project

1. Download or build the `.aar` file (`opticonnectsdk.aar`) as outlined in the previous section.
2. Place the `.aar` file in your project’s `libs` directory (e.g., `app/libs/opticonnectsdk.aar`).

### 4. Updating your `build.gradle(.kts)`

Add the `.aar` file and required dependencies in your `build.gradle(.kts)` file under `dependencies`:

```kotlin
dependencies {
    // Include the .aar file
    implementation(files("libs/opticonnectsdk.aar"))

    // Core Android and Kotlin dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coroutines dependencies
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.rx3)

    // RxAndroidBLE and RxKotlin for BLE and reactive programming
    implementation(libs.rxandroidble)
    implementation(libs.rxkotlin)
}
```

### 4. Android Manifest Bluetooth Permissions

To enable Bluetooth discovery and connection on Android, add the following permissions to your AndroidManifest.xml file located at android/app/src/main/AndroidManifest.xml below the manifest entry:

```xml
<uses-feature android:name="android.hardware.bluetooth_le" android:required="false" />

<!-- New Bluetooth permissions for Android 12 or higher -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Legacy permissions for Android 11 or lower -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

<!-- Legacy permission for Android 9 or lower -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" android:maxSdkVersion="28" />
```

### 5. Example

This example demonstrates how to integrate the OptiConnect SDK to discover devices, manage Bluetooth connections, retrieve barcode data, and monitor battery status for the OPN-2500 and OPN-6000.

#### Main Components in the Example
- DeviceState: Manages the state of the connected BLE scanner.
- MainActivity: Handles permissions, device discovery, and manages connection/disconnection events.
- ConnectionStatusScreen: A simple UI to display connection status and device data.

```Kotlin
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
                OptiConnect.bluetoothManager.listenToDiscoveredDevices.collect { device ->
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
```
