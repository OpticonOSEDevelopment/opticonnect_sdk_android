package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.internal.interfaces.DirectInputKeysHelper
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ConnectionPool
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConnectionPoolImpl @Inject constructor(
    private val directInputKeysHelper: DirectInputKeysHelper
) : ConnectionPool, SettingsBase() {

    private val reservedHexIds = listOf("0001", "0002", "0003", "30F4", "46F9", "9BE5")

    private fun validateHexId(id: String): CommandResponse {
        val hexPattern = Regex("^[0-9A-Fa-f]{4}$")
        if (!hexPattern.matches(id)) {
            val msg = "Invalid ID: must be a 4-character hexadecimal value."
            Timber.w(msg)
            return CommandResponse.failed(msg)
        }
        if (reservedHexIds.contains(id)) {
            val msg = "Invalid ID: ID is reserved. Reserved IDs are: ${reservedHexIds.joinToString(", ")}."
            Timber.w(msg)
            return CommandResponse.failed(msg)
        }
        return CommandResponse.succeeded()
    }

    private fun getDirectInputKeysFromHexId(hexId: String): List<String> {
        return hexId.uppercase().mapNotNull { char ->
            val directInputKey = directInputKeysHelper.stringToDirectInputKey(char.toString())
            directInputKey?.let { directInputKeysHelper.directInputKeyToString(it) }
        }
    }

    override suspend fun setHexId(deviceId: String, poolId: String): CommandResponse {
        val validationResponse = validateHexId(poolId)
        if (!validationResponse.succeeded) return validationResponse

        val directInputKeys = getDirectInputKeysFromHexId(poolId)
        val result = sendCommand(deviceId, CommunicationCommands.SET_CONNECTION_POOL_ID, parameters = directInputKeys)
        return if (result.succeeded) {
            sendCommand(deviceId, CommunicationCommands.SAVE_SETTINGS)
        } else result
    }

    override suspend fun resetHexId(deviceId: String): CommandResponse {
        val directInputKeys = getDirectInputKeysFromHexId("0000")
        return sendCommand(deviceId, CommunicationCommands.SET_CONNECTION_POOL_ID, parameters = directInputKeys)
    }

    override fun isValidHexId(poolId: String): Boolean {
        return validateHexId(poolId).succeeded
    }

    override fun getConnectionPoolQRData(poolId: String): String {
        if (!isValidHexId(poolId)) return ""
        val directInputKeys = getDirectInputKeysFromHexId(poolId)
        return "@MENU_OPTO@ZZ@BBP@${directInputKeys.joinToString("@")}@ZZ@OTPO_UNEM@"
    }
}
