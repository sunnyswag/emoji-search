package com.example.testbigfileread.processor.impl

import android.content.Context
import com.example.testbigfileread.processor.IProcessor
import com.example.testbigfileread.processor.ProcessorType

class DefaultProcessor : IProcessor {

    override val processorType = ProcessorType.DEFAULT

    override fun process(context: Context) {
        TODO("Not yet implemented")
    }
}