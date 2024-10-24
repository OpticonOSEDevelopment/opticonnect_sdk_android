package com.opticon.opticonnect.sdk.internal.services.scanner_settings

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.opticon.opticonnect.sdk.api.interfaces.SettingsHandler
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseFields
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsHandlerImpl @Inject constructor(
    private val databaseTablesHelper: DatabaseTablesHelper,
    private val databaseManager: DatabaseManager
) : SettingsHandler {
    private val groupsForCode = mutableMapOf<String, List<String>>()
    private val dimensionsForCode = mutableMapOf<String, List<String>>()
    private val codesForGroup = mutableMapOf<String, MutableList<String>>()
    private val groupsToEnableForCode = mutableMapOf<String, List<String>>()
    private val groupsToDisableForCode = mutableMapOf<String, List<String>>()
    private val defaultScannerSettings = mutableMapOf<String, List<String>>()
    private val descriptionForCode = mutableMapOf<String, String>()
    private val charToDirectInputKey = mutableMapOf<String, String>()

    private var directInputKeysSet = mutableSetOf<String>()
    private var directInputKeys = listOf<String>()

    override fun initialize(context: Context, closeDB: Boolean) {
        val database = databaseManager.getDatabase(context)
        initializeCodesDataStructures(database)
        setDescriptions(database)
        setDirectInputKeys()
        if (closeDB) {
            databaseManager.closeDatabase()
        }
    }

    override fun isDirectInputKey(code: String): Boolean {
        return directInputKeysSet.contains(code)
    }

    private fun getStrippedCode(code: String): String {
        return if (code.startsWith("[") || code.startsWith("]")) {
            code.substring(1)
        } else {
            code
        }
    }

    override fun getGroupsToDisableForCode(code: String): List<String> {
        return groupsToDisableForCode[getStrippedCode(code)] ?: listOf()
    }

    override fun getGroupsForCode(code: String): List<String> {
        return groupsForCode[getStrippedCode(code)] ?: listOf()
    }

    private fun convertCommaSeparatedToLowerCaseList(commaSeparated: String): List<String> {
        return commaSeparated.split(",")
            .filter { it.isNotEmpty() }
            .map { it.trim().lowercase() }
    }

    private fun initializeCodesDataStructures(database: SupportSQLiteDatabase) {
        try {
            val tables = databaseTablesHelper.getTables(database)
            for (table in tables) {
                try {
                    val data = database.query("SELECT * FROM $table")
                    if (data.count == 0 || !data.columnNames.contains(DatabaseFields.CODE)) {
                        continue
                    }
                    while (data.moveToNext()) {
                        val codeColumnIndex = data.getColumnIndex(DatabaseFields.CODE)
                        val descriptionKeyColumnIndex = data.getColumnIndex(DatabaseFields.DESCRIPTION_KEY)
                        val groupsColumnIndex = data.getColumnIndex(DatabaseFields.GROUPS)
                        val dimensionsColumnIndex = data.getColumnIndex(DatabaseFields.DIMENSIONS)
                        val enablesGroupsColumnIndex = data.getColumnIndex(DatabaseFields.ENABLES_GROUPS)
                        val disablesGroupsColumnIndex = data.getColumnIndex(DatabaseFields.DISABLES_GROUPS)

                        if (codeColumnIndex >= 0 && descriptionKeyColumnIndex >= 0) {
                            val code = data.getString(codeColumnIndex)
                            val descriptionKey = data.getString(descriptionKeyColumnIndex)
                            val groupsString = if (groupsColumnIndex >= 0) data.getString(groupsColumnIndex) ?: "" else ""
                            val dimensionsString = if (dimensionsColumnIndex >= 0) data.getString(dimensionsColumnIndex) ?: "" else ""
                            val enablesGroupsString = if (enablesGroupsColumnIndex >= 0) data.getString(enablesGroupsColumnIndex) ?: "" else ""
                            val disablesGroupsString = if (disablesGroupsColumnIndex >= 0) data.getString(disablesGroupsColumnIndex) ?: "" else ""

                            if (code != null && descriptionKey != null) {
                                val groups = convertCommaSeparatedToLowerCaseList(groupsString)
                                groupsForCode[code] = groups
                                val dimensions = convertCommaSeparatedToLowerCaseList(dimensionsString)
                                dimensionsForCode[code] = dimensions
                                val enablesGroups = convertCommaSeparatedToLowerCaseList(enablesGroupsString)
                                val disablesGroups = convertCommaSeparatedToLowerCaseList(disablesGroupsString)

                                for (group in groups) {
                                    codesForGroup.computeIfAbsent(group) { mutableListOf() }.add(code)
                                }
                                groupsToEnableForCode[code] = enablesGroups
                                groupsToDisableForCode[code] = disablesGroups
                            }
                        }
                    }
                    data.close()
                } catch (e: Exception) {
                    Timber.e("Failed to process table $table: $e")
                }
            }

            val defaultCodes = codesForGroup[DatabaseFields.DEFAULT]
            if (defaultCodes != null) {
                for (code in defaultCodes) {
                    defaultScannerSettings[code] = listOf()
                }
            }
        } catch (e: Exception) {
            Timber.e("Failed to initialize code data structures: $e")
        }
    }

    private fun setDescriptions(database: SupportSQLiteDatabase) {
        try {
            val tables = databaseTablesHelper.getTables(database)
            for (table in tables) {
                try {
                    val data = database.query("SELECT * FROM $table")
                    if (data.count == 0 || !data.columnNames.contains(DatabaseFields.CODE)) {
                        continue
                    }
                    while (data.moveToNext()) {
                        val codeColumnIndex = data.getColumnIndex(DatabaseFields.CODE)
                        val descriptionKeyColumnIndex = data.getColumnIndex(DatabaseFields.DESCRIPTION_KEY)

                        if (codeColumnIndex >= 0 && descriptionKeyColumnIndex >= 0) {
                            val code = data.getString(codeColumnIndex)
                            val descriptionKey = data.getString(descriptionKeyColumnIndex)

                            if (code != null && descriptionKey != null) {
                                descriptionForCode[code] = descriptionKey
                            }
                        }
                    }
                    data.close()
                } catch (e: Exception) {
                    Timber.e("Failed to set descriptions for table $table: $e")
                }
            }
        } catch (e: Exception) {
            Timber.e("Failed to set descriptions: $e")
        }
    }

    private fun setDirectInputKeys() {
        try {
            directInputKeys = codesForGroup[DatabaseFields.DIRECT_INPUT_KEYS]?.toList() ?: listOf()
            directInputKeysSet = directInputKeys.toMutableSet()

            for (key in directInputKeys) {
                val description = descriptionForCode[key]
                if (description != null && description.length == 1) {
                    charToDirectInputKey[description] = key
                }
            }
        } catch (e: Exception) {
            Timber.e("Failed to set direct input keys: $e")
        }
    }
}
