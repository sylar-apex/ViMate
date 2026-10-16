package org.app.vimate.sharedlogic.model

/** What a catalog entry is: a regular video, a vertical short, or a song. */
enum class MediaKind { VIDEO, SHORT, MUSIC }

/** One encoded file of a [Media] at a given quality. */
data class Rendition(
    val quality: String,
    val url: String,
    val bytes: Long,
)

/** A title ViMate can play and download: from the open catalog, or a local file wrapped for playback. */
data class Media(
    val id: String,
    val title: String,
    val creator: String,
    val durationMs: Long,
    val kind: MediaKind,
    val genre: String,
    val section: String,
    val blurb: String,
    val renditions: List<Rendition>,
    val adult: Boolean = false,
    val subtitleVtt: String? = null,
    val year: Int = 0,
    val thumbAtMs: Long = 0,
) {
    val isMusic: Boolean get() = kind == MediaKind.MUSIC
    val fileKind: FileKind get() = if (isMusic) FileKind.MUSIC else FileKind.VIDEO
}
