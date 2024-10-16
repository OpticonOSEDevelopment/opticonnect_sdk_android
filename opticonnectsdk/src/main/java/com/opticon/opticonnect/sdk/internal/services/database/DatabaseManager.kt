package com.opticon.opticonnect.sdk.internal.services.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseManager @Inject constructor(private val context: Context) {
    companion object {
        const val DB_NAME = "commands.db"
        const val DB_PATH = "databases/$DB_NAME"
    }

    private var database: SupportSQLiteDatabase? = null

    suspend fun getDatabase(): SupportSQLiteDatabase {
        if (database != null) return database!!

        return initializeDatabase()
    }

    private suspend fun initializeDatabase(): SupportSQLiteDatabase {
        val databasePath = context.getDatabasePath(DB_NAME).path

        // Copy the prebuilt database from assets to the app's data directory
        if (!File(databasePath).exists()) {
            copyDatabaseFromAssets(databasePath)
        }

        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(DB_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) {}
            })
            .build()

        val openHelper = FrameworkSQLiteOpenHelperFactory().create(configuration)

        database = openHelper.readableDatabase
        return database!!
    }

    private suspend fun copyDatabaseFromAssets(databasePath: String) {
        withContext(Dispatchers.IO) {
            val inputStream: InputStream = context.assets.open(DB_PATH)
            val outputStream: OutputStream = FileOutputStream(databasePath)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }

    suspend fun closeDatabase() {
        withContext(Dispatchers.IO) {
            database?.close()
            database = null
        }
    }
}
