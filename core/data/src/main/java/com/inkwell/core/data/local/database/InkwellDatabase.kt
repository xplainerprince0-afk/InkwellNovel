package com.inkwell.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inkwell.core.data.local.dao.ChapterDao
import com.inkwell.core.data.local.dao.CharacterDao
import com.inkwell.core.data.local.dao.NovelDao
import com.inkwell.core.data.local.dao.WritingSessionDao
import com.inkwell.core.data.local.dao.WorldNoteDao
import com.inkwell.core.data.local.entity.ChapterEntity
import com.inkwell.core.data.local.entity.CharacterEntity
import com.inkwell.core.data.local.entity.NovelEntity
import com.inkwell.core.data.local.entity.WritingSessionEntity
import com.inkwell.core.data.local.entity.WorldNoteEntity

@Database(
    entities = [
        NovelEntity::class,
        ChapterEntity::class,
        CharacterEntity::class,
        WorldNoteEntity::class,
        WritingSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InkwellDatabase : RoomDatabase() {

    abstract fun novelDao(): NovelDao

    abstract fun chapterDao(): ChapterDao

    abstract fun characterDao(): CharacterDao

    abstract fun worldNoteDao(): WorldNoteDao

    abstract fun writingSessionDao(): WritingSessionDao

    companion object {
        const val DATABASE_NAME = "inkwell_database"
    }
}
