package com.inkwell.core.data.repository.mapper

import com.inkwell.core.data.local.entity.NovelEntity
import com.inkwell.core.data.repository.model.Novel

fun NovelEntity.toDomain(): Novel {
    return Novel(
        id = id,
        title = title,
        description = description,
        coverColor = coverColor,
        createdAt = createdAt,
        updatedAt = updatedAt,
        wordCount = wordCount,
        isLocked = isLocked,
        syncStatus = syncStatus
    )
}

fun Novel.toEntity(): NovelEntity {
    return NovelEntity(
        id = id,
        title = title,
        description = description,
        coverColor = coverColor,
        createdAt = createdAt,
        updatedAt = updatedAt,
        wordCount = wordCount,
        isLocked = isLocked,
        syncStatus = syncStatus
    )
}
