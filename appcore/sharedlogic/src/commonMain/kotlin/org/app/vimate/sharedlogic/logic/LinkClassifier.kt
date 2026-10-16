package org.app.vimate.sharedlogic.logic

import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.LinkKind

/** Decides whether typed or copied text is a downloadable file link. */
object LinkClassifier {
    private val audioExtensions = listOf(".mp3", ".m4a", ".aac", ".wav", ".ogg", ".flac")
    private val directExtensions = listOf(".mp4", ".m4v", ".webm", ".mov", ".mkv") + audioExtensions

    fun classify(raw: String): LinkKind {
        val lower = raw.trim().lowercase()
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) return LinkKind.NOT_A_URL
        val path = lower.substringBefore('?').substringBefore('#')
        return if (directExtensions.any { path.endsWith(it) }) LinkKind.DIRECT_MEDIA else LinkKind.PAGE
    }

    fun kindForUrl(url: String): FileKind {
        val path = url.lowercase().substringBefore('?')
        return if (audioExtensions.any { path.endsWith(it) }) FileKind.MUSIC else FileKind.VIDEO
    }

    /** The upper-case container of a file url, "MP4" when it has no extension. */
    fun container(url: String): String = url.substringBefore('?').substringAfterLast('.', "mp4").uppercase()

    /** The last path segment of a url without its query, used as a default download name. */
    fun lastSegment(url: String): String = url.trim().substringAfterLast('/').substringBefore('?')
}
