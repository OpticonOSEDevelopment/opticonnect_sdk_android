package com.opticon.opticonnect.sdk.api

import com.opticon.opticonnect.sdk.internal.logging.OptiConnectDebugTree
import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.LifecycleHandler
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import timber.log.Timber
import java.lang.ref.WeakReference

object OptiConnect : LifecycleHandler {

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

    /**
     * Initializes the SDK with the required context.
     *
     * This function must be called by the client before accessing any SDK services.
     * It sets up the necessary context, which is used throughout the SDK for managing resources
     * and accessing system services safely.
     *
     * @param context The application or activity context to be used by the SDK.
     */
    override fun initialize(context: Context) {
        setContext(context)
    }

    /**
     * Access the scanner settings for Opticon BLE scanners.
     *
     * This property provides various configuration options, allowing you to
     * manage symbology settings, indicators, and send commands to the scanner.
     */
    val scannerSettings: ScannerSettings by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerSettings()
    }

    /**
     * Access Bluetooth management operations.
     *
     * This property enables you to perform Bluetooth operations such as
     * device discovery, connection, and disconnection, ensuring that the Bluetooth
     * lifecycle is managed properly.
     */
    val bluetoothManager: BluetoothManager by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        val manager = getComponentFromContext().bluetoothManager()
        bluetoothLifeCycleManager.initialize(getContext())
        manager
    }

    private val bluetoothLifeCycleManager: LifecycleHandler by lazy {
        getComponentFromContext().bluetoothLifecycleHandler()
    }

    /**
     * Access detailed information about connected BLE devices.
     *
     * This property allows you to retrieve information such as the MAC address,
     * serial number, local name, and firmware version of a BLE device.
     */
    val scannerInfo: ScannerInfo by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerInfo()
    }

    /**
     * Configure feedback behavior for the scanner.
     *
     * This property allows you to customize feedback settings such as LED, buzzer,
     * and vibration, controlling the scanner's responses to various commands.
     */
    val scannerFeedback: ScannerFeedback by lazy {
        val settingsHandler = getComponentFromContext().settingsHandler()
        ensureSettingsHandlerInitialized(settingsHandler)
        getComponentFromContext().scannerFeedback()
    }

    private fun getContext(): Context {
        return contextRef?.get() ?: throw IllegalStateException("Context not set. You must provide a context when accessing the SDK services.")
    }

    private fun setContext(ctx: Context) {
        contextRef = WeakReference(ctx.applicationContext)
    }

    // Retrieve the component using the weak-referenced context
    private fun getComponentFromContext(): OptiConnectComponent {
        val ctx = getContext()
        return getComponent(ctx)
    }

    /**
     * Releases all resources associated with OptiConnect.
     *
     * This function is responsible for cleaning up the Bluetooth resources
     * and any other components managed by OptiConnect. It performs the following actions:
     *
     * 1. Stops any ongoing Bluetooth discovery processes to ensure no more devices are being scanned.
     * 2. Closes all active Bluetooth connections, ensuring a safe and proper disconnection.
     * 3. Terminates any active data streams related to Bluetooth communication, ensuring resources are freed.
     * 4. Releases any other allocated resources associated with Bluetooth operations.
     *
     * This function should be called when OptiConnect is no longer needed, to avoid memory leaks
     * and ensure the system is left in a clean state.
     */
    override fun close() {
        bluetoothLifeCycleManager.close()
        Timber.d("OptiConnect resources released")
    }
}
