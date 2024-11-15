package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CompositeCodesOutputMode

/**
 * Interface representing settings for composite codes.
 *
 * This interface provides methods to configure the output mode, and manage settings for GS1 and EAN/UPC composite codes.
 */
interface CompositeCodes {

    /**
     * Sets the output mode for composite codes.
     *
     * @param deviceId The identifier of the target device.
     * @param outputMode The [CompositeCodesOutputMode] enum value representing the desired output mode.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setOutputMode(deviceId: String, outputMode: CompositeCodesOutputMode): CommandResponse

    /**
     * Callback-based version of [setOutputMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param outputMode The [CompositeCodesOutputMode] enum value representing the desired output mode.
     * @param callback Callback to receive [CommandResponse] or an error.
     */
    fun setOutputMode(deviceId: String, outputMode: CompositeCodesOutputMode, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets whether to ignore the link flag for composite codes.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to ignore (`true`) or not ignore (`false`) the link flag.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setIgnoreLinkFlag(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setIgnoreLinkFlag] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to ignore or not ignore the link flag.
     * @param callback Callback to receive [CommandResponse] or an error.
     */
    fun setIgnoreLinkFlag(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the state for GS1 Databar and GS1-128 composite codes.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) GS1 composite codes.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCompositeGS1DatabarGS1128(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setCompositeGS1DatabarGS1128] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable GS1 composite codes.
     * @param callback Callback to receive [CommandResponse] or an error.
     */
    fun setCompositeGS1DatabarGS1128(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the state for EAN/UPC composite codes.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) EAN/UPC composite codes.
     * @return A [CommandResponse] indicating the success or failure of the operation.
     */
    suspend fun setCompositeEANUPC(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setCompositeEANUPC] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable EAN/UPC composite codes.
     * @param callback Callback to receive [CommandResponse] or an error.
     */
    fun setCompositeEANUPC(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)
}
