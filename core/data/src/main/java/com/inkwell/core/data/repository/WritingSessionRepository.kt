package com.inkwell.core.data.repository

import com.inkwell.core.data.repository.model.WritingSession
import kotlinx.coroutines.flow.Flow

interface WritingSessionRepository {

    fun getSessionsByNovelId(novelId: Long): Flow<List<WritingSession>>

    suspend fun startSession(novelId: Long, location: String? = null): Long

    suspend fun endSession(sessionId: Long, wordsWritten: Int)

    suspend fun updateSession(session: WritingSession)

    fun getTotalWordsWritten(): Flow<Int>
}
