package com.opticon.opticonnect.sdk

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import junit.framework.TestCase.assertTrue
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    @Inject
    lateinit var settingsHandler: SettingsHandler

    @Inject
    lateinit var databaseManager: DatabaseManager

    @Inject
    lateinit var databaseTablesHelper: DatabaseTablesHelper

    private lateinit var database: SupportSQLiteDatabase
    private lateinit var testComponent: TestComponent

    @Before
    fun setup() {
        // Create the Dagger TestComponent and inject dependencies
        testComponent = DaggerTestComponent.builder()
            .context(ApplicationProvider.getApplicationContext<Context>())
            .build()
        testComponent.inject(this)

        // Initialize the database
        database = runBlocking {
            databaseManager.getDatabase()
        }
    }

    @After
    fun teardown() {
        runBlocking {
            databaseManager.closeDatabase()
        }
    }

    @Test
    fun testDatabaseHasRequiredTables() = runBlocking {
        // Get the list of tables from the database
        val tables = databaseTablesHelper.getTables(database)

        // List of required tables
        val expectedTables = listOf(
            "default_settings_options",
            "readable_codes_options",
            "miscellaneous_options",
            "read_options",
            "memorizing_options",
            "keyboard_options",
            "indicator_options",
            "formatting_options",
            "direct_input_keys",
            "data_wizard_options",
            "code_specific_options",
            "code_options",
            "barcode_validation_options"
        )

        // Ensure that the database contains the required tables
        expectedTables.forEach { table ->
            assertTrue("Table $table should exist in the database.", tables.contains(table))
        }
    }

    @Test
    fun testInitializeCodesDataStructures() = runBlocking {
        settingsHandler.initialize()

        // Validate that specific data was loaded into the structures
        val groupsForSomeCode = settingsHandler.getGroupsForCode("R2B")
        assertTrue("Groups for 'R2B' should contain upcEAddon2", groupsForSomeCode.contains("upcEAddon2".lowercase()))
        assertTrue("Groups for 'R2B' should contain addon", groupsForSomeCode.contains("addon"))

        val groupsToDisableForSomeCode = settingsHandler.getGroupsToDisableForCode("4V")
        assertTrue("Groups to disable for '4V' should contain eanISSNTranslation", groupsToDisableForSomeCode.contains("eanISSNTranslation".lowercase()))
    }
}