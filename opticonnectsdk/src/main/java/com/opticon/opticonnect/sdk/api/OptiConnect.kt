package com.opticon.opticonnect.sdk.api

import OptiConnectDebugTree
import android.content.Context
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.api.scanner_settings.ScannerSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object OptiConnect {
    // Private lateinit variables for dependencies
    private lateinit var bluetoothManagerInstance: BluetoothManager
    private lateinit var scannerHandler: SettingsHandler
    private lateinit var scannerSettingsInstance: ScannerSettings
    private lateinit var databaseManagerInstance: DatabaseManager
    private lateinit var databaseTablesHelperInstance: DatabaseTablesHelper
    private var isInitialized = false

    // Public getters for clients to access the SDK services
    val scannerSettings: ScannerSettings
        get() = scannerSettingsInstance

    val bluetoothManager: BluetoothManager
        get() = bluetoothManagerInstance

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
        bluetoothManagerInstance = component.bluetoothManager()
        scannerHandler = component.scannerHandler()
        scannerSettingsInstance = component.scannerSettings()
        databaseManagerInstance = component.databaseManager()
        databaseTablesHelperInstance = component.databaseTablesHelper()

        if (Timber.forest().isEmpty()) {
            Timber.plant(OptiConnectDebugTree())
        }

        withContext(Dispatchers.IO) {
            Timber.d("Initializing SDK...")

            try {
                // Call initialization on scanner settings
                scannerHandler.initialize(context)
                Timber.i("SDK initialized successfully.")
            } catch (e: Exception) {
                Timber.e("Failed to initialize SDK: $e")
                throw e
            }
        }

        isInitialized = true
    }
}
