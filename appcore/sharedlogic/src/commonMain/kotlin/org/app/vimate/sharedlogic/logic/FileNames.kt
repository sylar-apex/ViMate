package org.app.vimate.sharedlogic.logic

/** File names for downloads. */
object FileNames {
    private const val MAX_NAME = 40
    private const val MAX_EXT = 4
    private const val FALLBACK = "media"
    private val unsafe = Regex("[^a-z0-9]+")

    /** A safe "title-words.ext" name, keeping the extension of [url]. */
    fun forDownload(title: String, url: String): String {
        val ext = url.substringBefore('?').substringAfterLast('.', "mp4").lowercase().take(MAX_EXT)
        val safe = title.lowercase().replace(unsafe, "-").trim('-').take(MAX_NAME).ifBlank { FALLBACK }
        return "$safe.$ext"
    }
}
