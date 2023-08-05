package com.example.emoji_data_reader.processor.impl

import android.content.Context
import com.example.model.EmojiEmbeddingEntity
import com.example.emoji_data_reader.processor.IProcessor
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiEmbeddings
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
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

internal class MultiStreamProcessor : IProcessor {

    private val gson = Gson()

    override val processorType = ProcessorType.JSON_EACH_LINE_MULTI_STREAM_PROCESSOR

    override suspend fun process(context: Context, rawFileIds: List<Int>) = withContext(Dispatchers.Default) {
        var index = 0
        val mutex = Mutex()

        List(STREAM_SIZE) { i ->
            flow {
                context.resources.openRawResource(rawFileIds[i]).use { inputStream ->
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
                val entity = gson.fromJson(data, EmojiEmbeddingEntity::class.java)
                mutex.withLock {
                    emojiInfoData[index].emoji = entity.emoji
                    emojiInfoData[index].message = entity.message
                    emojiEmbeddings[index] = mk.ndarray(entity.embed)
                    index++
                }
            }
    }

    companion object {
        private const val TAG = "MultiStreamProcessor"
        private const val STREAM_SIZE = 5
    }
}