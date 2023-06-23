package com.example.emojisemanticsearch.network

import android.util.Log
import com.example.emojisemanticsearch.entity.EmbeddingRequest
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.EMBEDDING_LENGTH_PER_EMOJI
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class EmbeddingRepository(private val openAIAPI: OpenAIAPI) {
    @OptIn(ExperimentalTime::class)
    suspend fun getEmbedding(userInput: String): List<EmojiEntity> {
        kotlin.runCatching {
            val networkResult = openAIAPI.getEmbedding(EmbeddingRequest(userInput))
            networkResult.embeddings.firstOrNull()?.embedding?.let { embedding ->
                if (embedding.size == EMBEDDING_LENGTH_PER_EMOJI) {
                    measureTime {
                        val embeddingReshaped = mk.ndarray(embedding).reshape(EMBEDDING_LENGTH_PER_EMOJI, 1)
                        val xxx = emojiEmbeddings.dot(embeddingReshaped)
                        Log.d(TAG, "embeddingReshaped shape: ${xxx.shape[0]}, ${xxx.shape[1]}")
                    }.let {
                        Log.d(TAG, "embeddingReshaped time: $it")
                    }
                } else {
                    Log.e(TAG, "embedding size is out of range, size: ${embedding.size}")
                }
            }
        }

        return listOf(
            EmojiEntity(
                emoji = userInput,
                message = userInput
            )
        )
    }

    companion object {
        private const val TAG = "EmbeddingRepository"
    }
}