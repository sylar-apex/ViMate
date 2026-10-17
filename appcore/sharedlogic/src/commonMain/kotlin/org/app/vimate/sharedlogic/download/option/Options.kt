package org.app.vimate.sharedlogic.download.option

import kotlin.math.abs
import org.app.vimate.sharedlogic.yt.Stream
import org.app.vimate.sharedlogic.yt.VideoInfo

/** Download task kind (POC download.Kind). */
enum class Kind { Video, Music, Picture, App, Other }

/** One choice in the download sheet ("128K (M4A)", "720P HD (MP4)"). */
data class DownloadOption(
    val label: String,
    val container: String,
    val kind: Kind,
    val sizeBytes: Long,
    val parts: List<Stream>,
) {
    val ext get() = when (container) { "MP4" -> "mp4"; "M4A" -> "m4a"; "WEBM" -> "webm"; else -> container.lowercase() }
}

object Options {
    /** Snap any height (vertical/letterboxed videos give odd values) to VidMate's standard tiers. */
    fun tier(h: Int): String {
        val tiers = listOf(144, 240, 360, 480, 720, 1080, 1440, 2160)
        val t = tiers.minBy { abs(it - h) }
        return when (t) { 2160 -> "4K"; 1440 -> "1440P"; 1080 -> "1080P FHD"; 720 -> "720P HD"; else -> "${t}P" }
    }

    /** Music and Video sections of the download sheet. */
    fun of(info: VideoInfo): Pair<List<DownloadOption>, List<DownloadOption>> {
        val music = info.audioOnly.filter { it.container == "mp4" }.ifEmpty { info.audioOnly }
            .sortedWith(compareBy({ it.container != "mp4" }, { -it.bitrate }))
            .map { s ->
                val kb = when { s.kbps >= 200 -> 256; s.kbps >= 100 -> 128; s.kbps >= 60 -> 64; else -> 48 }
                DownloadOption("${kb}K", if (s.container == "mp4") "M4A" else "WEBM", Kind.Music, s.sizeBytes, listOf(s))
            }
            .distinctBy { it.label + it.container }

        val audio = info.audioOnly.filter { it.container == "mp4" }.maxByOrNull { it.bitrate }
        val muxed = info.muxed.filter { it.container == "mp4" }.associateBy { it.height }
        val adaptive = info.videoOnly.filter { it.container == "mp4" && it.isAvc }
            .groupBy { it.height }.mapValues { it.value.maxBy { s -> s.bitrate } }
        val heights = (muxed.keys + if (audio != null) adaptive.keys else emptySet()).filter { it > 0 }.sorted()
        val video = heights.map { h ->
            val label = tier(h)
            muxed[h]?.let { DownloadOption(label, "MP4", Kind.Video, it.sizeBytes, listOf(it)) }
                ?: adaptive.getValue(h).let { v -> DownloadOption(label, "MP4", Kind.Video, v.sizeBytes + audio!!.sizeBytes, listOf(v, audio)) }
        }
        return music to video.groupBy { it.label }.map { (_, l) -> l.maxBy { it.sizeBytes } }
    }

    fun default(video: List<DownloadOption>, preferred: String): DownloadOption? {
        val h = preferred.filter(Char::isDigit)
        return video.find { it.label.startsWith(h) } ?: video.lastOrNull { it.parts.size == 1 } ?: video.firstOrNull()
    }
}
