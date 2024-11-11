package com.opticon.opticonnect_sdk_example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState

@Composable
fun DeviceListScreen(
    devices: List<BleDiscoveredDevice>,
    connectionStates: Map<String, BleDeviceConnectionState>,
    barcodeDataMap: Map<String, String>,
    batteryPercentageMap: Map<String, Int>,
    batteryStatusMap: Map<String, Boolean>,
    onDeviceClick: (BleDiscoveredDevice) -> Unit,
    onDisconnect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Discovered Devices",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(devices) { device ->
                val connectionState = connectionStates[device.deviceId]
                val barcodeData = barcodeDataMap[device.deviceId]
                val batteryPercentage = batteryPercentageMap[device.deviceId]
                val isCharging = batteryStatusMap[device.deviceId] == true
                DeviceItem(
                    device = device,
                    connectionState = connectionState,
                    barcodeData = barcodeData,
                    batteryPercentage = batteryPercentage,
                    isCharging = isCharging,
                    onClick = { onDeviceClick(device) },
                    onDisconnect = { onDisconnect(device.deviceId) }
                )
            }
        }
    }
}

@Composable
fun DeviceItem(
    device: BleDiscoveredDevice,
    connectionState: BleDeviceConnectionState?,
    barcodeData: String?,
    batteryPercentage: Int?,
    isCharging: Boolean,
    onClick: () -> Unit,
    onDisconnect: () -> Unit
) {
    // Define colors for different connection states
    val connectionColor = when (connectionState) {
        BleDeviceConnectionState.CONNECTED -> Color(0xFF4CAF50) // Green for connected
        BleDeviceConnectionState.CONNECTING -> Color(0xFFFFC107) // Amber for connecting
        BleDeviceConnectionState.DISCONNECTED -> Color(0xFFF44336) // Red for disconnected
        else -> MaterialTheme.colorScheme.onSurface // Default color for unknown state
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Device: ${device.name} (${device.deviceId})", style = MaterialTheme.typography.titleMedium)

            // Connection status text with color indication
            Text(
                text = "Connection: ${connectionState ?: "Unknown"}",
                color = connectionColor, // Apply color here
                style = MaterialTheme.typography.bodyMedium
            )

            Text(text = "Barcode Data: ${barcodeData ?: "N/A"}")

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Battery: ${batteryPercentage ?: "N/A"}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isCharging) "Charging" else "Not Charging",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (connectionState == BleDeviceConnectionState.CONNECTED) {
                Button(
                    onClick = onDisconnect,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Disconnect")
                }
            }
        }
    }
}