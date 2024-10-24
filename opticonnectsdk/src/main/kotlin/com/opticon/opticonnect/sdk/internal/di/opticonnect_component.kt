package com.opticon.opticonnect.sdk.internal.di

import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OptiConnectModule::class])
internal interface OptiConnectComponent {
    fun bluetoothManager(): BluetoothManager
    fun bluetoothLifecycleHandler(): LifecycleHandler
    fun scannerSettings(): ScannerSettings
    fun settingsHandler(): SettingsHandler
    fun scannerInfo(): ScannerInfo
    fun scannerFeedback(): ScannerFeedback

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): OptiConnectComponent
    }
}
