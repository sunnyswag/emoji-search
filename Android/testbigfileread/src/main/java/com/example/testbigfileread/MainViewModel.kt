package com.example.testbigfileread

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testbigfileread.entity.EmojiInfoEntity
import com.example.testbigfileread.processor.ProcessorFactory
import com.example.testbigfileread.processor.ProcessorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            measureTime {
                ProcessorFactory.doProcess(context, ProcessorType.DEFAULT_PROCESSOR)
            }.also {
                if (emojiInfoData.size != EMOJI_EMBEDDING_SIZE ||
                    (emojiEmbeddings.shape[0] != EMOJI_EMBEDDING_SIZE
                            && emojiEmbeddings.shape[1] != EMBEDDING_LENGTH_PER_EMOJI)) {
                    throw IllegalStateException("the size of emojiInfoData or emojiEmbeddings is not correct," +
                            "emojiInfoData.size: ${emojiInfoData.size}, " +
                            "emojiEmbeddings.shape: ${emojiEmbeddings.shape[0]}、${emojiEmbeddings.shape[1]}}")
                }
                _timeSpend.value = it
                Log.d(TAG, "processInitialEmojiData time: $it")
            }
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
        const val EMOJI_EMBEDDING_SIZE = 3753
        const val EMBEDDING_LENGTH_PER_EMOJI = 1536

        // size: 3753, 1536
        val emojiEmbeddings = mk.zeros<Float>(EMOJI_EMBEDDING_SIZE, EMBEDDING_LENGTH_PER_EMOJI)
        val emojiInfoData: MutableList<EmojiInfoEntity> = mutableListOf()
    }
}