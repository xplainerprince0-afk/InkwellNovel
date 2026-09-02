package com.inkwell.core.data.repository

import com.inkwell.core.data.local.dao.WritingSessionDao
import com.inkwell.core.data.repository.mapper.toDomain
import com.inkwell.core.data.repository.mapper.toEntity
import com.inkwell.core.data.repository.model.WritingSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WritingSessionRepositoryImpl @Inject constructor(
    private val writingSessionDao: WritingSessionDao
) : WritingSessionRepository {

    override fun getSessionsByNovelId(novelId: Long): Flow<List<WritingSession>> {
        return writingSessionDao.getSessionsByNovelId(novelId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun startSession(novelId: Long, location: String?): Long {
        val session = WritingSession(
            novelId = novelId,
            startTime = System.currentTimeMillis(),
            location = location
        )
        return writingSessionDao.insert(session.toEntity())
    }

    override suspend fun endSession(sessionId: Long, wordsWritten: Int) {
        val session = WritingSession(
            id = sessionId,
            novelId = 0,
            startTime = 0,
            endTime = System.currentTimeMillis(),
            wordsWritten = wordsWritten
        )
        writingSessionDao.update(session.toEntity())
    }

    override suspend fun updateSession(session: WritingSession) {
        writingSessionDao.update(session.toEntity())
    }

    override fun getTotalWordsWritten(): Flow<Int> {
        return writingSessionDao.getTotalWordsWritten()
    }
}
