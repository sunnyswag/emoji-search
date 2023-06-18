package com.example.emojisemanticsearch.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIAPI {

    @Headers(
        "Content-Type:application/json",
        "Authorization:Bearer API_KEY"
    )
    @POST("v1/embeddings")
    suspend fun getEmbedding(@Body request: String): String
}