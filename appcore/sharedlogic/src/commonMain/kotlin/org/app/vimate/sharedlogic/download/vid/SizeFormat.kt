package org.app.vimate.sharedlogic.download.vid

import org.app.kmediaplayer.util.formatDecimal

/** POC download/fmtSize: "1.5 GB", "12.3 MB", "40 KB", "12 B". */
fun fmtSize(b: Long): String = when {
    b >= 1L shl 30 -> formatDecimal(b / (1L shl 30).toDouble(), 1) + " GB"
    b >= 1L shl 20 -> formatDecimal(b / (1L shl 20).toDouble(), 1) + " MB"
    b >= 1L shl 10 -> formatDecimal(b / 1024.0, 0) + " KB"
    else -> "$b B"
}
