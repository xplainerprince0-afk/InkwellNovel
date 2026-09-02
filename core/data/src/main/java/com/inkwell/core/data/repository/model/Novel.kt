package com.inkwell.core.data.repository.model

data class Novel(
    val id: Long = 0,
    val title: String,
    val description: String,
    val coverColor: Int = 0xFF6200EE.toInt(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val wordCount: Int = 0,
    val isLocked: Boolean = false,
    val syncStatus: String = "SYNCED"
)
