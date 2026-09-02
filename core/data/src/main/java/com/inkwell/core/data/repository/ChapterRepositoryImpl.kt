package com.inkwell.core.data.repository

import com.inkwell.core.data.local.dao.ChapterDao
import com.inkwell.core.data.repository.mapper.toDomain
import com.inkwell.core.data.repository.mapper.toEntity
import com.inkwell.core.data.repository.model.Chapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepositoryImpl @Inject constructor(
    private val chapterDao: ChapterDao
) : ChapterRepository {

    override fun getChaptersByNovelId(novelId: Long): Flow<List<Chapter>> {
        return chapterDao.getChaptersByNovelId(novelId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChapterById(id: Long): Flow<Chapter?> {
        return chapterDao.getChapterById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun createChapter(novelId: Long, title: String, content: String): Long {
        val position = chapterDao.getNextPosition(novelId)
        val chapter = Chapter(
            novelId = novelId,
            title = title,
            content = content,
            position = position,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            wordCount = content.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        )
        return chapterDao.insert(chapter.toEntity())
    }

    override suspend fun updateChapter(chapter: Chapter) {
        val wordCount = chapter.content.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        val updatedChapter = chapter.copy(
            updatedAt = System.currentTimeMillis(),
            wordCount = wordCount
        )
        chapterDao.update(updatedChapter.toEntity())
    }

    override suspend fun deleteChapter(chapter: Chapter) {
        chapterDao.delete(chapter.toEntity())
    }

    override suspend fun getNextPosition(novelId: Long): Int {
        return chapterDao.getNextPosition(novelId)
    }
}
