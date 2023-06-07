package com.example.emojisemanticsearch.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Reference: https://github.com/dhhAndroid/gson-ktx
 */
object GSON {
    /**
     * 初始化默认的 [com.google.gson.Gson] 转换器
     */
    private val gson: Gson = GsonBuilder().setLenient().create()

    private const val TAG = "GSON"

    fun <T> fromJson(json: String, type: Type): T? = try {
        gson.fromJson(json, type)
    } catch (e: JsonSyntaxException) {
        Log.e(TAG, "fromJson error: ${e.message}, json: $json")
        null
    }

    fun <T> fromJson(jsonElement: JsonElement, type: Type): T? = try {
        gson.fromJson(jsonElement, type)
    } catch (e: JsonSyntaxException) {
        Log.e(TAG, "fromJson error: ${e.message}, json: $jsonElement")
        null
    }

    fun toJson(any: Any): String = gson.toJson(any)

    fun toJson(jsonElement: JsonElement): String = gson.toJson(jsonElement)

}

inline fun <reified T> String.toBean(): T? {
    val type = object : TypeToken<T>() {}.type
    return GSON.fromJson(this, type)
}

/**
 * 将 json 字符串转换为 List<T> 对象，
 * 对于 List<Int> 这种类型，需要在使用时直接定义 type，应该不能通过泛型来传递 Int 类型
 */
inline fun <reified T> String.toListBean(): List<T>? {
    val type = object : TypeToken<Array<T>>() {}.type
    return GSON.fromJson<Array<T>>(this, type)?.toList()
}

inline fun <reified K, reified V> String.toMapBean(): Map<K, V>? {
    val type = object : TypeToken<Map<K, V>>() {}.type
    return GSON.fromJson<Map<K, V>>(this, type)
}

inline fun <reified T> JsonElement.toBean(): T? {
    val type = object : TypeToken<T>() {}.type
    return GSON.fromJson(this, type)
}

fun Any.toJson() = GSON.toJson(this)

fun JsonElement.toJson() = GSON.toJson(this)