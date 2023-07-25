package com.example.emoji_data_reader.processor.impl

import android.content.Context
import com.example.emoji_data_reader.R
import com.example.emoji_data_reader.processor.IProcessor
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiEmbeddings
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import com.example.model.EmojiEmbeddingEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream

internal class DefaultProcessor : IProcessor {

    private val gson = Gson()

    override val processorType = ProcessorType.DEFAULT_JSON_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.IO) {
        context.resources.openRawResource(R.raw.emoji_embeddings_json).use { inputStream ->
            GZIPInputStream(inputStream).bufferedReader().use { bufferedReader ->
                bufferedReader.readLines().forEachIndexed { index, line ->
                    val entity = gson.fromJson(line, EmojiEmbeddingEntity::class.java)
                    emojiInfoData[index].emoji = entity.emoji
                    emojiInfoData[index].message = entity.message
                    emojiEmbeddings[index] = mk.ndarray(entity.embed)
                }
            }
        }
    }
}