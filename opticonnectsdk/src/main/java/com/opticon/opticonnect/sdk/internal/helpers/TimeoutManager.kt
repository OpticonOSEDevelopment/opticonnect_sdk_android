package com.opticon.opticonnect.sdk.internal.helpers

import android.os.Handler
import android.os.Looper

class TimeoutManager {

    private var timeoutHandler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    fun startTimeout(timeoutDuration: Long, onTimeout: () -> Unit) {
        cancelTimeout()  // Cancel any existing timeout

        // Initialize the handler and runnable
        timeoutHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable { onTimeout() }
        timeoutRunnable = runnable

        // Post the non-null runnable
        timeoutHandler?.postDelayed(runnable, timeoutDuration)
    }

    fun cancelTimeout() {
        timeoutRunnable?.let {
            timeoutHandler?.removeCallbacks(it)
        }
    }

    fun dispose() {
        cancelTimeout()
        timeoutHandler = null
        timeoutRunnable = null
    }
}
