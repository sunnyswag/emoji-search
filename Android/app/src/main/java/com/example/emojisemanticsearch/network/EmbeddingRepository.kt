package com.example.emojisemanticsearch.network

import android.util.Log
import com.example.emojisemanticsearch.entity.EmbeddingRequest
import com.example.emojisemanticsearch.entity.EmbeddingResponse
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.EMBEDDING_LENGTH_PER_EMOJI
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.EMOJI_EMBEDDING_SIZE
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiData
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import java.lang.Float.max
import java.lang.Float.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class EmbeddingRepository(private val openAIAPI: OpenAIAPI) {

    @OptIn(ExperimentalTime::class)
    suspend fun getEmbedding(userInput: String, topK: Int = 20): Result<List<Int>?> {
        return kotlin.runCatching {
            var networkResult: EmbeddingResponse
            measureTime { networkResult = openAIAPI.getEmbedding(EmbeddingRequest(userInput)) }
                .also { Log.d(TAG, "getEmbedding network time: $it") }
            networkResult.embeddings.firstOrNull()?.embedding?.let { embedding ->
                if (embedding.size == EMBEDDING_LENGTH_PER_EMOJI) {
                    var result: List<Int>?
                    measureTime {
                        val embeddingReshaped =
                            mk.ndarray(embedding).reshape(EMBEDDING_LENGTH_PER_EMOJI, 1)
                        val dotResult = emojiEmbeddings.dot(embeddingReshaped).flatten().toList()
                        result = topKIndices(dotResult, getScaledTopK(topK))
                    }.also { Log.d(TAG, "getEmbedding process time: $it") }
                    result
                } else {
                    Log.e(TAG, "embedding size is out of range, size: ${embedding.size}")
                    null
                }
            }
        }
    }

    companion object {
        private const val TAG = "EmbeddingRepository"

        fun topKIndices(list: List<Float>, k: Int): List<Int> {
            val indices = List(list.size) { index -> index }
            return indices.sortedByDescending { list[it] }.take(k)
        }

        fun getScaledTopK(topK: Int): Int {
            val scale = min(1f, max(0.2f, emojiData.size / EMOJI_EMBEDDING_SIZE.toFloat()))
            return (topK * scale).toInt().also {
                Log.d(TAG, "scaled topK: $it, topK: $topK, scale: $scale")
            }
        }
    }
}