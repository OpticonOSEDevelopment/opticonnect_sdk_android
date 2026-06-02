package com.opticon.opticonnect.sdk.api.interfaces

/**
 * Handle for a long-running SDK listener.
 *
 * Call [close] when the caller no longer needs updates, for example when a generated screen is
 * stopped or when switching to a different scanner.
 */
interface ListenerSubscription {
    val isClosed: Boolean

    fun close()
}
