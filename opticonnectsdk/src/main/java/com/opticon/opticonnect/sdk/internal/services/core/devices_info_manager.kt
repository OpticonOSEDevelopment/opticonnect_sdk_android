package com.opticon.opticonnect.sdk.internal.services.core

import com.opticon.opticonnect.sdk.api.constants.commands.communication.TRANSMIT_DEVICE_INFORMATION
import com.opticon.opticonnect.sdk.api.constants.commands.direct_input_keys.DIRECT_INPUT_KEY_1
import com.opticon.opticonnect.sdk.api.constants.commands.direct_input_keys.DIRECT_INPUT_KEY_5
import com.opticon.opticonnect.sdk.api.constants.commands.direct_input_keys.DIRECT_INPUT_KEY_M
import com.opticon.opticonnect.sdk.api.constants.commands.direct_input_keys.DIRECT_INPUT_KEY_N
import com.opticon.opticonnect.sdk.api.entities.DeviceInfo
import com.opticon.opticonnect.sdk.api.entities.ScannerCommand
import com.opticon.opticonnect.sdk.internal.services.commands.CommandExecutorsManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevicesInfoManager @Inject constructor(
    private val commandExecutorsManager: CommandExecutorsManager
) {

    private val macAddresses = mutableMapOf<String, String>()
    private val serialNumbers = mutableMapOf<String, String>()
    private val localNames = mutableMapOf<String, String>()
    private val firmwareVersions = mutableMapOf<String, String>()

    fun getInfo(deviceId: String): DeviceInfo {
        return DeviceInfo(
            deviceId = deviceId,
            macAddress = macAddresses[deviceId] ?: "",
            serialNumber = serialNumbers[deviceId] ?: "",
            localName = localNames[deviceId] ?: "",
            firmwareVersion = firmwareVersions[deviceId] ?: ""
        )
    }

    suspend fun fetchInfo(deviceId: String) {
        fetchAndStoreInfo(deviceId, macAddresses, DIRECT_INPUT_KEY_M)
        fetchAndStoreInfo(deviceId, serialNumbers, DIRECT_INPUT_KEY_5)
        fetchAndStoreInfo(deviceId, localNames, DIRECT_INPUT_KEY_N)
        fetchAndStoreInfo(deviceId, firmwareVersions, DIRECT_INPUT_KEY_1)
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
                    ScannerCommand(TRANSMIT_DEVICE_INFORMATION, listOf(parameter), sendFeedback = false)
                )

                if (result.succeeded) {
                    var fetchedData = result.response
                    if (parameter == DIRECT_INPUT_KEY_M) {
                        fetchedData = formatMacAddress(fetchedData)
                    }
                    store[deviceId] = fetchedData
                    Timber.e("Device info fetched: $parameter: $fetchedData")
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
        Timber.e("Try formatting MAC address: $cleanMacAddress")

        return cleanMacAddress.chunked(2).joinToString(":")
    }
}
