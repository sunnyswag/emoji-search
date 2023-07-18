package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.database.getEmbeddingEntityDao
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set

class DatabaseLoadProcessor: IProcessor {
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