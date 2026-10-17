package org.app.vimate.sites.logic

import org.app.kmediaplayer.util.formatDecimal

/** POC download/DownloadService.kt `fmtSize`. */
fun fmtSize(b: Long): String = when {
    b >= 1L shl 30 -> formatDecimal(b / (1L shl 30).toDouble(), 1) + " GB"
    b >= 1L shl 20 -> formatDecimal(b / (1L shl 20).toDouble(), 1) + " MB"
    b >= 1L shl 10 -> formatDecimal(b / 1024.0, 0) + " KB"
    else -> "$b B"
}
