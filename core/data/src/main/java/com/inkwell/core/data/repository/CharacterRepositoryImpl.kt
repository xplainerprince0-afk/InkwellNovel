package com.inkwell.core.data.repository

import com.inkwell.core.data.local.dao.CharacterDao
import com.inkwell.core.data.repository.mapper.toDomain
import com.inkwell.core.data.repository.mapper.toEntity
import com.inkwell.core.data.repository.model.Character
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val characterDao: CharacterDao
) : CharacterRepository {

    override fun getCharactersByNovelId(novelId: Long): Flow<List<Character>> {
        return characterDao.getCharactersByNovelId(novelId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createCharacter(
        novelId: Long,
        name: String,
        description: String,
        role: String,
        notes: String,
        imageUrl: String?
    ): Long {
        val character = Character(
            novelId = novelId,
            name = name,
            description = description,
            role = role,
            notes = notes,
            imageUrl = imageUrl
        )
        return characterDao.insert(character.toEntity())
    }

    override suspend fun updateCharacter(character: Character) {
        characterDao.update(character.toEntity())
    }

    override suspend fun deleteCharacter(character: Character) {
        characterDao.delete(character.toEntity())
    }
}
