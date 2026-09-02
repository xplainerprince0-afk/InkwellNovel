package com.inkwell.core.data.repository

import com.inkwell.core.data.local.dao.NovelDao
import com.inkwell.core.data.repository.mapper.toDomain
import com.inkwell.core.data.repository.mapper.toEntity
import com.inkwell.core.data.repository.model.Novel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovelRepositoryImpl @Inject constructor(
    private val novelDao: NovelDao
) : NovelRepository {

    override fun getNovels(): Flow<List<Novel>> {
        return novelDao.getAllNovels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNovelById(id: Long): Flow<Novel?> {
        return novelDao.getNovelById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun createNovel(title: String, description: String): Long {
        val novel = Novel(
            title = title,
            description = description,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return novelDao.insert(novel.toEntity())
    }

    override suspend fun updateNovel(novel: Novel) {
        val updatedNovel = novel.copy(updatedAt = System.currentTimeMillis())
        novelDao.update(updatedNovel.toEntity())
    }

    override suspend fun deleteNovel(novel: Novel) {
        novelDao.delete(novel.toEntity())
    }

    override suspend fun updateWordCount(novelId: Long, wordCount: Int) {
        novelDao.updateWordCount(novelId, wordCount)
    }
}
