package com.example.testbigfileread.processor.impl

import EmojiEmbeddingOuterClass
import android.content.Context
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.R
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
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

class ProtobufProcessor: IProcessor {

    private var index = AtomicInteger(0)
    override val processorType = ProcessorType.PROTOBUF_PROCESSOR

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {
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

    companion object {
        private const val TAG = "ProtobufProcessor"
    }
}