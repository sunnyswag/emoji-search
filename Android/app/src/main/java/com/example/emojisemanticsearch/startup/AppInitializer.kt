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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class AppInitializer : Initializer<Unit> {

    private val initializerScope = CoroutineScope(Dispatchers.Default)

    @OptIn(ExperimentalTime::class)
    override fun create(context: Context) {
        initializerScope.launch {
            val timeSpend = measureTime { readEmojiEmbeddings(context) }
            Log.d(TAG, "read emoji embeddings in $timeSpend")
            if (emojiData.size != EMOJI_EMBEDDING_SIZE) {
                Log.e(TAG, "emoji data size is not correct, " +
                        "emoji data size: ${emojiData.size}, emoji embedding size: ${emojiEmbeddings.size}")
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    @SuppressLint("DiscouragedApi")
    private suspend fun readEmojiEmbeddings(context: Context) = coroutineScope {
        val channel = Channel<String>()
        var index = 0

        launch(Dispatchers.IO) {
            context.resources.openRawResource(R.raw.emoji_embeddings).use { inputStream ->
                GZIPInputStream(inputStream).use { gzipInputStream ->
                    gzipInputStream.bufferedReader().useLines { lines ->
                        for (line in lines) {
                            channel.send(line)
                        }
                        channel.close()
                    }
                }
            }
        }

        for (data in channel) {
            data.toBean<EmojiJsonEntity>()?.let { emojiJsonEntity ->
                emojiData.add(
                    EmojiEntity(
                        emojiJsonEntity.emoji,
                        emojiJsonEntity.message
                    )
                )
                emojiEmbeddings[index] = mk.ndarray(emojiJsonEntity.embed)
                index++
            }
        }
    }

    companion object {
        const val TAG = "AppInitializer"
        const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 1536
        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiData: MutableList<EmojiEntity> = mutableListOf()
    }
}