package com.example.emoji_data_reader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [EmojiEmbeddingEntity::class], version = 1)
@TypeConverters(EmbeddingEntityConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun embeddingEntityDao(): EmbeddingEntityDao
}

fun getEmbeddingEntityDao(context: Context): EmbeddingEntityDao {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "emoji_embedding"
    ).build().embeddingEntityDao()
}