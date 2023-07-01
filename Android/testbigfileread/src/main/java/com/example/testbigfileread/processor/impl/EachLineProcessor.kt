package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType

class EachLineProcessor : IProcessor {

    override val processorType = ProcessorType.PROCESS_EACH_LINE

    override fun process(context: Context) {
        TODO("Not yet implemented")
    }
}