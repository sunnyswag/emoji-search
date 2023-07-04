package com.example.testbigfileread.processor

import android.content.Context
import com.example.testbigfileread.processor.impl.EachLineProcessor
import com.example.testbigfileread.processor.impl.DefaultProcessor
import com.example.testbigfileread.processor.impl.MultiStreamProcessor

object ProcessorFactory {

    private val processors = listOf(
        DefaultProcessor(),
        EachLineProcessor(),
        MultiStreamProcessor()
    )

    suspend fun doProcess(context: Context, processorType: ProcessorType) {
        processors.find { it.processorType == processorType }?.process(context)
            ?: throw ProcessorNotFoundException("the $processorType processor not support!")
    }
}