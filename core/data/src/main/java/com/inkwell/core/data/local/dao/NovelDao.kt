package com.inkwell.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.inkwell.core.data.local.entity.NovelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NovelDao {

    @Query("SELECT * FROM novels ORDER BY updatedAt DESC")
    fun getAllNovels(): Flow<List<NovelEntity>>

    @Query("SELECT * FROM novels WHERE id = :id")
    fun getNovelById(id: Long): Flow<NovelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(novel: NovelEntity): Long

    @Update
    suspend fun update(novel: NovelEntity)

    @Delete
    suspend fun delete(novel: NovelEntity)

    @Query("UPDATE novels SET wordCount = :wordCount WHERE id = :novelId")
    suspend fun updateWordCount(novelId: Long, wordCount: Int)
}
