package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType

class ChannelProcessor : IProcessor {

    override val processorType = ProcessorType.PROCESS_EACH_LINE_WITH_CHANNEL

    override fun process(context: Context) {
        TODO("Not yet implemented")
    }
}