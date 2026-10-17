package org.app.vimate.settings.ui.component

import kotlin.math.roundToLong

/** POC download/fmtSize. */
internal fun fmtSize(b: Long): String = when {
    b >= 1L shl 30 -> "${one(b / (1L shl 30).toDouble())} GB"
    b >= 1L shl 20 -> "${one(b / (1L shl 20).toDouble())} MB"
    b >= 1L shl 10 -> "${(b / 1024.0).roundToLong()} KB"
    else -> "$b B"
}

private fun one(v: Double): String { val t = (v * 10).roundToLong(); return "${t / 10}.${t % 10}" }
