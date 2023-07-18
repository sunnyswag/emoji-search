package com.example.testbigfileread.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.testbigfileread.entity.EmojiEmbeddingEntity

@Dao
interface EmbeddingEntityDao {
    @Query("SELECT * FROM emoji_embedding")
    fun queryAll(): List<EmojiEmbeddingEntity>?

    @Insert
    fun insert(emojiEmbeddingEntity: EmojiEmbeddingEntity)
}