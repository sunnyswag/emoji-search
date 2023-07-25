package com.example.emojisemanticsearch.network

import com.example.emojisemanticsearch.BuildConfig
import com.example.emojisemanticsearch.network.entity.EmbeddingRequest
import com.example.emojisemanticsearch.network.entity.EmbeddingResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIAPI {

    @Headers(
        "Content-Type:application/json",
        "Authorization:Bearer ${BuildConfig.API_KEY}"
    )
    @POST("v1/embeddings")
    suspend fun getEmbedding(@Body request: EmbeddingRequest): EmbeddingResponse
}