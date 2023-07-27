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

fun String.limitLength(maxLen: Int = 30): String {
    return if (this.length > maxLen) {
        this.substring(0, maxLen)
    } else {
        this
    }
}