package com.example.testbigfileread.processor

enum class ProcessorType {
    DEFAULT_JSON_PROCESSOR, // 一次性加载模式
    JSON_EACH_LINE_PROCESSOR, // 逐行进行加载
    JSON_EACH_LINE_MULTI_STREAM_PROCESSOR, // 多个流逐行进行加载，使用 Flow 和多线程
    PROTOBUF_PROCESSOR, // 使用 protobuf 进行加载
    PROTOBUF_MULTI_STREAM_PROCESSOR // 使用 protobuf 进行加载，多个流
}