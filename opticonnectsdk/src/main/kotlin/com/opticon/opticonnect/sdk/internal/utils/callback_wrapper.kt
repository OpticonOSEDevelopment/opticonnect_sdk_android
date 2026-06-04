package com.opticon.opticonnect.sdk.internal.utils

import com.opticon.opticonnect.sdk.api.interfaces.Callback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal object CallbackUtils {

    /**
     * Wraps a suspend function with a callback, allowing for safe, asynchronous
     * handling of results or exceptions in a coroutine.
     *
     * @param scope The coroutine scope in which the function should be executed.
     * @param callback The callback that receives the result or an error.
     * @param block The suspend function to execute.
     */
    fun <T> wrapWithCallback(
        scope: CoroutineScope,
        callback: Callback<T>,
        block: suspend () -> T
    ) {
        scope.launch {
            runCatching { block() }
                .onSuccess { callback.onSuccess(it) }
                .onFailure { callback.onError(it) }
        }
    }
}
