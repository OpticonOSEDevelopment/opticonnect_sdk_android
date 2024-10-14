package com.opticon.opticonnect.sdk

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.internal.services.settings.SettingsHandler
import junit.framework.TestCase.assertTrue
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest : KoinTest {

    private lateinit var database: SupportSQLiteDatabase
    private val settingsHandler: SettingsHandler by inject()
    private val databaseManager: DatabaseManager by inject()
    private val databaseTablesHelper: DatabaseTablesHelper by inject()

    @Before
    fun setup() = runBlocking {
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(defaultModule) //test
        }
        database = databaseManager.getDatabase()
    }

    @After
    fun teardown() = runBlocking {
        // Close the database after the test
        databaseManager.closeDatabase()
    }

    @Test
    fun testDatabaseHasTables() = runBlocking {
        // Get the list of tables from the database
        val tables = databaseTablesHelper.getTables(database)

        // Ensure that the database contains at least one table
        assertTrue("The database should contain tables.", tables.isNotEmpty())

        // Log the table names (optional)
        tables.forEach { table ->
            println("Table found: $table")
        }
    }
}
