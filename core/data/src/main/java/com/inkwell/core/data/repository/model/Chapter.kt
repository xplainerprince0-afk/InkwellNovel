package com.inkwell.core.data.repository.model

data class Chapter(
    val id: Long = 0,
    val novelId: Long,
    val title: String,
    val content: String = "",
    val position: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val wordCount: Int = 0
)
