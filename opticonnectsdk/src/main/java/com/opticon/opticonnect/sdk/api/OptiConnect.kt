package com.opticon.opticonnect.sdk.api

import android.content.Context
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.ScannerSettings
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object OptiConnect {
    // Private lateinit variables for dependencies
    private lateinit var bleDevicesDiscovererInstance: BleDevicesDiscoverer
    private lateinit var scannerHandler: SettingsHandler
    private lateinit var scannerSettingsInstance: ScannerSettings
    private lateinit var databaseManagerInstance: DatabaseManager
    private lateinit var databaseTablesHelperInstance: DatabaseTablesHelper
    private var isInitialized = false

    // Public getters for clients to access the SDK services
    val scannerSettings: ScannerSettings
        get() = scannerSettingsInstance

    val bleDevicesDiscoverer: BleDevicesDiscoverer
        get() = bleDevicesDiscovererInstance

    val databaseManager: DatabaseManager
        get() = databaseManagerInstance

    val databaseTablesHelper: DatabaseTablesHelper
        get() = databaseTablesHelperInstance

    // Initialize the SDK
    suspend fun initialize(context: Context) {
        if (isInitialized) return

        // Create the Dagger component and initialize dependencies
        val component: OptiConnectComponent = DaggerOptiConnectComponent.builder()
            .context(context)
            .build()

        // Manually initialize the dependencies using the component
        bleDevicesDiscovererInstance = component.bleDevicesDiscoverer()
        scannerHandler = component.scannerHandler()
        scannerSettingsInstance = component.scannerSettings()
        databaseManagerInstance = component.databaseManager()
        databaseTablesHelperInstance = component.databaseTablesHelper()

        if (Timber.forest().isEmpty()) {
            Timber.plant(Timber.DebugTree())
        }

        withContext(Dispatchers.IO) {
            Timber.d("Initializing SDK...")

            try {
                // Call initialization on scanner settings
                scannerHandler.initialize(context)
                var db = databaseManager.getDatabase(context)
                Timber.i("SDK initialized successfully.")
            } catch (e: Exception) {
                Timber.e("Failed to initialize SDK: $e")
                throw e
            }
        }

        isInitialized = true
    }
}
