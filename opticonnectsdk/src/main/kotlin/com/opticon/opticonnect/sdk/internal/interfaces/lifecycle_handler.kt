package com.opticon.opticonnect.sdk.internal.interfaces

import android.content.Context
import java.io.Closeable

internal interface LifecycleHandler : Closeable {
    fun initialize(context: Context)
}