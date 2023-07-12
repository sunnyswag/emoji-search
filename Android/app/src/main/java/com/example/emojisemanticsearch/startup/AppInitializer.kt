package com.example.emojisemanticsearch.startup

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.example.emojisemanticsearch.entity.EmojiInfoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
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
        ProtobufProcessor().process(context)
    }

    companion object {
        const val TAG = "AppInitializer"
        const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 3072
        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiInfoData: List<EmojiInfoEntity> = List(EMOJI_EMBEDDING_SIZE) {
            EmojiInfoEntity("", "")
        }
    }
}