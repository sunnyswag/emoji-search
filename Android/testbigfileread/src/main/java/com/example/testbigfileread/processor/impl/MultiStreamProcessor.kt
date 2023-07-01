package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType

class MultiStreamProcessor : IProcessor {

    override val processorType = ProcessorType.PROCESS_EACH_LINE_WITH_CHANNEL_AND_MULTI_STREAM

    override suspend fun process(context: Context) {
        TODO("Not yet implemented")
    }
}