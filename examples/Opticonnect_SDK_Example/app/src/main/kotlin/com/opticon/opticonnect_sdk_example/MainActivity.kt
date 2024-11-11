package com.opticon.opticonnect_sdk_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import com.opticon.opticonnect_sdk_example.ui.theme.Opticonnect_SDK_ExampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val discoveredDevices = mutableStateListOf<BleDiscoveredDevice>()
    private var selectedDeviceId: String? = null

    private val connectionStates = mutableStateMapOf<String, BleDeviceConnectionState>()
    private val barcodeDataMap = mutableStateMapOf<String, String>()
    private val batteryPercentageMap = mutableStateMapOf<String, Int>()
    private val batteryStatusMap = mutableStateMapOf<String, Boolean>() // true for charging, false otherwise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set up the UI
        setContent {
            Opticonnect_SDK_ExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DeviceListScreen(
                        devices = discoveredDevices,
                        connectionStates = connectionStates,
                        barcodeDataMap = barcodeDataMap,
                        batteryPercentageMap = batteryPercentageMap,
                        batteryStatusMap = batteryStatusMap,
                        onDeviceClick = { device ->
                            connectToDevice(device.deviceId)
                        },
                        modifier = Modifier.padding(innerPadding),
                        onDisconnect = { deviceId ->
                            disconnectDevice(deviceId)
                        }
                    )
                }
            }
        }

        // Check and request Bluetooth permissions if necessary
        checkBluetoothPermissions()
    }

    private fun checkBluetoothPermissions() {
        val requiredPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
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
            showPermissionDeniedMessage()
        }
    }

    private fun initializeOptiConnectAndStartDiscovery() {
        OptiConnect.initialize(this)
        OptiConnect.bluetoothManager.startDiscovery()

        lifecycleScope.launch {
            OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { device ->
                val index = discoveredDevices.indexOfFirst { it.deviceId == device.deviceId }
                if (index != -1) {
                    discoveredDevices[index] = device
                } else {
                    discoveredDevices.add(device)
                }
            }
        }
    }

    private fun connectToDevice(deviceId: String) {
        selectedDeviceId = deviceId
        lifecycleScope.launch {
            launch {
                OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { connectionState ->
                    connectionStates[deviceId] = connectionState
                    if (connectionState == BleDeviceConnectionState.DISCONNECTED) {
                        barcodeDataMap.remove(deviceId)
                        batteryPercentageMap.remove(deviceId)
                        batteryStatusMap.remove(deviceId)
                    }
                }
            }

            launch {
                OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
                    barcodeDataMap[deviceId] = barcode.data
                }
            }

            launch {
                OptiConnect.bluetoothManager.listenToBatteryPercentage(deviceId).collect { batteryPercentage ->
                    batteryPercentageMap[deviceId] = batteryPercentage
                }
            }

            launch {
                OptiConnect.bluetoothManager.listenToBatteryStatus(deviceId).collect { batteryStatus ->
                    batteryStatusMap[deviceId] = batteryStatus.isCharging
                }
            }

            OptiConnect.bluetoothManager.connect(deviceId)
        }
    }

    private fun disconnectDevice(deviceId: String) {
        lifecycleScope.launch {
            OptiConnect.bluetoothManager.disconnect(deviceId)
            connectionStates[deviceId] = BleDeviceConnectionState.DISCONNECTED
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Bluetooth permissions are required for this feature.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        OptiConnect.close()
    }
}
