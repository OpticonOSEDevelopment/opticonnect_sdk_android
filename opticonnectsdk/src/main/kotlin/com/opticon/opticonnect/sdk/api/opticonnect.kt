package com.opticon.opticonnect.sdk.api

import OptiConnectDebugTree
import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import timber.log.Timber
import java.lang.ref.WeakReference

object OptiConnect {

    private var component: OptiConnectComponent? = null
    private var isSettingsHandlerInitialized = false

    // Hold context as a WeakReference to avoid memory leaks
    private var contextRef: WeakReference<Context>? = null

    // Private method to lazily build the Dagger component
    private fun getComponent(context: Context): OptiConnectComponent {
        if (component == null) {
            component = DaggerOptiConnectComponent.builder()
                .context(context.applicationContext)  // Use application context to avoid memory leaks
                .build()

            if (Timber.forest().isEmpty()) {
                Timber.plant(OptiConnectDebugTree())
            }

            Timber.d("OptiConnect component initialized")
        }
        return component!!
    }

    // Function to ensure settingsHandler is initialized
    private fun ensureSettingsHandlerInitialized(settingsHandler: SettingsHandler) {
        if (!isSettingsHandlerInitialized) {
            settingsHandler.initialize(getContext())
            isSettingsHandlerInitialized = true
            Timber.i("Initialized SettingsHandler")
        }
    }

    // Public getters for clients to access the SDK services, lazily initialized via Dagger
    val scannerSettings: ScannerSettings by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerSettings()
    }

    val bluetoothManager: BluetoothManager by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        val manager = getComponentFromContext().bluetoothManager()
        manager.initialize(getContext())
        manager
    }

    val scannerInfo: ScannerInfo by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerInfo()
    }

    val scannerFeedback: ScannerFeedback by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerFeedback()
    }

    private fun getContext(): Context {
        return contextRef?.get() ?: throw IllegalStateException("Context not set. You must provide a context when accessing the SDK services.")
    }

    // Set the context, store it as a weak reference to avoid memory leaks
    fun setContext(ctx: Context) {
        contextRef = WeakReference(ctx.applicationContext)
    }

    fun withContext(ctx: Context): OptiConnect {
        setContext(ctx)
        return this
    }

    // Retrieve the component using the weak-referenced context
    private fun getComponentFromContext(): OptiConnectComponent {
        val ctx = getContext()
        return getComponent(ctx)
    }

    fun close() {
        bluetoothManager.close()
        Timber.d("OptiConnect resources released")
    }
}
