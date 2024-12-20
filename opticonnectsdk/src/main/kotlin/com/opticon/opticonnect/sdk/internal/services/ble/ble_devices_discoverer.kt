package com.opticon.opticonnect.sdk.internal.services.ble

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ConnectionPool
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants.OPC_SERVICE_UUID
import com.opticon.opticonnect.sdk.internal.services.ble.constants.UuidConstants.SCANNER_SERVICE_UUID
import com.polidea.rxandroidble3.RxBleClient
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
import java.util.UUID

@Singleton
internal class BleDevicesDiscoverer @Inject constructor(
    private val blePermissionsChecker: BlePermissionsChecker,
    private val connectionPool: ConnectionPool
) : Closeable {

    private val deviceDiscoveryFlow = MutableSharedFlow<BleDiscoveredDevice>(replay = 0)
    private var scanDisposable: Disposable? = null  // Store the Disposable to manage the subscription lifecycle
    private var bleClient: RxBleClient? = null
    private var isScanning = false

    fun startDiscovery(context: Context) {
        // Check Bluetooth permissions
        if (!blePermissionsChecker.hasBluetoothPermissions()) {
            throw SecurityException("Bluetooth permissions are not granted. Please request permissions.")
        } else {
            if (bleClient == null) {
                // Initialize bleClient only when needed
                bleClient = RxBleClient.create(context)
            }

            // Start BLE scan using RxAndroidBle's scanning mechanism
            scanDisposable = bleClient?.scanBleDevices(
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)  // Battery efficient scanning mode
                    .build()
            )?.doOnError { throwable ->
                // Handle or log the specific error gracefully
                if (throwable is com.polidea.rxandroidble3.exceptions.BleDisconnectedException) {
                    Timber.e("BLE Disconnected during scan: ${throwable.message}")
                    // Handle any additional recovery steps, e.g., retrying the scan if needed
                } else {
                    Timber.e(throwable, "An unexpected error occurred during BLE scanning.")
                }
            }?.subscribe(
                { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        if (hasRequiredServiceUUID(result, SCANNER_SERVICE_UUID, OPC_SERVICE_UUID)) {
                            onScanResult(result)
                        }
                    }
                },  // Handle successful scan result
                { throwable ->
                    Timber.e(throwable, "Failed to start BLE scan")
                    isScanning = false
                }  // Handle error during scanning
            )

            isScanning = true
            Timber.i("Started BLE device discovery.")
        }
    }

    private fun hasRequiredServiceUUID(result: ScanResult, vararg uuids: UUID): Boolean {
        val serviceUuids = result.scanRecord?.serviceUuids ?: return false
        return serviceUuids.any { parcelUuid -> uuids.contains(parcelUuid.uuid) }
    }

    private suspend fun onScanResult(result: ScanResult) {
        val device = result.bleDevice
        val deviceId = device.macAddress
        val poolId = getConnectionPoolId(result)

        if (connectionPool.getId(deviceId) != poolId && poolId.length == 4) {
            connectionPool.cacheId(deviceId, poolId)
        }

        val discoveredDevice = BleDiscoveredDevice(
            name = device.name ?: "",
            deviceId = deviceId,
            rssi = result.rssi,
            timeStamp = Date(System.currentTimeMillis()),
            connectionPoolId = poolId
        )

        if (isValidDeviceName(discoveredDevice.name)) {
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
        scanDisposable = null
        isScanning = false
        bleClient = null
        Timber.i("Stopped BLE device discovery.")
    }

    fun isDiscovering(): Boolean {
        return isScanning
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