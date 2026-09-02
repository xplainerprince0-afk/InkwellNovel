package com.inkwell.core.data.repository.model

data class WorldNote(
    val id: Long = 0,
    val novelId: Long,
    val title: String,
    val content: String = "",
    val category: String = "other",
    val latitude: Double? = null,
    val longitude: Double? = null
)
