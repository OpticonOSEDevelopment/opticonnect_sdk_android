package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.IlluminationMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.PositiveAndNegativeBarcodesMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.ReadTime

/**
 * Interface defining methods for configuring scan options such as read modes, illumination, and barcode settings.
 */
interface ReadOptions {

    /**
     * Sets the mode for reading positive and negative barcodes.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for barcode reading, specified by [PositiveAndNegativeBarcodesMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setPositiveAndNegativeBarcodesMode(deviceId: String, mode: PositiveAndNegativeBarcodesMode): CommandResponse

    /**
     * Sets the read mode for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The mode to set for reading, specified by [ReadMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setReadMode(deviceId: String, mode: ReadMode): CommandResponse

    /**
     * Sets the read time for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param time The duration for reading, specified by [ReadTime].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setReadTime(deviceId: String, time: ReadTime): CommandResponse

    /**
     * Sets the illumination mode for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The illumination mode to set, specified by [IlluminationMode].
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setIlluminationMode(deviceId: String, mode: IlluminationMode): CommandResponse

    /**
     * Toggles the aiming feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable aiming.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAiming(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Toggles the trigger repeat feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable trigger repeat.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setTriggerRepeat(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Toggles the delete key feature for the scanner based on the [enabled] flag.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the delete key.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setDeleteKey(deviceId: String, enabled: Boolean): CommandResponse
}
