package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCELeadingZeroAndTransmitCDMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.UPCEConversionMode

/**
 * Interface representing settings for UPCE symbology.
 *
 * This interface provides methods to configure leading zero, check digit transmission settings,
 * conversion modes, and add-on support for UPCE symbology.
 */
interface UPCE {

    /**
     * Sets the leading zero and check digit transmission mode for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired leading zero and check digit transmission mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode
    ): CommandResponse

    /**
     * Callback-based version of [setLeadingZeroAndTransmitCDMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The leading zero and check digit transmission mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setLeadingZeroAndTransmitCDMode(
        deviceId: String,
        mode: UPCELeadingZeroAndTransmitCDMode,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Sets the conversion mode for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The desired conversion mode for UPCE symbology.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode
    ): CommandResponse

    /**
     * Callback-based version of [setConversionMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The conversion mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setConversionMode(
        deviceId: String,
        mode: UPCEConversionMode,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Sets the 2-character add-on for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 2-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse

    /**
     * Callback-based version of [setAddOnPlus2] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled Whether to enable or disable the 2-character add-on.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setAddOnPlus2(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    )

    /**
     * Sets the 5-character add-on for UPCE symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the 5-character add-on.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean
    ): CommandResponse

    /**
     * Callback-based version of [setAddOnPlus5] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled Whether to enable or disable the 5-character add-on.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setAddOnPlus5(
        deviceId: String,
        enabled: Boolean,
        callback: (Result<CommandResponse>) -> Unit
    )
}
