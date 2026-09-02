package com.inkwell.core.data.repository.mapper

import com.inkwell.core.data.local.entity.WorldNoteEntity
import com.inkwell.core.data.repository.model.WorldNote

fun WorldNoteEntity.toDomain(): WorldNote {
    return WorldNote(
        id = id,
        novelId = novelId,
        title = title,
        content = content,
        category = category,
        latitude = latitude,
        longitude = longitude
    )
}

fun WorldNote.toEntity(): WorldNoteEntity {
    return WorldNoteEntity(
        id = id,
        novelId = novelId,
        title = title,
        content = content,
        category = category,
        latitude = latitude,
        longitude = longitude
    )
}
