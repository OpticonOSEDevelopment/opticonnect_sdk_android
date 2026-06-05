package com.opticon.opticonnect.sdk.internal.interfaces

import android.content.Context
import com.opticon.opticonnect.sdk.api.entities.CommandData

/**
 * Interface that defines methods for handling scanner settings.
 */
internal interface SettingsHandler {

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
     * Checks whether the provided command code is part of the scanner's default settings.
     */
    fun isDefaultCode(code: String): Boolean

    /**
     * Normalizes command codes to the database representation.
     *
     * Scanner commands can be represented with transport prefixes such as '[' or ']'.
     * Runtime scanner settings are keyed by the stripped command code so API constants,
     * fetched BPT settings, and database metadata resolve to the same key.
     */
    fun normalizeCode(code: String): String

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

    /**
     * Applies a command to a mutable settings map using the scanner metadata conflict rules.
     */
    fun applyCommandToSettings(
        settings: MutableMap<String, List<String>>,
        commandData: CommandData
    )

    /**
     * Adds a command to a compressed settings list using the same conflict rules.
     */
    fun addCommandToCompressedList(
        commandData: CommandData,
        compressedList: MutableList<CommandData>
    )
}
