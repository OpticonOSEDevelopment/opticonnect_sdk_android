package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import android.os.ParcelUuid
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants.OPC_SERVICE_UUID
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanResult
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Closeable
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleDevicesDiscoverer @Inject constructor(
    private val blePermissionsChecker: BlePermissionsChecker
) : Closeable {

    private val deviceDiscoveryFlow = MutableSharedFlow<BleDiscoveredDevice>(replay = 0)
    private var scanDisposable: Disposable? = null  // Store the Disposable to manage the subscription lifecycle
    private var bleClient: RxBleClient? = null

    fun startDiscovery(context: Context) {
        // Check Bluetooth permissions
        if (!blePermissionsChecker.hasBluetoothPermissions()) {
            throw SecurityException("Bluetooth permissions are not granted. Please request permissions.")
        } else {
            if (bleClient == null) {
                // Initialize bleClient only when needed
                bleClient = RxBleClient.create(context)
            }

            val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(OPC_SERVICE_UUID)) // Add the OPC Service UUID to the filter supported scanners
                .build()
            // Start BLE scan using RxAndroidBle's scanning mechanism
            scanDisposable = bleClient?.scanBleDevices(
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)  // Battery efficient scanning mode
                    .build(),
            )?.subscribe(
                { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        onScanResult(result)
                    }
                },  // Handle successful scan result
                { throwable -> Timber.e(throwable, "Failed to start BLE scan") }  // Handle error during scanning
            )
            Timber.i("Started BLE device discovery.")
        }
    }

    private suspend fun onScanResult(result: ScanResult) {
        val device = result.bleDevice

        val discoveredDevice = BleDiscoveredDevice(
            name = device.name ?: "",
            deviceId = device.macAddress,
            rssi = result.rssi,
            timeStamp = Date(System.currentTimeMillis()),
            connectionPoolId = getConnectionPoolId(result)
        )

        if (isValidDeviceName(discoveredDevice.name)) {
            Timber.i("Discovered device: ${discoveredDevice.name}, ${discoveredDevice.deviceId}")
            try {
                deviceDiscoveryFlow.emit(discoveredDevice)
            }
            catch (e: Exception) {
                Timber.e(e, "Failed to emit discovered device")
            }
        }
    }

    private fun getConnectionPoolId(result: ScanResult): String {
        // Extract connection pool ID from manufacturer data
        val manufacturerData = result.scanRecord?.manufacturerSpecificData ?: return ""
        if (manufacturerData.size() > 0) {
            val firstKey = manufacturerData.keyAt(0)
            val dataList = manufacturerData[firstKey]
            if (dataList.size >= 2) {
                return String.format("%02x%02x", dataList[0], dataList[1])
            }
        }
        return ""
    }

    fun stopDiscovery() {
        scanDisposable?.dispose()  // Dispose the scan when stopping the discovery

        bleClient = null
        Timber.i("Stopped BLE device discovery.")
    }

    fun getDeviceDiscoveryFlow(): Flow<BleDiscoveredDevice> {
        return deviceDiscoveryFlow.asSharedFlow()
    }

    private fun isValidDeviceName(deviceName: String): Boolean {
        return deviceName.trim().isNotEmpty()
    }

    override fun close() {
        stopDiscovery()  // Ensure BLE discovery is stopped when closing
    }
}