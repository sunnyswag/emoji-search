package com.example.emojisemanticsearch.network.entity


data class EmbeddingRequest(
    val input: String,
    val model: String = "text-embedding-ada-002"
)
