package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.internal.services.ble.BleConnectivityHandler
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.ble.BlePermissionsChecker
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.CRC16Handler
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import com.opticon.opticonnect.sdk.api.ScannerFeedback
import com.polidea.rxandroidble3.RxBleClient
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.ScannerSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.Symbology
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.OpcDataHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.OpcDataHandlerFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object OptiConnectModule {

    @Provides
    @Singleton
    fun provideScannerFeedback(): ScannerFeedback {
        return ScannerFeedback()
    }

    @Provides
    @Singleton
    fun provideRxBleClient(context: Context): RxBleClient {
        return RxBleClient.create(context)
    }

    @Provides
    @Singleton
    fun provideBlePermissionsChecker(context: Context): BlePermissionsChecker {
        return BlePermissionsChecker(context)
    }



    @Provides
    @Singleton
    fun provideBleConnectivityHandler(
        bleClient: RxBleClient,
        dataHandler: DataHandler
    ): BleConnectivityHandler {
        return BleConnectivityHandler(bleClient, dataHandler)
    }

    @Provides
    @Singleton
    fun provideBleDevicesDiscoverer(
        blePermissionsChecker: BlePermissionsChecker,
        context: Context
    ): BleDevicesDiscoverer {
        return BleDevicesDiscoverer(context, blePermissionsChecker)
    }

    @Provides
    @Singleton
    fun provideDatabaseManager(context: Context): DatabaseManager {
        return DatabaseManager(context)
    }

    @Provides
    @Singleton
    fun provideDatabaseTablesHelper(): DatabaseTablesHelper {
        return DatabaseTablesHelper()
    }

    @Provides
    @Singleton
    fun provideSettingsHandler(
        databaseTablesHelper: DatabaseTablesHelper,
        databaseManager: DatabaseManager
    ): SettingsHandler {
        return SettingsHandler(databaseTablesHelper, databaseManager)
    }

    @Provides
    @Singleton
    fun provideSymbology(
        scannerFeedback: ScannerFeedback
    ): Symbology {
        return Symbology(scannerFeedback)
    }

    @Provides
    @Singleton
    fun provideScannerSettings(
        symbology: Symbology,
        scannerFeedback: ScannerFeedback
    ): ScannerSettings {
        return ScannerSettings(symbology, scannerFeedback)
    }

    @Provides
    @Singleton
    fun provideCRC16Handler(): CRC16Handler {
        return CRC16Handler() // Assuming it has a no-arg constructor
    }

    @Provides
    @Singleton
    fun provideSymbologyHandler(): SymbologyHandler {
        return SymbologyHandler() // Assuming it has a no-arg constructor
    }

    @Provides
    fun provideOpcDataHandler(
        deviceId: String, // Pass deviceId at runtime
        crc16Handler: CRC16Handler,
        symbologyHandler: SymbologyHandler
    ): OpcDataHandler {
        return OpcDataHandler(deviceId, crc16Handler, symbologyHandler)
    }

    @Provides
    fun provideOpcDataHandlerFactory(
        crc16Handler: CRC16Handler,
        symbologyHandler: SymbologyHandler
    ): OpcDataHandlerFactory {
        return OpcDataHandlerFactory(crc16Handler, symbologyHandler)
    }

    @Provides
    @Singleton
    fun provideDataHandler(opcDataHandlerFactory: OpcDataHandlerFactory): DataHandler {
        // Assume DataHandler has a default constructor or no dependencies
        return DataHandler(opcDataHandlerFactory)
    }
}

