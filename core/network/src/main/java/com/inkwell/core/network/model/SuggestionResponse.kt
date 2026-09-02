package com.inkwell.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SuggestionResponse(
    val suggestions: List<String>
)
