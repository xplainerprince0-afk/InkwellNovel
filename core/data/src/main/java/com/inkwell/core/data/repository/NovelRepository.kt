package com.inkwell.core.data.repository

import com.inkwell.core.data.repository.model.Novel
import kotlinx.coroutines.flow.Flow

interface NovelRepository {

    fun getNovels(): Flow<List<Novel>>

    fun getNovelById(id: Long): Flow<Novel?>

    suspend fun createNovel(title: String, description: String): Long

    suspend fun updateNovel(novel: Novel)

    suspend fun deleteNovel(novel: Novel)

    suspend fun updateWordCount(novelId: Long, wordCount: Int)
}
