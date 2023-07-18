package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiEmbeddingEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.example.testbigfileread.database.getEmbeddingEntityDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.GZIPInputStream

class JsonToDatabaseProcessor : IProcessor {

    private val gson = Gson()

    override val processorType: ProcessorType
        get() = ProcessorType.JSON_TO_DATABASE_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.IO) {
        val embeddingDao = getEmbeddingEntityDao(context)
        context.resources.openRawResource(R.raw.emoji_embeddings_json).use { inputStream ->
            GZIPInputStream(inputStream).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val entity = gson.fromJson(line, EmojiEmbeddingEntity::class.java)
                    embeddingDao.insert(entity)
                }
            }
        }
    }
}
