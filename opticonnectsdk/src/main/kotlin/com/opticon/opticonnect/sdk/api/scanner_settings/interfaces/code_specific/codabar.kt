package com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMode
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarMinimumLength
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.CodabarStartStopTransmission

/**
 * Interface for managing Codabar-specific settings on the scanner.
 *
 * This interface provides methods for configuring Codabar-related settings
 * such as enabling or disabling check digits, transmitting start and stop characters,
 * and setting the Codabar mode.
 */
interface Codabar {

    /**
     * Sets the Codabar mode for the scanner.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The Codabar mode to set.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setMode(deviceId: String, mode: CodabarMode): CommandResponse

    /**
     * Callback-based version of [setMode] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param mode The Codabar mode to set.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setMode(deviceId: String, mode: CodabarMode, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the check digit state for Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the check digit.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setCheckCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setCheckCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the check digit.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setCheckCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the transmission state of the check digit for Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the transmission of the check digit.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setTransmitCD] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the transmission of the check digit.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setTransmitCD(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the space insertion mode between characters in Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) space insertion.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setSpaceInsertion(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setSpaceInsertion] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable space insertion.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setSpaceInsertion(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the minimum data length for Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param length The minimum data length to set.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setMinimumLength(deviceId: String, length: CodabarMinimumLength): CommandResponse

    /**
     * Callback-based version of [setMinimumLength] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param length The minimum data length to set.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setMinimumLength(deviceId: String, length: CodabarMinimumLength, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the intercharacter gap check mode for Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable (`true`) or disable (`false`) the intercharacter gap check.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setIntercharacterGapCheck(deviceId: String, enabled: Boolean): CommandResponse

    /**
     * Callback-based version of [setIntercharacterGapCheck] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param enabled A boolean indicating whether to enable or disable the intercharacter gap check.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setIntercharacterGapCheck(deviceId: String, enabled: Boolean, callback: (Result<CommandResponse>) -> Unit)

    /**
     * Sets the start and stop character transmission mode for Codabar symbology.
     *
     * @param deviceId The identifier of the target device.
     * @param transmission The start and stop character transmission mode.
     * @return A [CommandResponse] indicating the success or failure of the command.
     */
    suspend fun setStartStopTransmission(
        deviceId: String, transmission: CodabarStartStopTransmission
    ): CommandResponse

    /**
     * Callback-based version of [setStartStopTransmission] for Java interoperability.
     *
     * @param deviceId The identifier of the target device.
     * @param transmission The start and stop character transmission mode.
     * @param callback Callback to receive [CommandResponse].
     */
    fun setStartStopTransmission(
        deviceId: String, transmission: CodabarStartStopTransmission, callback: (Result<CommandResponse>) -> Unit
    )
}
