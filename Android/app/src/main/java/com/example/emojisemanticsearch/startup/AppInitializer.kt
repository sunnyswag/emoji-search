package com.example.emojisemanticsearch.startup

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.entity.EmojiEmbedding
import com.example.emojisemanticsearch.utils.toBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                    lines.forEach { line ->
                        line.toBean<EmojiEmbedding>()?.let { emojiEmbeddings.add(it) }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "AppInitializer"
        var emojiEmbeddings: MutableList<EmojiEmbedding> = mutableListOf()
    }
}