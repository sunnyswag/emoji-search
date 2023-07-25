package com.example.emojisemanticsearch.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.example.emoji_data_reader.processor.ProcessorFactory
import com.example.emoji_data_reader.processor.ProcessorFactory.EMOJI_EMBEDDING_SIZE
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class AppInitializer : Initializer<Unit> {

    private val initializerScope = CoroutineScope(Dispatchers.Default)

    @OptIn(ExperimentalTime::class)
    override fun create(context: Context) {
        initializerScope.launch {
            val timeSpend = measureTime { readEmojiEmbeddings(context) }
            Log.d(TAG, "read emoji embeddings in $timeSpend")

            val emojiDataSize = emojiInfoData.filterNot { it.emoji.isEmpty() }.size
            if (emojiDataSize != EMOJI_EMBEDDING_SIZE) {
                Log.e(TAG, "the size of emojiInfoData is not correct, " +
                        "emojiInfoData.size: $emojiDataSize")
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    private suspend fun readEmojiEmbeddings(context: Context) {
        ProcessorFactory.doProcess(context, ProcessorType.PROTOBUF_PROCESSOR)
    }

    companion object {
        const val TAG = "AppInitializer"
    }
}