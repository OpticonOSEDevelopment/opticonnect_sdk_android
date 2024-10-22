package com.opticon.opticonnect.sdk.api

import OptiConnectDebugTree
import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object OptiConnect {
    // Private lateinit variables for dependencies
    private lateinit var bluetoothManagerInstance: BluetoothManager
    private lateinit var scannerSettingsInstance: ScannerSettings
    private lateinit var settingsHandler: SettingsHandler
    private var isInitialized = false

    // Public getters for clients to access the SDK services
    val scannerSettings: ScannerSettings
        get() = scannerSettingsInstance

    val bluetoothManager: BluetoothManager
        get() = bluetoothManagerInstance

    // Initialize the SDK
    suspend fun initialize(context: Context) {
        if (isInitialized) return

        // Create the Dagger component and initialize dependencies
        val component: OptiConnectComponent = DaggerOptiConnectComponent.builder()
            .context(context)
            .build()

        // Manually initialize the dependencies using the component
        bluetoothManagerInstance = component.bluetoothManager()
        scannerSettingsInstance = component.scannerSettings()
        settingsHandler = component.settingsHandler()

        if (Timber.forest().isEmpty()) {
            Timber.plant(OptiConnectDebugTree())
        }

        withContext(Dispatchers.IO) {
            Timber.d("Initializing SDK...")

            try {
                settingsHandler.initialize(context)
                Timber.i("SDK initialized successfully.")
            } catch (e: Exception) {
                Timber.e("Failed to initialize SDK: $e")
                throw e
            }
        }

        isInitialized = true
    }
}
