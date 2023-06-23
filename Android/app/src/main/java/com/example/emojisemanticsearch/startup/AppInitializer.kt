package com.example.emojisemanticsearch.startup

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.entity.EmojiJsonEntity
import com.example.emojisemanticsearch.utils.toBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class AppInitializer : Initializer<Unit> {

    private val initializerScope = CoroutineScope(Dispatchers.IO)

    @OptIn(ExperimentalTime::class)
    override fun create(context: Context) {
        initializerScope.launch {
            val timeSpend = measureTime { readEmojiEmbeddings(context) }
            Log.d(TAG, "read emoji embeddings in $timeSpend")
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    @SuppressLint("DiscouragedApi")
    private fun readEmojiEmbeddings(context: Context) {
        context.resources.openRawResource(R.raw.emoji_embeddings).use { inputStream ->
            GZIPInputStream(inputStream).use { gzipInputStream ->
                gzipInputStream.bufferedReader().useLines { lines ->
                    lines.forEachIndexed { index, line ->
                        line.toBean<EmojiJsonEntity>()?.let { emojiJsonEntity ->
                            if (index >= EMOJI_EMBEDDING_SIZE) {
                                Log.e(TAG, "emoji embeddings size is out of range, index: $index, "
                                    + "size: $EMOJI_EMBEDDING_SIZE, emoji: ${emojiJsonEntity.emoji}")
                                return@forEachIndexed
                            }

                            emojiData.add(EmojiEntity(emojiJsonEntity.emoji, emojiJsonEntity.message))
                            emojiEmbeddings[index] = mk.ndarray(emojiJsonEntity.embed)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "AppInitializer"
        private const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 1536
        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiData: MutableList<EmojiEntity> = mutableListOf()
    }
}