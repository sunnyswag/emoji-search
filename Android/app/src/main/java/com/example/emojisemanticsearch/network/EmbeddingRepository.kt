package com.example.emojisemanticsearch.network

import android.util.Log
import com.example.emojisemanticsearch.entity.EmbeddingRequest
import com.example.emojisemanticsearch.entity.EmojiEntity

class EmbeddingRepository(private val openAIAPI: OpenAIAPI) {
    suspend fun getEmbedding(
        text: EmbeddingRequest = EmbeddingRequest("I love you")
    ): List<EmojiEntity> {
        kotlin.runCatching {
            openAIAPI.getEmbedding(text).embeddings.firstOrNull()?.let { embedding ->
                Log.d(TAG, "getEmbedding: $embedding")

            }
        }

        return listOf(
            EmojiEntity(
                emoji = text.input,
                message = text.input
            )
        )
    }

    companion object {
        private const val TAG = "EmbeddingRepository"
    }
}