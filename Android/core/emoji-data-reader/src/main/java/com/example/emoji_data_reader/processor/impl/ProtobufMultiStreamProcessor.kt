package com.example.emoji_data_reader.processor.impl

import android.content.Context
import android.util.Log
import com.example.emoji_data_reader.R
import com.example.emoji_data_reader.processor.IProcessor
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiEmbeddings
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.io.DataInputStream
import java.io.EOFException
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@Deprecated("the version of this code is too old, not support no longer")
internal class ProtobufMultiStreamProcessor : IProcessor {

    private var index = AtomicInteger(0)
    override val processorType = ProcessorType.PROTOBUF_MULTI_STREAM_PROCESSOR

    override suspend fun process(context: Context, rawFileIds: List<Int>) = withContext(Dispatchers.Default) {
        List(STREAM_SIZE) { i ->
            flow {
                context.resources.openRawResource(rawFileIds[i]).use { inputStream ->
                    GZIPInputStream(inputStream).buffered().use { gzipInputStream ->
                        DataInputStream(gzipInputStream).use { dataInputStream ->
                            try {
                                while (true) {
                                    val length = dataInputStream.readInt() // read message length
                                    val byteArray = ByteArray(length)
                                    dataInputStream.readFully(byteArray) // read message content

                                    emit(byteArray)
                                }
                            } catch (e: EOFException) {
                                Log.d(TAG, "process: EOFException, end of file.")
                            }
                        }
                    }
                }
            }.flowOn(Dispatchers.IO)
        }.forEach {
            it.buffer()
                .flatMapMerge {
                    flow { emit(readEmojiData(it)) }
                }
                .collect {}
        }
    }

    private fun readEmojiData(byteArray: ByteArray) {
        val entity = EmojiEmbeddingOuterClass.EmojiEmbedding.parseFrom(byteArray)
        val currentIdx = index.getAndIncrement()
        emojiInfoData[currentIdx].emoji = entity.emoji
        emojiInfoData[currentIdx].message = entity.message
        emojiEmbeddings[currentIdx] = mk.ndarray(entity.embedList)
    }

    companion object {
        private const val TAG = "ProtobufMultiStreamProcessor"
        private const val STREAM_SIZE = 5
    }
}