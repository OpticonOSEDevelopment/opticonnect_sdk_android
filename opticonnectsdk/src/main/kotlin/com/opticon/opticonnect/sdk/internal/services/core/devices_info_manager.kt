package com.opticon.opticonnect.sdk.internal.services.core

import com.opticon.opticonnect.sdk.api.constants.commands.CommunicationCommands
import com.opticon.opticonnect.sdk.api.constants.commands.DirectInputKeyCommands
import com.opticon.opticonnect.sdk.api.entities.DeviceInfo
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.api.interfaces.ScannerInfo
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DevicesInfoManager @Inject constructor(
    private val commandExecutorsManager: CommandExecutorsManager
) : ScannerInfo {
    private val macAddresses = ConcurrentHashMap<String, String>()
    private val serialNumbers = ConcurrentHashMap<String, String>()
    private val localNames = ConcurrentHashMap<String, String>()
    private val firmwareVersions = ConcurrentHashMap<String, String>()

    override fun getInfo(deviceId: String): DeviceInfo? {
        if (!hasCachedInfo(deviceId)) return null

        return DeviceInfo(
            deviceId = deviceId,
            macAddress = macAddresses[deviceId] ?: "",
            serialNumber = serialNumbers[deviceId] ?: "",
            localName = localNames[deviceId] ?: "",
            firmwareVersion = firmwareVersions[deviceId] ?: ""
        )
    }

    private fun hasCachedInfo(deviceId: String): Boolean {
        return macAddresses.containsKey(deviceId) ||
            serialNumbers.containsKey(deviceId) ||
            localNames.containsKey(deviceId) ||
            firmwareVersions.containsKey(deviceId)
    }

    suspend fun fetchInfo(deviceId: String) {
        fetchAndStoreInfo(deviceId, macAddresses, DirectInputKeyCommands.DIRECT_INPUT_KEY_M)
        fetchAndStoreInfo(deviceId, serialNumbers, DirectInputKeyCommands.DIRECT_INPUT_KEY_5)
        fetchAndStoreInfo(deviceId, localNames, DirectInputKeyCommands.DIRECT_INPUT_KEY_N)
        fetchAndStoreInfo(deviceId, firmwareVersions, DirectInputKeyCommands.DIRECT_INPUT_KEY_1)
    }

    private suspend fun fetchAndStoreInfo(
        deviceId: String,
        store: MutableMap<String, String>,
        parameter: String
    ) {
        try {
            if (!store.containsKey(deviceId)) {
                val result = commandExecutorsManager.sendCommand(
                    deviceId,
                    ScannerCommand(CommunicationCommands.TRANSMIT_DEVICE_INFORMATION, listOf(parameter), sendFeedback = false)
                )

                if (result.succeeded) {
                    var fetchedData = result.response
                    if (parameter == DirectInputKeyCommands.DIRECT_INPUT_KEY_M) {
                        fetchedData = formatMacAddress(fetchedData)
                    }
                    store[deviceId] = fetchedData
                }
            }
        } catch (e: Exception) {
            Timber.e("Failed to fetch device info: $e")
        }
    }

    private fun formatMacAddress(macAddress: String): String {
        var cleanMacAddress = macAddress.replace(Regex("[^0-9A-Fa-f]+$"), "")
        if (cleanMacAddress.length % 2 == 1) {
            throw IllegalArgumentException("Invalid MAC address: $cleanMacAddress")
        }
        return cleanMacAddress.chunked(2).joinToString(":")
    }
}
