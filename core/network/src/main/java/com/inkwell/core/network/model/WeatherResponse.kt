package com.inkwell.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val temperature: Double,
    val description: String,
    val icon: String
)
