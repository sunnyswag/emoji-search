package com.example.emoji_data_reader.processor

import android.content.Context
import com.example.emoji_data_reader.processor.impl.DatabaseLoadProcessor
import com.example.emoji_data_reader.processor.impl.EachLineProcessor
import com.example.emoji_data_reader.processor.impl.DefaultProcessor
import com.example.emoji_data_reader.processor.impl.JsonToProtoBufProcessor
import com.example.emoji_data_reader.processor.impl.MultiStreamProcessor
import com.example.emoji_data_reader.processor.impl.ProtobufProcessor
import com.example.emoji_data_reader.processor.impl.JsonToDatabaseProcessor
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros

object ProcessorFactory {

    private val processors = listOf(
        DefaultProcessor(),
        EachLineProcessor(),
        MultiStreamProcessor(),
        ProtobufProcessor(),
        JsonToProtoBufProcessor(),
        JsonToDatabaseProcessor(),
        DatabaseLoadProcessor()
    )

    suspend fun doProcess(context: Context, processorType: ProcessorType, rawFileIds: List<Int>) {
        processors.find { it.processorType == processorType }?.process(context, rawFileIds)
            ?: throw ProcessorNotFoundException("the $processorType processor not support!")
    }

    const val EMOJI_EMBEDDING_SIZE = 3753
    const val EMBEDDING_LENGTH_PER_EMOJI = 1536

    // size: 3753, 1536
    val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
    val emojiInfoData: List<com.example.model.EmojiInfoEntity> = List(EMOJI_EMBEDDING_SIZE) {
        com.example.model.EmojiInfoEntity("", "")
    }
}