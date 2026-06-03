package com.opticon.opticonnect.sdk.api

import com.opticon.opticonnect.sdk.internal.logging.OptiConnectDebugTree
import android.content.Context
import com.opticon.opticonnect.sdk.api.interfaces.BluetoothManager
import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ScannerSettings
import com.opticon.opticonnect.sdk.internal.di.OptiConnectComponent
import com.opticon.opticonnect.sdk.internal.di.DaggerOptiConnectComponent
import com.polidea.rxandroidble3.exceptions.BleDisconnectedException
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.IOException
import java.net.SocketException

object OptiConnect {

    private val componentLock = Any()
    private val debugTree = OptiConnectDebugTree()

    @Volatile
    private var component: OptiConnectComponent? = null

    @Volatile
    private var isSettingsHandlerInitialized = false

    @Volatile
    private var isBluetoothManagerInitialized = false

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var debugLoggingEnabled = false

    @Volatile
    private var rxJavaErrorHandlerInstalled = false

    /**
    * Initializes the OptiConnect SDK.
    *
    * **This method should be called once at the beginning of your application**, ideally in your Application class.
    * It sets up the necessary resources for the SDK to function correctly, including context-based components
    * that manage Bluetooth and scanner configurations.
    *
    * @param context The application context, which will be retained for the app's lifecycle to avoid memory leaks.
    */
    fun initialize(context: Context) {
        // Store application context, which is safe to keep for the app's lifetime
        synchronized(componentLock) {
            installRxJavaErrorHandlerIfNeeded()
            appContext = context.applicationContext
        }
    }

    /**
     * Enables or disables SDK debug logging.
     *
     * Logging is disabled by default so host apps do not receive verbose SDK logs unless they opt in.
     */
    fun setDebugLoggingEnabled(enabled: Boolean) {
        synchronized(componentLock) {
            debugLoggingEnabled = enabled
            val isPlanted = debugTree in Timber.forest()
            if (enabled && !isPlanted) {
                Timber.plant(debugTree)
            } else if (!enabled && isPlanted) {
                Timber.uproot(debugTree)
            }
        }
    }

    // Private method to lazily build the Dagger component
    private fun getComponent(context: Context): OptiConnectComponent {
        return component ?: synchronized(componentLock) {
            component ?: DaggerOptiConnectComponent.builder()
                .context(context.applicationContext)
                .build()
                .also { component = it }
        }
    }

    private fun requireAppContext(): Context {
        return appContext ?: throw IllegalStateException(
            "OptiConnect.initialize(context) must be called before using the SDK."
        )
    }

    private fun installRxJavaErrorHandlerIfNeeded() {
        if (rxJavaErrorHandlerInstalled || RxJavaPlugins.getErrorHandler() != null) return

        RxJavaPlugins.setErrorHandler { error ->
            val cause = if (error is UndeliverableException) error.cause else error
            when (cause) {
                is BleDisconnectedException -> {
                    Timber.d(cause, "Ignoring expected BLE disconnect after Rx stream disposal")
                }
                is SocketException, is IOException -> {
                    Timber.d(cause, "Ignoring expected I/O error after Rx stream disposal")
                }
                else -> {
                    Thread.currentThread().uncaughtExceptionHandler
                        ?.uncaughtException(Thread.currentThread(), error)
                }
            }
        }
        rxJavaErrorHandlerInstalled = true
    }

    private fun getInitializedSettingsComponent(): OptiConnectComponent = synchronized(componentLock) {
        val component = getComponentFromContext()
        val settingsHandler = component.settingsHandler()
        if (!isSettingsHandlerInitialized) {
            settingsHandler.initialize(requireAppContext())
            isSettingsHandlerInitialized = true
        }
        component
    }

    private fun getInitializedBluetoothManager(): BluetoothManager = synchronized(componentLock) {
        val component = getComponentFromContext()
        val manager = component.bluetoothManager()
        if (!isBluetoothManagerInitialized) {
            component.bluetoothLifecycleHandler().initialize(requireAppContext())
            isBluetoothManagerInitialized = true
        }
        manager
    }

    /**
     * Access the scanner settings for Opticon BLE scanners.
     *
     * This property provides various configuration options, allowing you to
     * manage symbology settings, indicators, and send commands to the scanner.
     */
    val scannerSettings: ScannerSettings
        get() {
            return getInitializedSettingsComponent().scannerSettings()
        }

    /**
     * Access Bluetooth management operations.
     *
     * This property enables you to perform Bluetooth operations such as
     * device discovery, connection, and disconnection, ensuring that the Bluetooth
     * lifecycle is managed properly.
     */
    val bluetoothManager: BluetoothManager
        get() = getInitializedBluetoothManager()

    /**
     * Access detailed information about connected BLE devices.
     *
     * This property allows you to retrieve information such as the MAC address,
     * serial number, local name, and firmware version of a BLE device.
     */
    val scannerInfo: ScannerInfo
        get() {
            return getInitializedSettingsComponent().scannerInfo()
        }

    /**
     * Configure feedback behavior for the scanner.
     *
     * This property allows you to customize feedback settings such as LED, buzzer,
     * and vibration, controlling the scanner's responses to various commands.
     */
    val scannerFeedback: ScannerFeedback
        get() {
            return getInitializedSettingsComponent().scannerFeedback()
        }

    // Retrieve the component using the weak-referenced context
    private fun getComponentFromContext(): OptiConnectComponent {
        return getComponent(requireAppContext())
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
    fun close() {
        synchronized(componentLock) {
            component?.bluetoothLifecycleHandler()?.close()
            component = null
            isSettingsHandlerInitialized = false
            isBluetoothManagerInitialized = false
            appContext = null
        }
        Timber.d("OptiConnect resources released")
    }
}
