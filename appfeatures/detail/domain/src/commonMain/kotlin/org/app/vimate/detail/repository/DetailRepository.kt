package org.app.vimate.detail.repository

import kotlinx.coroutines.flow.StateFlow
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.library.Playlist
import org.app.vimate.sharedlogic.yt.Comment
import org.app.vimate.sharedlogic.yt.Page
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.app.vimate.sharedlogic.yt.YouTube

/** Data the video page reads and writes: stream info, related videos, comments, history and playlists. */
interface DetailRepository {
    suspend fun info(id: String): VideoInfo
    suspend fun next(id: String): YouTube.Next
    suspend fun comments(token: String): Page<Comment>
    fun addWatched(v: Video)
    val playlists: StateFlow<List<Playlist>>
    val subscriptions: StateFlow<List<Channel>>
    fun saveToPlaylist(playlistId: Long, v: Video)
    fun createPlaylist(name: String): Long
    val autoPlay: Boolean
    /** Settings › Auto Pop-up Play Videos: leaving the page hands a playing video to the popup window. */
    val autoPopup: Boolean
    val onlineQuality: String
}
