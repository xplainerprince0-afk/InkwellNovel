package com.inkwell.core.data.repository.model

data class Character(
    val id: Long = 0,
    val novelId: Long,
    val name: String,
    val description: String = "",
    val role: String = "supporting",
    val notes: String = "",
    val imageUrl: String? = null
)
