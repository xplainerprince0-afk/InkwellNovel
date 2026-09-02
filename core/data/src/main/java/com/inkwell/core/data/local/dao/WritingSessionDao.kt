package com.inkwell.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.inkwell.core.data.local.entity.WritingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WritingSessionDao {

    @Query("SELECT * FROM writing_sessions WHERE novelId = :novelId ORDER BY startTime DESC")
    fun getSessionsByNovelId(novelId: Long): Flow<List<WritingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WritingSessionEntity): Long

    @Update
    suspend fun update(session: WritingSessionEntity)

    @Query("SELECT COALESCE(SUM(wordsWritten), 0) FROM writing_sessions")
    fun getTotalWordsWritten(): Flow<Int>
}
