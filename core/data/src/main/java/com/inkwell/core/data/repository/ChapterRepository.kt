package com.inkwell.core.data.repository

import com.inkwell.core.data.repository.model.Chapter
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {

    fun getChaptersByNovelId(novelId: Long): Flow<List<Chapter>>

    fun getChapterById(id: Long): Flow<Chapter?>

    suspend fun createChapter(novelId: Long, title: String, content: String = ""): Long

    suspend fun updateChapter(chapter: Chapter)

    suspend fun deleteChapter(chapter: Chapter)

    suspend fun getNextPosition(novelId: Long): Int
}
