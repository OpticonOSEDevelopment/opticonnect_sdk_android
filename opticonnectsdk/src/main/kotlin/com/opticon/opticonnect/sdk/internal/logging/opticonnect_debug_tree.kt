package com.opticon.opticonnect.sdk.internal.logging

import timber.log.Timber

internal class OptiConnectDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        // Prefix log with "OptiConnect: "
        return "OptiConnect: ${super.createStackElementTag(element)}"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Prefix the message itself if needed
        super.log(priority, tag, "OptiConnect: $message", t)
    }
}
