package com.example.testbigfileread.processor

import android.content.Context
import com.example.testbigfileread.processor.impl.DatabaseLoadProcessor
import com.example.testbigfileread.processor.impl.EachLineProcessor
import com.example.testbigfileread.processor.impl.DefaultProcessor
import com.example.testbigfileread.processor.impl.JsonToProtoBufProcessor
import com.example.testbigfileread.processor.impl.MultiStreamProcessor
import com.example.testbigfileread.processor.impl.ProtobufMultiStreamProcessor
import com.example.testbigfileread.processor.impl.ProtobufProcessor
import com.example.testbigfileread.processor.impl.JsonToDatabaseProcessor

object ProcessorFactory {

    private val processors = listOf(
        DefaultProcessor(),
        EachLineProcessor(),
        MultiStreamProcessor(),
        ProtobufProcessor(),
        ProtobufMultiStreamProcessor(),
        JsonToProtoBufProcessor(),
        JsonToDatabaseProcessor(),
        DatabaseLoadProcessor()
    )

    suspend fun doProcess(context: Context, processorType: ProcessorType) {
        processors.find { it.processorType == processorType }?.process(context)
            ?: throw ProcessorNotFoundException("the $processorType processor not support!")
    }
}