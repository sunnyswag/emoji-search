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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

class EachLineProcessor : IProcessor {

    private val gson = Gson()

    override val processorType = ProcessorType.EACH_LINE_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        val index = AtomicInteger(0)

        flow {
            context.resources.openRawResource(R.raw.emoji_embeddings).use { inputStream ->
                GZIPInputStream(inputStream).use { gzipInputStream ->
                    gzipInputStream.bufferedReader().useLines { lines ->
                        for (line in lines) {
                            emit(line)
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                val i = index.getAndIncrement()
                val entity = gson.fromJson(it, EmojiJsonEntity::class.java)
                emojiInfoData[i].emoji = entity.emoji
                emojiInfoData[i].message = entity.message
                emojiEmbeddings[i] = mk.ndarray(entity.embed)
            }
    }
}