package org.app.vimate.detail.usecase

import org.app.vimate.detail.model.Quality
import org.app.vimate.detail.model.WebStream
import org.app.vimate.sharedlogic.yt.VideoInfo

/** POC DetailScreen quality list: muxed or mp4/AVC video-only (≤1080p) + best audio, then HLS "Auto"; preferred quality first. */
class BuildQualitiesUseCase {
    operator fun invoke(data: VideoInfo): List<Quality> {
        val audio = data.bestAudio?.url
        val muxed = data.muxed.associateBy { it.height }
        val split = if (audio == null) emptyMap() else data.videoOnly.filter { it.container == "mp4" && it.isAvc && it.height <= 1080 }
            .groupBy { it.height }.mapValues { it.value.maxBy { s -> s.bitrate } }
        return (muxed.keys + split.keys).filter { it > 0 }.sortedDescending().map { h ->
            muxed[h]?.let { Quality("${h}p", it.url) } ?: Quality("${h}p", split.getValue(h).url, audio)
        } + listOfNotNull(data.hlsUrl?.let { Quality("Auto", it, hls = true) })
    }

    fun ordered(qualities: List<Quality>, pref: String) = qualities.sortedBy { if (it.label.startsWith(pref.filter(Char::isDigit))) 0 else 1 }

    /** Web-captured fallback: every video stream, paired with the first audio stream. */
    fun fromWeb(cap: List<WebStream>): List<Quality> {
        val audio = cap.firstOrNull { it.mime.startsWith("audio") }
        return cap.filter { it.mime.startsWith("video") }.map { Quality("Web " + it.itag, it.url, audio?.url, ua = it.userAgent) }
    }
}
