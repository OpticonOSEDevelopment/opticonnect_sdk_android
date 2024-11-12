package com.opticon.opticonnect.sdk.internal.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.Result

object CallbackUtils {

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
        callback: (Result<T>) -> Unit,
        block: suspend () -> T
    ) {
        scope.launch {
            runCatching { block() }
                .onSuccess { callback(Result.success(it)) }
                .onFailure { callback(Result.failure(it)) }
        }
    }
}
