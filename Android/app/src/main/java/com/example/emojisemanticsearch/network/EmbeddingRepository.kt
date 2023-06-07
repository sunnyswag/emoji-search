package com.example.emojisemanticsearch.network

class EmbeddingRepository(private val openAIAPI: OpenAIAPI) {
    suspend fun getEmbedding(text: String) = openAIAPI.getEmbedding(text)
}