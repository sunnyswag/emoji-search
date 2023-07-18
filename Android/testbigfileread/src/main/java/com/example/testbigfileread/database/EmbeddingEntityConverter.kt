package com.example.testbigfileread.database

import androidx.room.TypeConverter
import java.nio.ByteBuffer

class EmbeddingEntityConverter {
    @TypeConverter
    fun fromFloatArray(floatArray: FloatArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(floatArray.size * 4) // Float 是 4 字节
        floatArray.forEach { byteBuffer.putFloat(it) }
        return byteBuffer.array()
    }

    @TypeConverter
    fun toFloatArray(byteArray: ByteArray): FloatArray {
        val byteBuffer = ByteBuffer.wrap(byteArray)
        return FloatArray(byteArray.size / 4) { byteBuffer.float } // Float 是 4 字节
    }
}