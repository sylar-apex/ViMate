package org.app.vimate.sharedlogic.playback

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.model.MediaFile
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.app.vimate.sharedlogic.yt.YouTube

/** Playback entry points that bridge YouTube streams into kmediaplayer (POC ui/Nav.kt `Play`). */
class PlaybackLauncher(private val library: Library) {
    /** Full-screen Now Playing (opened by tapping the mini player, or the playing song again). */
    val nowPlaying = MutableStateFlow(false)

    /** Song-list tap: first tap plays the song; tapping the song that is already playing opens Now Playing. */
    suspend fun tap(videos: List<Video>, index: Int) {
        val e = KMediaPlayer.engine
        val current = KMediaPlayer.queue.value.getOrNull(e.currentIndex)
        if (KMediaPlayer.isMusicSession.value && current?.id == videos[index].id.hashCode().toLong()) { nowPlaying.value = true; if (!e.playWhenReady) e.play() }
        else audio(videos, index)
    }

    suspend fun video(v: Video) {
        val info = YouTube.info(v.id)
        library.addWatched(info.video.copy(channelAvatar = v.channelAvatar.ifEmpty { info.video.channelAvatar }))
        KMediaPlayer.playVideo(listOf(info.toMediaFile(audioOnly = false)), 0)
    }

    suspend fun audio(videos: List<Video>, index: Int) {
        // Queue a window around the tapped track; stream URLs resolve in parallel.
        val from = (index - 5).coerceAtLeast(0)
        val window = videos.subList(from, minOf(videos.size, index + 25))
        val resolved = coroutineScope {
            window.map { v -> async { runCatching { YouTube.info(v.id).toMediaFile(true) }.getOrNull() } }.awaitAll()
        }
        val target = resolved[index - from] ?: error("Track unavailable")
        val files = resolved.filterNotNull()
        KMediaPlayer.playMusic(files, files.indexOf(target))
    }

    /** A local file or downloaded uri, as Files plays them (POC FilesScreen: playMusic / playVideo / playUrl). */
    fun playFiles(files: List<MediaFile>, index: Int, asMusic: Boolean) {
        if (asMusic) KMediaPlayer.playMusic(files, index) else KMediaPlayer.playVideo(files, index)
    }

    fun playUrl(url: String) = KMediaPlayer.playUrl(url)
}

fun VideoInfo.toMediaFile(audioOnly: Boolean): MediaFile {
    val uri = if (audioOnly) (bestAudio?.url ?: playUrl) else playUrl
    return MediaFile(
        id = video.id.hashCode().toLong(),
        uri = uri ?: error("No playable stream"),
        path = video.url,
        name = video.title,
        sizeBytes = 0,
        durationMs = lengthSec * 1000,
        dateModifiedMs = 0,
        mimeType = if (audioOnly) "audio/mp4" else "video/mp4",
        isAudio = audioOnly,
        artist = video.channel,
        // The player's cover art reads an image URL from album: show the track's own thumbnail.
        album = video.thumbnail,
    )
}
