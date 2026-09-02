package com.inkwell.core.data.repository.mapper

import com.inkwell.core.data.local.entity.ChapterEntity
import com.inkwell.core.data.repository.model.Chapter

fun ChapterEntity.toDomain(): Chapter {
    return Chapter(
        id = id,
        novelId = novelId,
        title = title,
        content = content,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt,
        wordCount = wordCount
    )
}

fun Chapter.toEntity(): ChapterEntity {
    return ChapterEntity(
        id = id,
        novelId = novelId,
        title = title,
        content = content,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt,
        wordCount = wordCount
    )
}
