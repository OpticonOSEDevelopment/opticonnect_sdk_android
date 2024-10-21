package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.api.BluetoothManager
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
import com.opticon.opticonnect.sdk.api.ScannerSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.Symbology
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleCommandResponseReader
import com.opticon.opticonnect.sdk.internal.services.ble.interfaces.BleDataWriter
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.BleDevicesStreamsHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.OpcDataHandler
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.OpcDataHandlerFactory
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorFactory
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import com.opticon.opticonnect.sdk.internal.services.commands.CommandFactory
import com.opticon.opticonnect.sdk.internal.services.commands.CommandFeedbackService
import com.opticon.opticonnect.sdk.internal.services.commands.OpcCommandProtocolHandler
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import com.opticon.opticonnect.sdk.internal.services.core.DevicesInfoManager
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.DataWizardHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
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
        return BlePermissionsChecker(context.applicationContext)
    }

    @Provides
    @Singleton
    fun provideBleConnectivityHandler(
        bleClient: RxBleClient,
        dataHandler: DataHandler,
        commandExecutorsManager: CommandExecutorsManager
    ): BleConnectivityHandler {
        return BleConnectivityHandler(bleClient, dataHandler, commandExecutorsManager)
    }

    @Provides
    @Singleton
    fun provideBleDevicesDiscoverer(
        blePermissionsChecker: BlePermissionsChecker
    ): BleDevicesDiscoverer {
        return BleDevicesDiscoverer(blePermissionsChecker)
    }

    @Provides
    @Singleton
    fun provideDatabaseManager(): DatabaseManager {
        return DatabaseManager()
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
        commandExecutorsManager: CommandExecutorsManager,
        scannerFeedback: ScannerFeedback
    ): ScannerSettings {
        return ScannerSettings(symbology, commandExecutorsManager, scannerFeedback)
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
        return DataHandler(opcDataHandlerFactory)
    }

    @Provides
    @Singleton
    fun provideBleDataWriter(
        dataHandler: DataHandler
    ): BleDataWriter {
        return dataHandler
    }

    @Provides
    @Singleton
    fun provideBleCommandResponseReader(
        dataHandler: DataHandler
    ): BleCommandResponseReader {
        return dataHandler
    }

    @Provides
    @Singleton
    fun provideBleDevicesStreamsHandler(
        dataHandler: DataHandler
    ): BleDevicesStreamsHandler {
        return BleDevicesStreamsHandler(dataHandler)
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(
        bleDevicesDiscoverer: BleDevicesDiscoverer,
        bleConnectivityHandler: BleConnectivityHandler,
        bleDevicesStreamsHandler: BleDevicesStreamsHandler
    ): BluetoothManager {
        return BluetoothManager(
            bleDevicesDiscoverer,
            bleConnectivityHandler,
            bleDevicesStreamsHandler
        )
    }

    @Provides
    @Singleton
    fun provideCommandFeedbackService(): CommandFeedbackService {
        return CommandFeedbackService()
    }

    @Provides
    @Singleton
    fun provideCommandFactoryService(): CommandFactory {
        return CommandFactory()
    }

    @Provides
    @Singleton
    fun provideDataWizardHelper(): DataWizardHelper {
        return DataWizardHelper()
    }

    @Provides
    @Singleton
    fun provideSettingsCompressorService(
        settingsHandler: SettingsHandler,
        dataWizardHelper: DataWizardHelper
    ): SettingsCompressor {
        return SettingsCompressor(settingsHandler, dataWizardHelper)
    }

    @Provides
    @Singleton
    fun provideCommandExecutorFactory(
        bleDataWriter: BleDataWriter,
        bleCommandResponseReader: BleCommandResponseReader,
        commandBytesProvider: CommandBytesProvider,
        commandFeedbackService: CommandFeedbackService,
    ): CommandExecutorFactory {
        return CommandExecutorFactory(
            bleDataWriter,
            bleCommandResponseReader,
            commandBytesProvider,
            commandFeedbackService,
        )
    }

    @Provides
    @Singleton
    fun provideCommandExecutorsManager(
        commandExecutorFactory: CommandExecutorFactory,
        commandFactory: CommandFactory,
        settingsCompressor: SettingsCompressor
    ): CommandExecutorsManager {
        return CommandExecutorsManager(
            commandExecutorFactory,
            commandFactory,
            settingsCompressor
        )
    }

    @Provides
    @Singleton
    fun provideOpcCommandProtocolHandler(
        crc16Handler: CRC16Handler
    ): OpcCommandProtocolHandler {
        return OpcCommandProtocolHandler(crc16Handler)
    }

    @Provides
    @Singleton
    fun provideCommandBytesProvider(
        opcCommandProtocolHandler: OpcCommandProtocolHandler
    ): CommandBytesProvider {
        return opcCommandProtocolHandler
    }

    @Provides
    @Singleton
    fun provideDevicesInfoManager(
        commandExecutorsManager: CommandExecutorsManager
    ): DevicesInfoManager {
        return DevicesInfoManager(commandExecutorsManager)
    }
}

