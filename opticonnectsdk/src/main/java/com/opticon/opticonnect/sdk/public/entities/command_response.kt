package com.opticon.opticonnect.sdk.public.entities

/**
 * A class representing the response of a command sent to a device.
 *
 * This class holds the response message and a flag indicating whether the command was successful.
 *
 * @property response The response message from the command.
 * If the command fails, this contains the error message. If successful, it may be empty.
 * @property succeeded A flag indicating whether the command succeeded (`true`) or failed (`false`).
 */
class CommandResponse(
    val response: String,
    val succeeded: Boolean
) {

    /**
     * Companion object to create common instances of [CommandResponse].
     */
    companion object {
        /**
         * Creates a [CommandResponse] representing a failure.
         *
         * @param message The error message explaining why the command failed.
         * @return A [CommandResponse] with the `succeeded` flag set to `false` and the provided error message.
         */
        fun failed(message: String): CommandResponse {
            return CommandResponse(message, false)
        }

        /**
         * Creates a [CommandResponse] representing a success.
         *
         * @return A [CommandResponse] with an empty response message and the `succeeded` flag set to `true`.
         */
        fun succeeded(): CommandResponse {
            return CommandResponse("", true)
        }
    }
}
