package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.GS1Databar
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.GS1DatabarSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GS1DatabarImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), GS1Databar {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setTransmitCD(deviceId: String, enabled: Boolean): CommandResponse {
        val command = GS1DatabarSettingDescriptors.transmitCD.commandFor(enabled)
        Timber.d("Setting GS1 Databar transmit check digit for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitCD(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitCD(deviceId, enabled) }
    }

    override fun isTransmitCDEnabled(deviceId: String): Boolean {
        return GS1DatabarSettingDescriptors.transmitCD.valueFrom(settingsFor(deviceId))
    }

    override suspend fun setTransmitAI(deviceId: String, enabled: Boolean): CommandResponse {
        val command = GS1DatabarSettingDescriptors.transmitAI.commandFor(enabled)
        Timber.d("Setting GS1 Databar transmit AI for deviceId $deviceId to ${if (enabled) "enabled" else "disabled"}")
        return sendCommand(deviceId, command)
    }

    override fun setTransmitAI(deviceId: String, enabled: Boolean, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setTransmitAI(deviceId, enabled) }
    }

    override fun isTransmitAIEnabled(deviceId: String): Boolean {
        return GS1DatabarSettingDescriptors.transmitAI.valueFrom(settingsFor(deviceId))
    }
}
