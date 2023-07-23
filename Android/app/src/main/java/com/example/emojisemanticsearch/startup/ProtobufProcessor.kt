package com.example.emojisemanticsearch.startup

import EmojiEmbeddingOuterClass
import android.content.Context
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

class ProtobufProcessor {

    private var index = AtomicInteger(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        flow {
            context.resources.openRawResource(R.raw.emoji_embeddings_proto).use { inputStream ->
                GZIPInputStream(inputStream).buffered().use { gzipInputStream ->
                    while (true) {
                        EmojiEmbeddingOuterClass.EmojiEmbedding.parseDelimitedFrom(gzipInputStream)?.let {
                            emit(it)
                        } ?: break
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
            .buffer()
            .flatMapMerge { byteArray ->
                flow { emit(readEmojiData(byteArray)) }
            }.collect {}
    }

    private fun readEmojiData(entity: EmojiEmbeddingOuterClass.EmojiEmbedding) {
        val currentIdx = index.getAndIncrement()
        emojiInfoData[currentIdx].emoji = entity.emoji
        emojiInfoData[currentIdx].message = entity.message
        emojiEmbeddings[currentIdx] = mk.ndarray(entity.embedList)
    }
}