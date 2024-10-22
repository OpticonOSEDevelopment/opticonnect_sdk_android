package com.opticon.opticonnect.sdk.api.interfaces

import android.content.Context

/**
 * Interface that defines methods for handling scanner settings.
 */
interface SettingsHandler {

    /**
     * Initializes the scanner settings handler.
     *
     * @param context The application context.
     * @param closeDB Flag to indicate whether the database should be closed after initialization.
     */
    fun initialize(context: Context, closeDB: Boolean = true)

    /**
     * Checks if the provided code is a direct input key.
     *
     * @param code The code to check.
     * @return True if the code is a direct input key, false otherwise.
     */
    fun isDirectInputKey(code: String): Boolean

    /**
     * Retrieves the groups that should be disabled for a given code.
     *
     * @param code The code to retrieve the disabled groups for.
     * @return A list of groups that should be disabled for the provided code.
     */
    fun getGroupsToDisableForCode(code: String): List<String>

    /**
     * Retrieves the groups associated with a given code.
     *
     * @param code The code to retrieve the groups for.
     * @return A list of groups associated with the provided code.
     */
    fun getGroupsForCode(code: String): List<String>
}
