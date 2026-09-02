package com.inkwell.core.data.repository.mapper

import com.inkwell.core.data.local.entity.WritingSessionEntity
import com.inkwell.core.data.repository.model.WritingSession

fun WritingSessionEntity.toDomain(): WritingSession {
    return WritingSession(
        id = id,
        novelId = novelId,
        startTime = startTime,
        endTime = endTime,
        wordsWritten = wordsWritten,
        location = location
    )
}

fun WritingSession.toEntity(): WritingSessionEntity {
    return WritingSessionEntity(
        id = id,
        novelId = novelId,
        startTime = startTime,
        endTime = endTime,
        wordsWritten = wordsWritten,
        location = location
    )
}
