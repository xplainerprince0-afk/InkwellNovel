package com.inkwell.core.data.repository

import com.inkwell.core.data.local.dao.WorldNoteDao
import com.inkwell.core.data.repository.mapper.toDomain
import com.inkwell.core.data.repository.mapper.toEntity
import com.inkwell.core.data.repository.model.WorldNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorldNoteRepositoryImpl @Inject constructor(
    private val worldNoteDao: WorldNoteDao
) : WorldNoteRepository {

    override fun getWorldNotesByNovelId(novelId: Long): Flow<List<WorldNote>> {
        return worldNoteDao.getWorldNotesByNovelId(novelId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWorldNotesByCategory(novelId: Long, category: String): Flow<List<WorldNote>> {
        return worldNoteDao.getWorldNotesByCategory(novelId, category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createWorldNote(
        novelId: Long,
        title: String,
        content: String,
        category: String,
        latitude: Double?,
        longitude: Double?
    ): Long {
        val worldNote = WorldNote(
            novelId = novelId,
            title = title,
            content = content,
            category = category,
            latitude = latitude,
            longitude = longitude
        )
        return worldNoteDao.insert(worldNote.toEntity())
    }

    override suspend fun updateWorldNote(worldNote: WorldNote) {
        worldNoteDao.update(worldNote.toEntity())
    }

    override suspend fun deleteWorldNote(worldNote: WorldNote) {
        worldNoteDao.delete(worldNote.toEntity())
    }
}
