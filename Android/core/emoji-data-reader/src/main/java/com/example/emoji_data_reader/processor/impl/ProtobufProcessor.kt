package com.example.emoji_data_reader.processor.impl

import android.content.Context
import com.example.emoji_data_reader.R
import com.example.emoji_data_reader.processor.IProcessor
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiEmbeddings
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
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

internal class ProtobufProcessor: IProcessor {

    private var index = AtomicInteger(0)
    override val processorType = ProcessorType.PROTOBUF_PROCESSOR

    @OptIn(FlowPreview::class)
    override suspend fun process(context: Context, rawFileIds: List<Int>) = withContext(Dispatchers.Default) {
        flow {
            context.resources.openRawResource(rawFileIds.first()).use { inputStream ->
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