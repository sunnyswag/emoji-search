package com.example.emojisemanticsearch.network

import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIAPI {
    @POST("engines/davinci/completions")
    suspend fun getEmbedding(@Body text: String): String
}