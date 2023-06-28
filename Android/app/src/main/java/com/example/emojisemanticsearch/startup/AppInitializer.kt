package com.example.emojisemanticsearch.startup

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.entity.EmojiJsonEntity
import com.example.emojisemanticsearch.utils.toBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import java.util.zip.GZIPInputStream
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class AppInitializer : Initializer<Unit> {

    private val initializerScope = CoroutineScope(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    override fun create(context: Context) {
        initializerScope.launch {
            val timeSpend = measureTime { readEmojiEmbeddings(context) }
            Log.d(TAG, "read emoji embeddings in $timeSpend")
            if (emojiData.size != EMOJI_EMBEDDING_SIZE || emojiEmbeddings.size != EMOJI_EMBEDDING_SIZE) {
                Log.e(TAG, "emoji data or emoji embedding size is not correct, " +
                        "emoji data size: ${emojiData.size}, emoji embedding size: ${emojiEmbeddings.size}")
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    @SuppressLint("DiscouragedApi")
    private suspend fun readEmojiEmbeddings(context: Context) {
        fun processChunkedEmojis(chunkedLines: List<String>) =
            initializerScope.async(Dispatchers.IO) {
                mutex.withLock {
                    chunkedLines.forEach {
                        it.toBean<EmojiJsonEntity>()?.let { emojiJsonEntity ->
                            emojiData.add(
                                EmojiEntity(
                                    emojiJsonEntity.emoji,
                                    emojiJsonEntity.message
                                )
                            )
                            emojiEmbeddings.append(mk.ndarray(emojiJsonEntity.embed))
                        }
                    }
                }
            }

        context.resources.openRawResource(R.raw.emoji_embeddings).use { inputStream ->
            GZIPInputStream(inputStream).use { gzipInputStream ->
                gzipInputStream.bufferedReader().useLines { lines ->
                    lines.chunked(READING_CHUNK) { chunkedLines ->
                        processChunkedEmojis(chunkedLines)
                    }.forEach {
                        it.await()
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "AppInitializer"
        const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 1536
        const val READING_CHUNK = 5
        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiData: MutableList<EmojiEntity> = mutableListOf()
        val mutex = Mutex()
    }
}