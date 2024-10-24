package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.internal.interfaces.DirectInputKeysHelper
import com.opticon.opticonnect.sdk.internal.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.internal.services.ble.BleConnectivityHandler
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.ble.BlePermissionsChecker
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.CRC16Handler
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ConnectionPool
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Formatting
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Indicator
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ReadOptions
import com.polidea.rxandroidble3.RxBleClient
import com.opticon.opticonnect.sdk.internal.services.ble.streams.data.DataHandler
import com.opticon.opticonnect.sdk.internal.services.core.SymbologyHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.Symbology
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Codabar
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code11
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code128AndGS1128
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code2Of5AndSCode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code39
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Code93
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CodeSpecific
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.CompositeCodes
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN13
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.EAN8
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.GS1Databar
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.IATA
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.KoreanPostalAuthority
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.MSIPlessey
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Telepen
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UKPlessey
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCA
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.UPCE1
import com.opticon.opticonnect.sdk.internal.scanner_settings.ConnectionPoolImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.IndicatorImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.ScannerSettingsImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.SymbologyImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.CodabarImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.Code11Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.Code128AndGS1128Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.Code2Of5AndSCodeImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.Code39Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.Code93Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.CodeSpecificImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.CompositeCodesImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.EAN13Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.EAN8Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.GS1DatabarImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.IATAImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.KoreanPostalAuthorityImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.MSIPlesseyImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.TelepenImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.UKPlesseyImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.UPCAImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.UPCE1Impl
import com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific.UPCEImpl
import com.opticon.opticonnect.sdk.internal.services.ble.BluetoothManagerImpl
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
import com.opticon.opticonnect.sdk.internal.services.commands.ScannerFeedbackImpl
import com.opticon.opticonnect.sdk.internal.services.commands.interfaces.CommandBytesProvider
import com.opticon.opticonnect.sdk.internal.services.core.DevicesInfoManager
import com.opticon.opticonnect.sdk.internal.services.core.DirectInputKeysHelperImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.FormattingImpl
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.DataWizardHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsCompressor
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandlerImpl
import com.opticon.opticonnect.sdk.internal.scanner_settings.ReadOptionsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object OptiConnectModule {

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
        commandExecutorsManager: CommandExecutorsManager,
        devicesInfoManager: DevicesInfoManager
    ): BleConnectivityHandler {
        return BleConnectivityHandler(bleClient, dataHandler, commandExecutorsManager, devicesInfoManager)
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
        return SettingsHandlerImpl(databaseTablesHelper, databaseManager)
    }

    @Provides
    @Singleton
    fun provideScannerFeedback(): ScannerFeedback {
        return ScannerFeedbackImpl()
    }

    @Provides
    @Singleton
    fun provideSymbology(): Symbology {
        return SymbologyImpl()
    }

    @Provides
    @Singleton
    fun provideIndicator(): Indicator {
        return IndicatorImpl()
    }

    @Provides
    @Singleton
    fun provideCodabar() : Codabar {
        return CodabarImpl()
    }

    @Provides
    @Singleton
    fun provideCode11(): Code11 {
        return Code11Impl()
    }

    @Provides
    @Singleton
    fun provideCode128AndGS1128(): Code128AndGS1128 {
        return Code128AndGS1128Impl()
    }

    @Provides
    @Singleton
    fun provideCode2Of5AndSCode(): Code2Of5AndSCode {
        return Code2Of5AndSCodeImpl()
    }

    @Provides
    @Singleton
    fun provideCode39(): Code39 {
        return Code39Impl()
    }

    @Provides
    @Singleton
    fun provideCode93(): Code93 {
        return Code93Impl()
    }

    @Provides
    @Singleton
    fun provideCompositeCodes(): CompositeCodes {
        return CompositeCodesImpl()
    }

    @Provides
    @Singleton
    fun provideEAN8(): EAN8 {
        return EAN8Impl()
    }

    @Provides
    @Singleton
    fun provideEAN13(): EAN13 {
        return EAN13Impl()
    }

    @Provides
    @Singleton
    fun provideGS1Databar(): GS1Databar {
        return GS1DatabarImpl()
    }

    @Provides
    @Singleton
    fun provideIATA(): IATA {
        return IATAImpl()
    }

    @Provides
    @Singleton
    fun provideKoreanPostalAuthority(): KoreanPostalAuthority {
        return KoreanPostalAuthorityImpl()
    }

    @Provides
    @Singleton
    fun provideMSIPlessey(): MSIPlessey {
        return MSIPlesseyImpl()
    }

    @Provides
    @Singleton
    fun provideTelepen(): Telepen {
        return TelepenImpl()
    }

    @Provides
    @Singleton
    fun provideUKPlessey(): UKPlessey {
        return UKPlesseyImpl()
    }

    @Provides
    @Singleton
    fun provideUPCA(): UPCA {
        return UPCAImpl()
    }

    @Provides
    @Singleton
    fun provideUPCE(): UPCE {
        return UPCEImpl()
    }

    @Provides
    @Singleton
    fun provideUPCE1(): UPCE1 {
        return UPCE1Impl()
    }

    @Provides
    @Singleton
    fun provideCodeSpecific(
        codabar: Codabar,
        code2of5AndSCode: Code2Of5AndSCode,
        code11: Code11,
        code39: Code39,
        code93: Code93,
        code128AndGS1128: Code128AndGS1128,
        compositeCodes: CompositeCodes,
        ean8: EAN8,
        ean13: EAN13,
        gs1Databar: GS1Databar,
        iata: IATA,
        koreanPostalAuthority: KoreanPostalAuthority,
        msiPlessey: MSIPlessey,
        telepen: Telepen,
        ukPlessey: UKPlessey,
        upcA: UPCA,
        upcE: UPCE,
        upcE1: UPCE1
    ): CodeSpecific {
        return CodeSpecificImpl(
            codabar,
            code2of5AndSCode,
            code11,
            code39,
            code93,
            code128AndGS1128,
            compositeCodes,
            ean8,
            ean13,
            gs1Databar,
            iata,
            koreanPostalAuthority,
            msiPlessey,
            telepen,
            ukPlessey,
            upcA,
            upcE,
            upcE1
        )
    }

    @Provides
    @Singleton
    fun provideScannerSettings(
                symbology: Symbology,
                codeSpecific: CodeSpecific,
                readOptions: ReadOptions,
                indicator: Indicator,
                formatting: Formatting,
                connectionPool: ConnectionPool,
                commandExecutorsManager: CommandExecutorsManager,
                settingsCompressor: SettingsCompressor,
    ): ScannerSettings {
        return ScannerSettingsImpl(symbology, codeSpecific, readOptions, indicator, formatting, connectionPool,
            commandExecutorsManager, settingsCompressor)
    }

    @Provides
    @Singleton
    fun provideCRC16Handler(): CRC16Handler {
        return CRC16Handler()
    }

    @Provides
    @Singleton
    fun provideSymbologyHandler(): SymbologyHandler {
        return SymbologyHandler()
    }

    @Provides
    fun provideOpcDataHandler(
        deviceId: String,
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
        return BluetoothManagerImpl(
            bleDevicesDiscoverer,
            bleConnectivityHandler,
            bleDevicesStreamsHandler
        )
    }

    @Provides
    @Singleton
    fun provideBluetoothLifecycleHandler(
        bleDevicesDiscoverer: BleDevicesDiscoverer,
        bleConnectivityHandler: BleConnectivityHandler,
        bleDevicesStreamsHandler: BleDevicesStreamsHandler
    ): LifecycleHandler {
        return BluetoothManagerImpl(
            bleDevicesDiscoverer,
            bleConnectivityHandler,
            bleDevicesStreamsHandler
        )
    }

    @Provides
    @Singleton
    fun provideCommandFeedbackService(
        scannerFeedback: ScannerFeedback
    ): CommandFeedbackService {
        return CommandFeedbackService(scannerFeedback)
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

    @Provides
    @Singleton
    fun provideScannerInfo(
        devicesInfoManager: DevicesInfoManager
    ): ScannerInfo {
        return devicesInfoManager
    }

    @Provides
    @Singleton
    fun provideDirectInputKeyHelper(): DirectInputKeysHelper {
        return DirectInputKeysHelperImpl()
    }

    @Provides
    @Singleton
    fun provideFormatting(directInputKeysHelper: DirectInputKeysHelper): Formatting {
        return FormattingImpl(directInputKeysHelper)
    }

    @Provides
    @Singleton
    fun provideReadOptions(): ReadOptions {
        return ReadOptionsImpl()
    }

    @Provides
    @Singleton
    fun provideConnectionPool(directInputKeysHelper: DirectInputKeysHelper): ConnectionPool {
        return ConnectionPoolImpl(directInputKeysHelper)
    }
}
