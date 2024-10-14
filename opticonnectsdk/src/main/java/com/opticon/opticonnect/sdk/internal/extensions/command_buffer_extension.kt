package com.opticon.opticonnect.sdk.internal.extensions

fun StringBuilder.addCommand(command: String) {
    when (command.length) {
        4 -> append(']').append(command)
        3 -> append('[').append(command)
        else -> append(command)
    }
}
