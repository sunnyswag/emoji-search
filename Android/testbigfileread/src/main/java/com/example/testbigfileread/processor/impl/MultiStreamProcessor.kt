package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiJsonEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.example.testbigfileread.utils.toBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream

class MultiStreamProcessor : IProcessor {

    override val processorType = ProcessorType.EACH_LINE_WITH_CHANNEL_AND_MULTI_STREAM_PROCESSOR

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        var index = 0
        val mutex = Mutex()

        List(STREAM_SIZE) { i ->
            flow {
                val resId = getEmbeddingResId(i)
                context.resources.openRawResource(resId).use { inputStream ->
                    GZIPInputStream(inputStream).use { gzipInputStream ->
                        gzipInputStream.bufferedReader().useLines { lines ->
                            for (line in lines) {
                                emit(line)
                            }
                        }
                    }
                }
            }.flowOn(Dispatchers.IO)
        }.asFlow()
            .flattenMerge(STREAM_SIZE)
            .collect { data ->
                data.toBean<EmojiJsonEntity>()?.let { emojiJsonEntity ->
                    mutex.withLock {
                        emojiInfoData[index].emoji = emojiJsonEntity.emoji
                        emojiInfoData[index].message = emojiJsonEntity.message
                        emojiEmbeddings[index] = mk.ndarray(emojiJsonEntity.embed)
                        index++
                    }
                }
            }
    }

    private fun getEmbeddingResId(i: Int) = when (i) {
        0 -> R.raw.emoji_embeddings_0
        1 -> R.raw.emoji_embeddings_1
        2 -> R.raw.emoji_embeddings_2
        3 -> R.raw.emoji_embeddings_3
        4 -> R.raw.emoji_embeddings_4
        else -> throw IllegalArgumentException("Invalid index: $i")
    }

    companion object {
        private const val TAG = "MultiStreamProcessor"
        private const val STREAM_SIZE = 5
    }
}