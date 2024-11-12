package com.opticon.opticonnect.sdk.internal.scanner_settings

import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.internal.interfaces.DirectInputKeysHelper
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.ConnectionPool
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConnectionPoolImpl @Inject constructor(
    private val directInputKeysHelper: DirectInputKeysHelper
) : ConnectionPool, SettingsBase() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val connectionPoolIds = mutableMapOf<String, String>()
    private val reservedHexIds = listOf("0001", "0002", "0003", "30F4", "46F9", "9BE5")

    private fun validateId(id: String): CommandResponse {
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
            val directInputKey = directInputKeysHelper.charToDirectInputKey(char)
            directInputKey?.let { directInputKeysHelper.directInputKeyToString(it) }
        }
    }

    override suspend fun setId(deviceId: String, poolId: String): CommandResponse {
        val validationResponse = validateId(poolId)
        if (!validationResponse.succeeded) return validationResponse

        val directInputKeys = getDirectInputKeysFromHexId(poolId)
        Timber.d("Setting connection pool ID to $poolId for device $deviceId with parameters $directInputKeys")
        val result = sendCommand(deviceId, CommunicationCommands.SET_CONNECTION_POOL_ID, parameters = directInputKeys)
        if (result.succeeded) {
            cacheId(deviceId, poolId)  // Store the new pool ID
        }
        return result
    }

    override fun setId(deviceId: String, poolId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setId(deviceId, poolId) }
    }

    override fun cacheId(deviceId: String, poolId: String) {
        connectionPoolIds[deviceId] = poolId
    }

    override suspend fun getId(deviceId: String): String {
        return connectionPoolIds[deviceId] ?: "0000"  // Return "0000" if ID is unknown
    }

    override fun getId(deviceId: String, callback: (Result<String>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { getId(deviceId) }
    }

    override suspend fun resetId(deviceId: String): CommandResponse {
        connectionPoolIds[deviceId] = "0000"  // Reset the pool ID to "0000"
        val directInputKeys = getDirectInputKeysFromHexId("0000")
        return sendCommand(deviceId, CommunicationCommands.SET_CONNECTION_POOL_ID, parameters = directInputKeys)
    }

    override fun resetId(deviceId: String, callback: (Result<CommandResponse>) -> Unit) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { resetId(deviceId) }
    }

    override fun isValidId(poolId: String): Boolean {
        return validateId(poolId).succeeded
    }

    override fun getConnectionPoolQRData(poolId: String): String {
        if (!isValidId(poolId)) return ""
        val directInputKeys = getDirectInputKeysFromHexId(poolId)
        return "@MENU_OPTO@ZZ@BBP@${directInputKeys.joinToString("@")}@ZZ@OTPO_UNEM@"
    }
}
