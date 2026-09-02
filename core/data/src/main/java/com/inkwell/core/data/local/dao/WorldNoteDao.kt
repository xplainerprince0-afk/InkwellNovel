package com.inkwell.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.inkwell.core.data.local.entity.WorldNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldNoteDao {

    @Query("SELECT * FROM world_notes WHERE novelId = :novelId ORDER BY title ASC")
    fun getWorldNotesByNovelId(novelId: Long): Flow<List<WorldNoteEntity>>

    @Query("SELECT * FROM world_notes WHERE novelId = :novelId AND category = :category ORDER BY title ASC")
    fun getWorldNotesByCategory(novelId: Long, category: String): Flow<List<WorldNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldNote: WorldNoteEntity): Long

    @Update
    suspend fun update(worldNote: WorldNoteEntity)

    @Delete
    suspend fun delete(worldNote: WorldNoteEntity)
}
