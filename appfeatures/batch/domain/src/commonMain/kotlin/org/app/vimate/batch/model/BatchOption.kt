package org.app.vimate.batch.model

/** One choice of the batch sheet: label, container and the size summed over every selected video having it. */
data class BatchOption(val label: String, val container: String, val sizeBytes: Long) {
    val key get() = "$label|$container"
}

/** POC fmtSize. */
fun fmtSize(b: Long): String = when {
    b >= 1L shl 30 -> fmt1(b / (1L shl 30).toDouble()) + " GB"
    b >= 1L shl 20 -> fmt1(b / (1L shl 20).toDouble()) + " MB"
    b >= 1L shl 10 -> "${kotlin.math.round(b / 1024.0).toLong()} KB"
    else -> "$b B"
}

private fun fmt1(v: Double): String {
    val t = kotlin.math.round(v * 10).toLong()
    return "${t / 10}.${t % 10}"
}
