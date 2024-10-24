package com.opticon.opticonnect.sdk.internal.services.commands

import com.opticon.opticonnect.sdk.api.interfaces.ScannerFeedback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ScannerFeedbackImpl @Inject constructor() : ScannerFeedback {
    private var _led: Boolean = true
    override val led: Boolean
        get() = _led

    private var _buzzer: Boolean = true
    override val buzzer: Boolean
        get() = _buzzer

    private var _vibration: Boolean = true
    override val vibration: Boolean
        get() = _vibration

    override fun set(
        led: Boolean?,
        buzzer: Boolean?,
        vibration: Boolean?
    ) {
        led?.let { _led = it }
        buzzer?.let { _buzzer = it }
        vibration?.let { _vibration = it }
    }
}
