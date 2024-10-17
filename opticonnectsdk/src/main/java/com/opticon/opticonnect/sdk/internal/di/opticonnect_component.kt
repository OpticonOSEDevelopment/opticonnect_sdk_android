package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.api.BluetoothManager
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.ScannerSettings
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OptiConnectModule::class])
interface OptiConnectComponent {
    fun bluetoothManager(): BluetoothManager
    fun scannerHandler(): SettingsHandler
    fun scannerSettings(): ScannerSettings
    fun databaseManager(): DatabaseManager
    fun databaseTablesHelper(): DatabaseTablesHelper

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): OptiConnectComponent
    }
}
