package com.example.testbigfileread.processor

enum class ProcessorType {
    DEFAULT_JSON_PROCESSOR, // 一次性加载模式
    JSON_EACH_LINE_PROCESSOR, // 逐行进行加载
    JSON_EACH_LINE_MULTI_STREAM_PROCESSOR, // 多个流逐行进行加载，使用 Flow 和多线程
    PROTOBUF_PROCESSOR, // 使用 protobuf 进行加载
    PROTOBUF_MULTI_STREAM_PROCESSOR, // 使用 protobuf 进行加载，多个流

    JSON_TO_PROTOBUF_PROCESSOR, // 一次性加载模式，将 json 转换为 protobuf
    JSON_TO_DATABASE_PROCESSOR, // 一次性加载模式，将 json 转换为数据库
    DATABASE_PROCESSOR, // 一次性加载模式，从数据库中加载
}