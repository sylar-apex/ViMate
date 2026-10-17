package org.app.vimate.me.domain.util

import kotlin.math.roundToLong

/** POC download/fmtSize: "1.5 GB", "12.3 MB", "40 KB", "12 B". */
fun fmtSize(b: Long): String = when {
    b >= 1L shl 30 -> "${oneDecimal(b / (1L shl 30).toDouble())} GB"
    b >= 1L shl 20 -> "${oneDecimal(b / (1L shl 20).toDouble())} MB"
    b >= 1L shl 10 -> "${(b / 1024.0).roundToLong()} KB"
    else -> "$b B"
}

private fun oneDecimal(v: Double): String {
    val t = (v * 10).roundToLong()
    return "${t / 10}.${t % 10}"
}
