package com.inkwell.core.data.local.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MigrationTest {

    companion object {
        private const val TEST_DB_NAME = "test-database"
    }

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation,
        InkwellDatabase::class.java
    )

    @Before
    fun setup() {
        // Create initial database schema
    }

    @After
    fun teardown() {
        // Cleanup if needed
    }

    @Test
    fun migrate1To2_addsWordCountColumn() {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 1).apply {
            // Insert test data into version 1 schema
            execSQL(
                """
                INSERT INTO novels (id, title, description, cover_color, is_biometric_locked, created_at, updated_at)
                VALUES (1, 'Test Novel', 'Description', '#6650A4', 0, 1000000, 1000000)
                """
            )
            close()
        }

        val db = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            2,
            true,
            Migration_1_2()
        )

        // Verify the new column exists and has default value
        val cursor = db.query("SELECT word_count FROM novels WHERE id = 1")
        cursor.moveToFirst()
        val wordCount = cursor.getInt(0)
        assert(wordCount == 0)
    }

    @Test
    fun migrate2To3_addsChapterTable() {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 2).apply {
            execSQL(
                """
                INSERT INTO novels (id, title, description, word_count, cover_color, is_biometric_locked, created_at, updated_at)
                VALUES (1, 'Test Novel', 'Description', 1000, '#6650A4', 0, 1000000, 1000000)
                """
            )
            close()
        }

        val db = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            3,
            true,
            Migration_2_3()
        )

        // Verify chapters table exists
        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='chapters'")
        cursor.moveToFirst()
        val tableName = cursor.getString(0)
        assert(tableName == "chapters")
    }

    @Test
    fun migrate3To4_addsCharacterTable() {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 3).apply {
            execSQL(
                """
                INSERT INTO novels (id, title, description, word_count, cover_color, is_biometric_locked, created_at, updated_at)
                VALUES (1, 'Test Novel', 'Description', 1000, '#6650A4', 0, 1000000, 1000000)
                """
            )
            execSQL(
                """
                INSERT INTO chapters (id, novel_id, title, content, word_count, position, created_at, updated_at)
                VALUES (1, 1, 'Chapter 1', 'Content here', 500, 0, 1000000, 1000000)
                """
            )
            close()
        }

        val db = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            4,
            true,
            Migration_3_4()
        )

        val cursor = db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='characters'")
        cursor.moveToFirst()
        val tableName = cursor.getString(0)
        assert(tableName == "characters")
    }

    @Test
    fun allMigrations_handleDataPreservation() {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 1).apply {
            execSQL(
                """
                INSERT INTO novels (id, title, description, cover_color, is_biometric_locked, created_at, updated_at)
                VALUES (1, 'Preserved Novel', 'Should survive migrations', '#6650A4', 0, 1000000, 1000000)
                """
            )
            close()
        }

        val db = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB_NAME,
            4,
            true,
            Migration_1_2(),
            Migration_2_3(),
            Migration_3_4()
        )

        val cursor = db.query("SELECT title FROM novels WHERE id = 1")
        cursor.moveToFirst()
        val title = cursor.getString(0)
        assert(title == "Preserved Novel")
    }
}

class Migration_1_2 : Migration(1, 2) {
    override fun migrate(database: android.database.sqlite.SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE novels ADD COLUMN word_count INTEGER NOT NULL DEFAULT 0")
    }
}

class Migration_2_3 : Migration(2, 3) {
    override fun migrate(database: android.database.sqlite.SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS chapters (
                id INTEGER PRIMARY KEY NOT NULL,
                novel_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL DEFAULT '',
                word_count INTEGER NOT NULL DEFAULT 0,
                position INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE
            )
            """
        )
    }
}

class Migration_3_4 : Migration(3, 4) {
    override fun migrate(database: android.database.sqlite.SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS characters (
                id INTEGER PRIMARY KEY NOT NULL,
                novel_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                color TEXT NOT NULL DEFAULT '#5C6BC0',
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                FOREIGN KEY (novel_id) REFERENCES novels(id) ON DELETE CASCADE
            )
            """
        )
    }
}
