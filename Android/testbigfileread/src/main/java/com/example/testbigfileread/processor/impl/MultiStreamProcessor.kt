package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiJsonEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.google.gson.Gson
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

    private val gson = Gson()

    override val processorType = ProcessorType.JSON_EACH_LINE_MULTI_STREAM_PROCESSOR

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
                val entity = gson.fromJson(data, EmojiJsonEntity::class.java)
                mutex.withLock {
                    emojiInfoData[index].emoji = entity.emoji
                    emojiInfoData[index].message = entity.message
                    emojiEmbeddings[index] = mk.ndarray(entity.embed)
                    index++
                }
            }
    }

    private fun getEmbeddingResId(i: Int) = when (i) {
        0 -> R.raw.emoji_embeddings_json_0
        1 -> R.raw.emoji_embeddings_json_1
        2 -> R.raw.emoji_embeddings_json_2
        3 -> R.raw.emoji_embeddings_json_3
        4 -> R.raw.emoji_embeddings_json_4
        else -> throw IllegalArgumentException("Invalid index: $i")
    }

    companion object {
        private const val TAG = "MultiStreamProcessor"
        private const val STREAM_SIZE = 5
    }
}