package com.example.emojisemanticsearch.entity

data class EmojiEmbedding(
    val emoji: String,
    val message: String,
    val embed: List<Float>
)