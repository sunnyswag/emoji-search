package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.MainViewModel.Companion.emojiInfoData
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiInfoEntity
import com.example.testbigfileread.entity.EmojiJsonEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.example.testbigfileread.utils.toBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

class ChannelProcessor : IProcessor {

    override val processorType = ProcessorType.EACH_LINE_WITH_CHANNEL_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {
        val channel = Channel<String>()
        val index = AtomicInteger(0)

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
                emojiInfoData.add(
                    EmojiInfoEntity(
                        emojiJsonEntity.emoji,
                        emojiJsonEntity.message
                    )
                )
                emojiEmbeddings[index.getAndIncrement()] = mk.ndarray(emojiJsonEntity.embed)
            }
        }
    }
}