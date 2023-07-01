package com.example.testbigfileread

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.testbigfileread.entity.EmojiInfoEntity
import com.example.testbigfileread.processor.ProcessorFactory
import com.example.testbigfileread.processor.ProcessorType
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class MainViewModel: ViewModel() {

    private val _timeSpend: MutableStateFlow<Duration> = MutableStateFlow(Duration.ZERO)
    val timeSpend: MutableStateFlow<Duration>
        get() = _timeSpend

    @OptIn(ExperimentalTime::class)
    fun processInitialEmojiData(context: Context) {
        measureTime {
            ProcessorFactory.doProcess(context, ProcessorType.PROCESS_EACH_LINE)
        }.also { _timeSpend.value = it }
    }


    companion object {
        const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 1536

        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiInfoData: MutableList<EmojiInfoEntity> = mutableListOf()
    }
}