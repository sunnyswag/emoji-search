package com.example.testbigfileread.processor.impl

import EmojiEmbeddingOuterClass
import android.content.Context
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiJsonEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class JsonToProtoBufProcessor: IProcessor {

    private val gson = Gson()
    private val pbEntityCollection = mutableListOf<EmojiEmbeddingOuterClass.EmojiEmbedding>()

    override val processorType: ProcessorType
        get() = ProcessorType.JSON_TO_PROTOBUF_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.IO) {
        context.resources.openRawResource(R.raw.emoji_embeddings_json).use { inputStream ->
            GZIPInputStream(inputStream).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val entity = gson.fromJson(line, EmojiJsonEntity::class.java)
                    val pbEntity = EmojiEmbeddingOuterClass.EmojiEmbedding.newBuilder()
                        .setEmoji(entity.emoji)
                        .setMessage(entity.message)
                        .addAllEmbed(entity.embed.toList())
                        .build()

                    pbEntityCollection.add(pbEntity)
                }
            }
        }

        saveToProtoBuf(context)
    }

    private fun saveToProtoBuf(context: Context) {
        val fileStream = context.openFileOutput("emoji_embeddings_proto.gz", Context.MODE_PRIVATE)
        GZIPOutputStream(fileStream).use { gzipOutputStream ->
            try {
                pbEntityCollection.forEach {
                    it.writeDelimitedTo(gzipOutputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}