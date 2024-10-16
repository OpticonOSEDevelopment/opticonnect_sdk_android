package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.internal.services.ble.BleDevicesDiscoverer
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.ScannerSettings
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OptiConnectModule::class])
interface OptiConnectComponent {
    fun bleDevicesDiscoverer(): BleDevicesDiscoverer
    fun scannerHandler(): SettingsHandler
    fun scannerSettings(): ScannerSettings

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): OptiConnectComponent
    }
}
