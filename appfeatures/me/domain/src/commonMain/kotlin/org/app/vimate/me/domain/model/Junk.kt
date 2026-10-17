package org.app.vimate.me.domain.model

/** POC JunkGroup kinds, in POC order. */
enum class JunkKind { CACHE, TRASH, TEMP, THUMBNAIL, AD }

/** One scanned folder of a junk group. */
data class JunkPath(val path: String, val bytes: Long = 0, val checked: Boolean = true, val exists: Boolean = false, val isDownloadTemp: Boolean = false)

data class JunkGroup(val kind: JunkKind, val paths: List<JunkPath>, val open: Boolean) {
    val bytes: Long get() = paths.sumOf { it.bytes }
    val checked: Boolean get() = paths.isNotEmpty() && paths.all { it.checked } && bytes > 0
}
