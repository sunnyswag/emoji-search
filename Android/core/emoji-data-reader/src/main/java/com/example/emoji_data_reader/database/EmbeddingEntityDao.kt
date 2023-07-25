package com.example.emoji_data_reader.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmbeddingEntityDao {
    @Query("SELECT * FROM emoji_embedding")
    fun queryAll(): List<EmojiEmbeddingEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(emojiEmbeddingEntity: EmojiEmbeddingEntity)
}