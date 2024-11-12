package com.opticon.opticonnect.sdk.internal.utils

import android.os.Handler
import android.os.Looper
import java.io.Closeable

internal class TimeoutManager : Closeable {

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

    override fun close() {
        cancelTimeout()
        timeoutHandler = null
        timeoutRunnable = null
    }
}
