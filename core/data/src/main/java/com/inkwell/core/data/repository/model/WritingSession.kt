package com.inkwell.core.data.repository.model

data class WritingSession(
    val id: Long = 0,
    val novelId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val wordsWritten: Int = 0,
    val location: String? = null
)
