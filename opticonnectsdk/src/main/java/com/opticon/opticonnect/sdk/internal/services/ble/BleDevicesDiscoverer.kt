package com.opticon.opticonnect.sdk.internal.services.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class BleDevicesDiscoverer(private val context: Context, private val blePermissionsChecker: BlePermissionsChecker) : KoinComponent {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    suspend fun startDiscovery() {
        if (!blePermissionsChecker.hasBluetoothPermissions()) {
            throw SecurityException("Bluetooth permissions are not granted. Please request permissions.")
        } else {
            // Proceed with BLE discovery logic
        }
    }
}