package com.example.testbigfileread.processor

import android.content.Context

interface IProcessor {

    val processorType: ProcessorType

    fun process(context: Context)
}