package com.example.testbigfileread.processor

enum class ProcessorType {
    DEFAULT_PROCESSOR, // 一次性加载模式
    EACH_LINE_PROCESSOR, // 逐行进行加载
    EACH_LINE_WITH_CHANNEL_PROCESSOR, // 逐行进行加载，使用Channel
    EACH_LINE_WITH_CHANNEL_AND_MULTI_STREAM_PROCESSOR // 逐行进行加载，使用Channel和多线程
}