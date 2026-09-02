package com.inkwell.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SuggestionRequest(
    val text: String,
    val type: String
)
