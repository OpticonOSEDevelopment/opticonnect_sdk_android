package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.PositiveAndNegativeBarcodesMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime

/**
 * Interface defining methods for configuring scan options such as read modes, illumination, and barcode settings.
 *
 * Provides both coroutine-based suspend functions for Kotlin and callback-based methods for Java interoperability.
 */
interface ReadOptions {

    /**
     * Sets the mode for reading positive and negative barcodes.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for barcode reading, specified by [PositiveAndNegativeBarcodesMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPositiveAndNegativeBarcodesMode(
        deviceId: String,
        mode: PositiveAndNegativeBarcodesMode
    ): CommandResponse

    /**
     * Callback-based version of [setPositiveAndNegativeBarcodesMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for barcode reading, specified by [PositiveAndNegativeBarcodesMode].
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setPositiveAndNegativeBarcodesMode(
        deviceId: String,
        mode: PositiveAndNegativeBarcodesMode,
        callback: Callback<CommandResponse>
    )

    /**
     * Gets the currently known positive/negative barcode mode from the runtime settings state.
     *
     * @param deviceId The identifier of the target device.
     * @return The currently known [PositiveAndNegativeBarcodesMode], or the default when no override is stored.
     */
    fun getPositiveAndNegativeBarcodesMode(deviceId: String): PositiveAndNegativeBarcodesMode

    /**
     * Sets the read mode for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for reading, specified by [ReadMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setReadMode(deviceId: String, mode: ReadMode): CommandResponse

    /**
     * Callback-based version of [setReadMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for reading, specified by [ReadMode].
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setReadMode(
        deviceId: String,
        mode: ReadMode,
        callback: Callback<CommandResponse>
    )

    /**
     * Gets the currently known read mode from the runtime settings state.
     *
     * @param deviceId The identifier of the target device.
     * @return The currently known [ReadMode], or the default when no override is stored.
     */
    fun getReadMode(deviceId: String): ReadMode

    /**
     * Sets the read time for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param time The duration for reading, specified by [ReadTime].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setReadTime(deviceId: String, time: ReadTime): CommandResponse

    /**
     * Callback-based version of [setReadTime] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param time The duration for reading, specified by [ReadTime].
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setReadTime(
        deviceId: String,
        time: ReadTime,
        callback: Callback<CommandResponse>
    )

    /**
     * Gets the currently known read time from the runtime settings state.
     *
     * @param deviceId The identifier of the target device.
     * @return The currently known [ReadTime], or the default when no override is stored.
     */
    fun getReadTime(deviceId: String): ReadTime

    /**
     * Sets the illumination mode for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The illumination mode to set, specified by [IlluminationMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setIlluminationMode(deviceId: String, mode: IlluminationMode): CommandResponse

    /**
     * Callback-based version of [setIlluminationMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The illumination mode to set, specified by [IlluminationMode].
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setIlluminationMode(
        deviceId: String,
        mode: IlluminationMode,
        callback: Callback<CommandResponse>
    )

    /**
     * Gets the currently known illumination mode from the runtime settings state.
     *
     * @param deviceId The identifier of the target device.
     * @return The currently known [IlluminationMode], or the default when no override is stored.
     */
    fun getIlluminationMode(deviceId: String): IlluminationMode

    /**
     * Toggles the aiming feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable aiming.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAiming(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setAiming] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable aiming.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setAiming(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    )

    /**
     * Returns whether aiming is currently known to be enabled.
     *
     * @param deviceId The identifier of the target device.
     * @return True when aiming is enabled, or the default when no override is stored.
     */
    fun isAimingEnabled(deviceId: String): Boolean

    /**
     * Toggles the trigger repeat feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable trigger repeat.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTriggerRepeat(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setTriggerRepeat] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable trigger repeat.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setTriggerRepeat(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    )

    /**
     * Returns whether trigger repeat is currently known to be enabled.
     *
     * @param deviceId The identifier of the target device.
     * @return True when trigger repeat is enabled, or the default when no override is stored.
     */
    fun isTriggerRepeatEnabled(deviceId: String): Boolean

    /**
     * Toggles the delete key feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the delete key.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setDeleteKey(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setDeleteKey] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the delete key.
     * @param callback Callback to receive the [CommandResponse].
     */
    fun setDeleteKey(
        deviceId: String,
        enabled: Boolean,
        callback: Callback<CommandResponse>
    )

    /**
     * Returns whether the delete key is currently known to be enabled.
     *
     * @param deviceId The identifier of the target device.
     * @return True when the delete key is enabled, or the default when no override is stored.
     */
    fun isDeleteKeyEnabled(deviceId: String): Boolean
}
