package com.opticon.opticonnect.sdk.public.entities

/**
 * Represents a command and its associated parameters for scanner settings.
 *
 * This class is used to store a command and the list of parameters that
 * are associated with the command when interacting with a scanner.
 *
 * It provides a structured way to store and access a command's details.
 */
data class CommandData(
    /**
     * The command being executed or retrieved.
     *
     * This field stores the specific code of the command.
     */
    var command: String,

    /**
     * The list of parameters associated with the command.
     */
    var parameters: MutableList<String> = mutableListOf()
)