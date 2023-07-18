package com.example.testbigfileread.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testbigfileread.entity.EmojiEmbeddingEntity

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