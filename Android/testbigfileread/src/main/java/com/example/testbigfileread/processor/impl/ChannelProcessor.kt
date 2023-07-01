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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream

class ChannelProcessor : IProcessor {

    override val processorType = ProcessorType.PROCESS_EACH_LINE_WITH_CHANNEL

    override suspend fun process(context: Context) = coroutineScope {
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
                emojiInfoData.add(
                    EmojiInfoEntity(
                        emojiJsonEntity.emoji,
                        emojiJsonEntity.message
                    )
                )
                emojiEmbeddings[index] = mk.ndarray(emojiJsonEntity.embed)
                index++
            }
        }
    }
}