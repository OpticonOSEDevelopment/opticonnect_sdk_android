package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import com.opticon.opticonnect.sdk.api.entities.CommandResponse
import com.opticon.opticonnect.sdk.api.scanner_settings.enums.code_specific.TelepenMode
import com.opticon.opticonnect.sdk.api.scanner_settings.interfaces.code_specific.Telepen
import com.opticon.opticonnect.sdk.internal.scanner_settings.descriptors.code_specific.TelepenSettingDescriptors
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore
import com.opticon.opticonnect.sdk.internal.utils.CallbackUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TelepenImpl @Inject constructor(
    scannerSettingsStateStore: ScannerSettingsStateStore
) : CodeSpecificSettingsBase(scannerSettingsStateStore), Telepen {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun setMode(deviceId: String, mode: TelepenMode): CommandResponse {
        val command = TelepenSettingDescriptors.mode.commandFor(mode)
        Timber.d("Setting Telepen code mode for deviceId $deviceId to $mode")
        return sendMappedCommand(deviceId, command, "Unsupported Telepen mode: $mode")
    }

    override fun setMode(deviceId: String, mode: TelepenMode, callback: Callback<CommandResponse>) {
        CallbackUtils.wrapWithCallback(coroutineScope, callback) { setMode(deviceId, mode) }
    }

    override fun getMode(deviceId: String): TelepenMode {
        return TelepenSettingDescriptors.mode.valueFrom(settingsFor(deviceId))
    }
}
