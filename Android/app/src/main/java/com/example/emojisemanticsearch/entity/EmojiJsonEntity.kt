package com.example.emojisemanticsearch.entity

data class EmojiJsonEntity(
    val emoji: String,
    val message: String,
    val embed: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiJsonEntity

        if (emoji != other.emoji) return false
        if (message != other.message) return false
        if (!embed.contentEquals(other.embed)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emoji.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + embed.contentHashCode()
        return result
    }
}