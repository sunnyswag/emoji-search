package com.example.testbigfileread.processor.impl

import EmojiEmbeddingOuterClass
import android.content.Context
import android.util.Log
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.R
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.io.DataInputStream
import java.io.EOFException
import java.util.zip.GZIPInputStream

class ProtobufMultiStreamProcessor : IProcessor {

    private var index = 0
    override val processorType = ProcessorType.PROTOBUF_MULTI_STREAM_PROCESSOR

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        val mutex = Mutex()
        List(STREAM_SIZE) { i ->
            flow {
                val resId = getEmbeddingResId(i)
                context.resources.openRawResource(resId).use { inputStream ->
                    GZIPInputStream(inputStream).use { gzipInputStream ->
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
        }.asFlow()
            .flattenMerge(STREAM_SIZE)
            .collect { data ->
                mutex.withLock {
                    readEmojiData(data)
                }
            }
    }

    private fun readEmojiData(byteArray: ByteArray) {
        val entity = EmojiEmbeddingOuterClass.EmojiEmbedding.parseFrom(byteArray)
        emojiInfoData[index].emoji = entity.emoji
        emojiInfoData[index].message = entity.message
        emojiEmbeddings[index] = mk.ndarray(entity.embedList)
        index++
    }

    private fun getEmbeddingResId(i: Int) = when (i) {
        0 -> R.raw.emoji_embeddings_proto_0
        1 -> R.raw.emoji_embeddings_proto_1
        2 -> R.raw.emoji_embeddings_proto_2
        3 -> R.raw.emoji_embeddings_proto_3
        4 -> R.raw.emoji_embeddings_proto_4
        else -> throw IllegalArgumentException("Invalid index: $i")
    }

    companion object {
        private const val TAG = "ProtobufMultiStreamProcessor"
        private const val STREAM_SIZE = 5
    }
}