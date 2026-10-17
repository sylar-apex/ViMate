package org.app.vimate.sharedlogic.yt

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: String,
    val title: String,
    val channel: String = "",
    val channelId: String = "",
    val channelAvatar: String = "",
    val duration: String = "",
    val views: String = "",
    val published: String = "",
    val thumbnail: String = "https://i.ytimg.com/vi/$id/hqdefault.jpg",
) {
    val url get() = "https://www.youtube.com/watch?v=$id"
}

@Serializable
data class Collection(
    val browseId: String,
    val title: String,
    val subtitle: String = "",
    val thumbnail: String = "",
    val playlistId: String = "",
)

data class Shelf(val title: String, val videos: List<Video> = emptyList(), val collections: List<Collection> = emptyList())

data class Page<T>(val items: List<T>, val continuation: String?)

data class Stream(
    val itag: Int,
    val url: String,
    val mime: String,
    val bitrate: Int,
    val sizeBytes: Long,
    val height: Int,
    val qualityLabel: String,
    val audioSampleRate: Int,
    val hasVideo: Boolean,
    val hasAudio: Boolean,
) {
    val container get() = mime.substringAfter('/').substringBefore(';')
    val isAvc get() = "avc1" in mime
    val kbps get() = bitrate / 1000
}

data class Caption(val url: String, val language: String, val name: String)

data class VideoInfo(
    val video: Video,
    val description: String,
    val lengthSec: Long,
    val likes: String,
    val muxed: List<Stream>,
    val videoOnly: List<Stream>,
    val audioOnly: List<Stream>,
    val captions: List<Caption>,
    val hlsUrl: String?,
) {
    /** Best progressive stream for quick playback; falls back to HLS. */
    val playUrl: String? get() = muxed.maxByOrNull { it.height }?.url ?: hlsUrl
    val bestAudio: Stream? get() = audioOnly.filter { it.container == "mp4" }.maxByOrNull { it.bitrate } ?: audioOnly.maxByOrNull { it.bitrate }
}

data class Comment(val author: String, val avatar: String, val text: String, val likes: String, val published: String, val replies: String = "")
