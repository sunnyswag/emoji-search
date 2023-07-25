package com.example.testbigfileread

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emoji_data_reader.processor.ProcessorFactory
import com.example.emoji_data_reader.processor.ProcessorFactory.EMOJI_EMBEDDING_SIZE
import com.example.emoji_data_reader.processor.ProcessorFactory.emojiInfoData
import com.example.emoji_data_reader.processor.ProcessorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
                ProcessorFactory.doProcess(context, ProcessorType.JSON_EACH_LINE_PROCESSOR)
            }.also { duration ->
                val emojiDataSize = emojiInfoData.filterNot { it.emoji.isEmpty() }.size
                if (emojiDataSize != EMOJI_EMBEDDING_SIZE) {
                    Log.e(TAG, "the size of emojiInfoData is not correct, " +
                            "emojiInfoData.size: $emojiDataSize")
                }

                Log.i(TAG, "emoji: ${emojiInfoData[3].emoji}, message: ${emojiInfoData[3].message}")
                _timeSpend.value = duration
                Log.d(TAG, "processInitialEmojiData time: $duration")
            }
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}