package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MultiStreamProcessor : IProcessor {

    override val processorType = ProcessorType.EACH_LINE_WITH_CHANNEL_AND_MULTI_STREAM_PROCESSOR

    override suspend fun process(context: Context) = withContext(Dispatchers.Default) {

    }

    companion object {
        private const val TAG = "MultiStreamProcessor"
        private const val STREAM_SIZE = 4
    }
}