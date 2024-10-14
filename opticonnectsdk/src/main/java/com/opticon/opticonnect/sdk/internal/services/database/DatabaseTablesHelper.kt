package com.opticon.opticonnect.sdk.internal.services.database

import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.core.annotation.Single

@Single
class DatabaseTablesHelper {

    // Retrieve the list of tables from the SQLite database
    fun getTables(database: SupportSQLiteDatabase): List<String> {
        val tablesList = mutableListOf<String>()

        val cursor = database.query(
            """
            SELECT 
                name
            FROM 
                sqlite_master
            WHERE 
                type ='table' AND 
                name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%';
            """
        )

        // Read cursor and get table names
        while (cursor.moveToNext()) {
            val tableName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            tablesList.add(tableName)
        }
        cursor.close()

        return tablesList
    }

    // Check if a specific table exists in the database
    suspend fun containsTable(database: SupportSQLiteDatabase, tableName: String): Boolean {
        val tables = getTables(database)
        return tables.contains(tableName)
    }
}
