import com.opticon.opticonnect.sdk.api.OptiConnect
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass

abstract class BaseBluetoothTest {

    companion object {
        const val TEST_DEVICE_MAC_ADDRESS = "38:89:DC:0E:00:4F"
        lateinit var context: android.content.Context

        @BeforeClass
        @JvmStatic
        fun globalSetup() {
            val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_SCAN}"
            ).close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.BLUETOOTH_CONNECT}"
            ).close()
            instrumentation.uiAutomation.executeShellCommand(
                "pm grant ${instrumentation.targetContext.packageName} ${android.Manifest.permission.ACCESS_FINE_LOCATION}"
            ).close()

            context = instrumentation.targetContext

            OptiConnect.setContext(context)
            OptiConnect.bluetoothManager.startDiscovery(context)
        }

        @AfterClass
        @JvmStatic
        fun globalTeardown() {
            OptiConnect.bluetoothManager.stopDiscovery()
        }
    }

    @After
    fun teardown() {
        OptiConnect.bluetoothManager.disconnect(TEST_DEVICE_MAC_ADDRESS)
    }

    suspend fun discoverDevice(deviceId: String): BleDiscoveredDevice? {
        val deferredDevice = CompletableDeferred<BleDiscoveredDevice?>()
        val collectionJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.bleDiscoveredDevicesFlow.collect { discoveredDevice ->
                if (discoveredDevice.deviceId == deviceId) {
                    deferredDevice.complete(discoveredDevice)
                }
            }
        }

        return try {
            withTimeoutOrNull(20000) { deferredDevice.await() }
        } finally {
            collectionJob.cancel()
        }
    }

    suspend fun connectDevice(deviceId: String, connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>): Boolean {
        val connectionStateJob = CoroutineScope(Dispatchers.IO).launch {
            OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { connectionState ->
                connectionStateFlow.emit(connectionState)
            }
        }

        return try {
            OptiConnect.bluetoothManager.connect(deviceId)
            val connectionState = withTimeoutOrNull(20000) {
                connectionStateFlow.first { it == BleDeviceConnectionState.CONNECTED }
            }
            connectionStateJob.cancel()
            connectionState == BleDeviceConnectionState.CONNECTED
        } finally {
            connectionStateJob.cancel()
        }
    }

    suspend fun assertDeviceStaysConnected(deviceId: String, connectionStateFlow: MutableStateFlow<BleDeviceConnectionState>, delayTime: Long = 5000) {
        delay(delayTime)
        val latestConnectionState = connectionStateFlow.value
        assertEquals(
            "Device with MAC address $deviceId failed to stay connected.",
            BleDeviceConnectionState.CONNECTED,
            latestConnectionState
        )
    }
}
