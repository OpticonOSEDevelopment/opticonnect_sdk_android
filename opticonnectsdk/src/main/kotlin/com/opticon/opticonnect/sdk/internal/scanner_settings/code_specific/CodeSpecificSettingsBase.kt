package com.opticon.opticonnect.sdk.internal.scanner_settings.code_specific

import com.opticon.opticonnect.sdk.internal.scanner_settings.SettingsBase
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.ScannerSettingsStateStore

internal abstract class CodeSpecificSettingsBase(
    private val scannerSettingsStateStore: ScannerSettingsStateStore
) : SettingsBase() {
    protected fun settingsFor(deviceId: String): Map<String, List<String>> {
        return scannerSettingsStateStore.settingsFor(deviceId)
    }
}
