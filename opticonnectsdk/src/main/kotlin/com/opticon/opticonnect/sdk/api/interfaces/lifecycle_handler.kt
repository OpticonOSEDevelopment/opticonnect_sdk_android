package com.opticon.opticonnect.sdk.api.interfaces

import android.content.Context
import java.io.Closeable

interface LifecycleHandler : Closeable {
    fun initialize(context: Context)
}