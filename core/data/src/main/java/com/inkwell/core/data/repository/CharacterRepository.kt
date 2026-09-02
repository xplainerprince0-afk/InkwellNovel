package com.inkwell.core.data.repository

import com.inkwell.core.data.repository.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharactersByNovelId(novelId: Long): Flow<List<Character>>

    suspend fun createCharacter(
        novelId: Long,
        name: String,
        description: String = "",
        role: String = "supporting",
        notes: String = "",
        imageUrl: String? = null
    ): Long

    suspend fun updateCharacter(character: Character)

    suspend fun deleteCharacter(character: Character)
}
