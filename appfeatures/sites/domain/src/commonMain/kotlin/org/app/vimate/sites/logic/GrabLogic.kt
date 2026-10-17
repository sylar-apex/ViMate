package org.app.vimate.sites.logic

import org.app.vimate.sites.model.GrabCandidate
import org.app.vimate.sites.model.GrabType

private val cdnSize = Regex("""/(\d{2,4})x(\d{0,4})[_/]""")

/** Same picture at several CDN sizes (Pinterest /236x/, /736x/, /originals/…): keep one, the largest. */
fun dedupeGrab(urls: List<String>): List<GrabCandidate> {
    fun sizeRank(u: String): Int = when {
        "/originals/" in u -> 100000
        else -> cdnSize.find(u)?.groupValues?.get(1)?.toIntOrNull() ?: 5000
    }
    return urls.distinct().groupBy { u ->
        val t = GrabType.of(u)
        if (t == GrabType.Image) u.substringBefore('?').substringAfterLast('/') else u
    }.map { (_, group) ->
        val best = group.maxBy(::sizeRank)
        GrabCandidate(best, GrabType.of(best), group.size)
    }
}

/** File extension of a URL when it looks like one (2–5 letters/digits). */
fun urlExt(url: String): String? =
    url.substringBefore('?').substringAfterLast('/').substringAfterLast('.', "").lowercase().takeIf { it.length in 2..5 && it.all(Char::isLetterOrDigit) }

fun urlName(url: String): String = url.substringBefore('?').substringAfterLast('/').ifEmpty { url.take(40) }

/** Network requests the browser treats as media (POC BrowserScreen `mediaExt` + CDN checks). */
object MediaSniffer {
    private val mediaExt = Regex("""\.(mp4|m4v|webm|mkv|mov|3gp|m3u8|mp3|m4a|aac|ogg|wav|flac)(\?|$)""", RegexOption.IGNORE_CASE)
    private val byteRange = Regex("""&?bytestart=\d+&byteend=\d+""")
    private val audioExt = Regex("""\.(mp3|m4a|aac)""")

    fun isMedia(u: String): Boolean =
        mediaExt.containsMatchIn(u.substringBefore('#')) || ".fbcdn.net/" in u && ".mp4" in u || "cdninstagram.com" in u && ".mp4" in u

    fun mimeOf(u: String): String = if (".m3u8" in u) "application/x-mpegURL" else "video/mp4"

    /** Strips Facebook byte ranges; null for blob:/data: URLs. */
    fun clean(u: String): String? {
        if (u.startsWith("blob:") || u.startsWith("data:")) return null
        return if ("bytestart=" in u) u.replace(byteRange, "") else u
    }

    fun isAudio(mime: String, url: String) = mime.startsWith("audio") || audioExt.containsMatchIn(url)
}

/** YouTube links the browser opens natively, like VidMate. */
sealed interface NativeLink {
    data class Playlist(val listId: String) : NativeLink
    data class Watch(val videoId: String) : NativeLink
}

private val listParam = Regex("[?&]list=([A-Za-z0-9_-]+)")

fun nativeLink(u: String, idFromUrl: (String) -> String?): NativeLink? {
    if ("youtube.com" !in u && "youtu.be/" !in u) return null
    val list = listParam.find(u)?.groupValues?.get(1)
    val id = idFromUrl(u)
    return when {
        "/playlist" in u && list != null -> NativeLink.Playlist(list)
        id != null && ("/watch" in u || "youtu.be/" in u) -> NativeLink.Watch(id)
        else -> null
    }
}

/** Host of [url] without "www.", or null. */
fun hostOf(url: String): String? = Regex("""^[a-zA-Z][a-zA-Z0-9+.-]*://([^/?#:]+)""").find(url)?.groupValues?.get(1)?.removePrefix("www.")

/** Address-bar text typed by the user → URL (Google search for non-URLs). */
fun typedToUrl(t: String, encode: (String) -> String): String =
    if (t.contains(' ') || !t.contains('.')) "https://www.google.com/search?q=" + encode(t) else if ("://" in t) t else "https://$t"
