package com.example.emoji_data_reader.processor.impl

import android.content.Context
import com.example.emoji_data_reader.database.getEmbeddingEntityDao
import com.example.emoji_data_reader.processor.IProcessor
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiEmbeddings
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set

internal class DatabaseLoadProcessor: IProcessor {
    override val processorType: ProcessorType
        get() = ProcessorType.DATABASE_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.IO) {
        val embeddingDao = getEmbeddingEntityDao(context)
        embeddingDao.queryAll()?.forEachIndexed { index, emojiEmbeddingEntity ->
            emojiInfoData[index].emoji = emojiEmbeddingEntity.emoji
            emojiInfoData[index].message = emojiEmbeddingEntity.message
            emojiEmbeddings[index] = mk.ndarray(emojiEmbeddingEntity.embed)
        }

        Unit
    }
}