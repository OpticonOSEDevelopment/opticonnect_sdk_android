package com.opticon.opticonnect.sdk

import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

object OpticonnectSDK : KoinComponent {
    private val bleDevicesDiscoverer: BleDevicesDiscoverer by inject()
    private val scannerHandler: SettingsHandler by inject()

    // Asynchronous SDK initialization
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            Timber.i("Initializing SDK...")

            try {
                // Call initialization on scanner settings
                scannerHandler.initialize()

                Timber.i("SDK initialized successfully.")
            } catch (e: Exception) {
                Timber.e("Failed to initialize SDK: $e")
                throw e
            }
        }
    }

    suspend fun startDiscovery() {
        bleDevicesDiscoverer.startDiscovery()
    }
}
