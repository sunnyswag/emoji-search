package com.example.emojisemanticsearch.startup

import EmojiEmbeddingOuterClass
import android.content.Context
import android.util.Log
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

class ProtobufProcessor {

    private var index = AtomicInteger(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        flow {
            context.resources.openRawResource(R.raw.emoji_embeddings_proto).use { inputStream ->
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
            .buffer()
            .flatMapMerge { byteArray ->
                flow { emit(readEmojiData(byteArray)) }
            }.collect {}
    }

    private fun readEmojiData(byteArray: ByteArray) {
        val entity = EmojiEmbeddingOuterClass.EmojiEmbedding.parseFrom(byteArray)
        val currentIdx = index.getAndIncrement()
        emojiInfoData[currentIdx].emoji = entity.emoji
        emojiInfoData[currentIdx].message = entity.message
        emojiEmbeddings[currentIdx] = mk.ndarray(entity.embedList)
    }

    companion object {
        private const val TAG = "ProtobufProcessor"
    }
}