package com.example.emoji_data_reader.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emoji_embedding")
data class EmojiEmbeddingEntity(
    @PrimaryKey val emoji: String,
    val message: String,
    val embed: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiEmbeddingEntity

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