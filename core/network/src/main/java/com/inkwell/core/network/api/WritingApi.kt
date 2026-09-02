package com.inkwell.core.network.api

import com.inkwell.core.network.model.SuggestionRequest
import com.inkwell.core.network.model.SuggestionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WritingApi {

    @POST("writing/suggest")
    suspend fun getSuggestions(@Body request: SuggestionRequest): SuggestionResponse
}
