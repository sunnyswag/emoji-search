package com.example.testbigfileread.processor

import android.content.Context

interface IProcessor {

    val processorType: ProcessorType

    suspend fun process(context: Context)
}