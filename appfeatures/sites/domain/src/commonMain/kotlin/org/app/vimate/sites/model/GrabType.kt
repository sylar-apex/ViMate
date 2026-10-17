package org.app.vimate.sites.model

/** File categories the grabber filters by (POC Grabber.kt `GrabType`). */
enum class GrabType(val exts: Set<String>) {
    Video(setOf("mp4", "m4v", "webm", "mkv", "mov", "avi", "3gp", "flv", "ts", "m3u8", "mpd")),
    Audio(setOf("mp3", "m4a", "aac", "ogg", "opus", "wav", "flac", "wma")),
    Image(setOf("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg", "avif", "heic")),
    Subtitle(setOf("srt", "vtt", "ass", "ssa", "sub")),
    Document(setOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "epub", "csv")),
    Compressed(setOf("zip", "rar", "7z", "tar", "gz", "bz2", "xz")),
    Program(setOf("apk", "xapk", "exe", "msi", "dmg", "deb", "jar")),
    Torrent(setOf("torrent")),
    Other(emptySet());

    companion object {
        private val imageHosts = Regex("""(pinimg|fbcdn|cdninstagram|ytimg|googleusercontent)\.""")

        fun of(url: String, mime: String = ""): GrabType {
            if (url.startsWith("magnet:")) return Torrent
            val ext = url.substringBefore('#').substringBefore('?').substringAfterLast('/').substringAfterLast('.', "").lowercase()
            entries.firstOrNull { ext in it.exts }?.let { return it }
            // Extension-less CDN URLs: fall back to known hosts / query hints.
            return when {
                mime.startsWith("video") || "videoplayback" in url || "mime=video" in url -> Video
                mime.startsWith("audio") || "mime=audio" in url -> Audio
                mime.startsWith("image") || imageHosts.containsMatchIn(url) && "/v/" !in url -> Image
                else -> Other
            }
        }
    }
}

/** A deduped grabber entry before probing. */
data class GrabCandidate(val url: String, val type: GrabType, val variants: Int = 1)

/** What probing learned about one entry; [thumb] is encoded image bytes (decoded by the image loader). */
data class ProbeResult(
    val width: Int = 0,
    val height: Int = 0,
    val durationMs: Long = 0,
    val size: Long = -1,
    val thumb: ByteArray? = null,
)

/** One media request seen by the browser (POC `Sniffed`). */
data class Sniffed(val url: String, val mime: String, val title: String)
