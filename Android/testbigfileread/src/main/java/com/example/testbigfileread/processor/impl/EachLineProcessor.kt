package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.MainViewModel
import com.example.testbigfileread.MainViewModel.Companion.emojiEmbeddings
import com.example.testbigfileread.R
import com.example.testbigfileread.entity.EmojiInfoEntity
import com.example.testbigfileread.entity.EmojiJsonEntity
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import com.example.testbigfileread.utils.toBean
import kotlinx.coroutines.coroutineScope
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.set
import java.util.zip.GZIPInputStream

class EachLineProcessor : IProcessor {

    override val processorType = ProcessorType.PROCESS_EACH_LINE

    override suspend fun process(context: Context) = coroutineScope {
        context.resources.openRawResource(R.raw.emoji_embeddings).use { inputStream ->
            GZIPInputStream(inputStream).bufferedReader().useLines { lines ->
                lines.forEachIndexed { index, line ->
                    line.toBean<EmojiJsonEntity>()?.let { emojiJsonEntity ->
                        MainViewModel.emojiInfoData.add(
                            EmojiInfoEntity(
                                emojiJsonEntity.emoji,
                                emojiJsonEntity.message
                            )
                        )
                        emojiEmbeddings[index] = mk.ndarray(emojiJsonEntity.embed)
                    }
                }
            }
        }
    }
}