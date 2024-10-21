package com.opticon.opticonnect.sdk

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.opticon.opticonnect.sdk.di.DaggerTestComponent
import com.opticon.opticonnect.sdk.di.TestComponent
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseManager
import com.opticon.opticonnect.sdk.internal.services.database.DatabaseTablesHelper
import com.opticon.opticonnect.sdk.internal.services.scanner_settings.SettingsHandler
import junit.framework.TestCase.assertTrue
import org.junit.Before
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {

    @Inject
    lateinit var databaseTablesHelper: DatabaseTablesHelper

    @Inject
    lateinit var settingsHandler: SettingsHandler

    @Inject
    lateinit var databaseManager: DatabaseManager

    companion object {
        private lateinit var context: Context
        private lateinit var testComponent: TestComponent
        private lateinit var database: SupportSQLiteDatabase
        private lateinit var settingsHandler: SettingsHandler
        private lateinit var databaseManager: DatabaseManager

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            // Get the context and set up Dagger
            context = ApplicationProvider.getApplicationContext<Context>()

            // Initialize Dagger TestComponent
            testComponent = DaggerTestComponent.builder()
                .context(context)
                .build()

            // Inject dependencies into a temporary instance for the companion setup
            val tempInstance = DatabaseIntegrationTest()
            testComponent.inject(tempInstance)

            settingsHandler = tempInstance.settingsHandler
            databaseManager = tempInstance.databaseManager

            // Initialize the database once for all tests
            database = runBlocking {
                databaseManager.getDatabase(context)
            }

            // Initialize the settings handler without closing the database
            runBlocking {
                settingsHandler.initialize(context, closeDB = false)
            }
        }

        @AfterClass
        @JvmStatic
        fun teardownClass() {
            // Close the database after all tests are done
            runBlocking {
                databaseManager.closeDatabase()
            }
        }
    }

    @Before
    fun injectDependencies() {
        // Inject dependencies into the instance
        testComponent.inject(this)
    }

    @Test
    fun testDatabaseHasRequiredTables() {
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
        // Validate that specific data was loaded into the structures
        val groupsForSomeCode = settingsHandler.getGroupsForCode("R2B")
        assertTrue("Groups for 'R2B' should contain upcEAddon2", groupsForSomeCode.contains("upcEAddon2".lowercase()))
        assertTrue("Groups for 'R2B' should contain addon", groupsForSomeCode.contains("addon"))

        val groupsToDisableForSomeCode = settingsHandler.getGroupsToDisableForCode("4V")
        assertTrue("Groups to disable for '4V' should contain eanISSNTranslation", groupsToDisableForSomeCode.contains("eanISSNTranslation".lowercase()))
    }
}
