package com.example.emojisemanticsearch.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

/**
 * save str to clipboard
 */
fun saveToClipboard(context: Context, str: String) {
    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", str)
    clipboard.setPrimaryClip(clip)
}

/**
 * 多个变量不为空的 let 实现
 * ref: https://stackoverflow.com/questions/35513636/multiple-variable-let-in-kotlin
 */
inline fun <T1: Any, T2: Any, R: Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}
inline fun <T1: Any, T2: Any, T3: Any, R: Any> safeLet(p1: T1?, p2: T2?, p3: T3?,
                                                       block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}
inline fun <T1: Any, T2: Any, T3: Any, T4: Any, R: Any> safeLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?,
                                                                block: (T1, T2, T3, T4) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}
inline fun <T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, R: Any>
        safeLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, block: (T1, T2, T3, T4, T5)->R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null)
        block(p1, p2, p3, p4, p5) else null
}