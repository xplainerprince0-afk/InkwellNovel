package com.inkwell.core.data.repository.mapper

import com.inkwell.core.data.local.entity.CharacterEntity
import com.inkwell.core.data.repository.model.Character

fun CharacterEntity.toDomain(): Character {
    return Character(
        id = id,
        novelId = novelId,
        name = name,
        description = description,
        role = role,
        notes = notes,
        imageUrl = imageUrl
    )
}

fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        novelId = novelId,
        name = name,
        description = description,
        role = role,
        notes = notes,
        imageUrl = imageUrl
    )
}
