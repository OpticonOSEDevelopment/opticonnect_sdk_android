package com.opticon.opticonnect.sdk.internal.di

import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.internal.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.internal.services.ble.BluetoothManagerImpl
import dagger.Binds
import dagger.Module

@Module
internal interface BluetoothManagerBindings {

    @Binds
    fun bindBluetoothManager(bluetoothManagerImpl: BluetoothManagerImpl): BluetoothManager

    @Binds
    fun bindLifecycleHandler(bluetoothManagerImpl: BluetoothManagerImpl): LifecycleHandler
}