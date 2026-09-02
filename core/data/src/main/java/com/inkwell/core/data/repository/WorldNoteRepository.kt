package com.inkwell.core.data.repository

import com.inkwell.core.data.repository.model.WorldNote
import kotlinx.coroutines.flow.Flow

interface WorldNoteRepository {

    fun getWorldNotesByNovelId(novelId: Long): Flow<List<WorldNote>>

    fun getWorldNotesByCategory(novelId: Long, category: String): Flow<List<WorldNote>>

    suspend fun createWorldNote(
        novelId: Long,
        title: String,
        content: String = "",
        category: String = "other",
        latitude: Double? = null,
        longitude: Double? = null
    ): Long

    suspend fun updateWorldNote(worldNote: WorldNote)

    suspend fun deleteWorldNote(worldNote: WorldNote)
}
