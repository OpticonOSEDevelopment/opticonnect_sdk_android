package com.opticon.opticonnect.sdk.api.interfaces

interface Callback<T> {
    fun onSuccess(data: T)
    fun onError(error: Throwable)
}