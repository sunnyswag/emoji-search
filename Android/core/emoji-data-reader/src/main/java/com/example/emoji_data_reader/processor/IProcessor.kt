package com.example.emoji_data_reader.processor

import android.content.Context

internal interface IProcessor {

    val processorType: ProcessorType

    suspend fun process(context: Context)
}