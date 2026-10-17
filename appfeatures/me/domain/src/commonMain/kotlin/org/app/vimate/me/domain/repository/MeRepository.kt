package org.app.vimate.me.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.library.Playlist
import org.app.vimate.sharedlogic.library.SiteVisit
import org.app.vimate.sharedlogic.yt.Video

interface MeRepository {
    val watchHistory: StateFlow<List<Video>>
    val siteHistory: StateFlow<List<SiteVisit>>
    val playlists: StateFlow<List<Playlist>>
    val subscriptions: StateFlow<List<Channel>>
    val settingsVersion: StateFlow<Int>
    val signedIn: Boolean
    val isDark: Boolean
    fun setDark(dark: Boolean)
    suspend fun hotVideos(): List<Video>
    suspend fun accountFeed(browseId: String): List<Video>
    suspend fun channelVideos(channelId: String): List<Video>
    suspend fun playAudio(videos: List<Video>, index: Int)
    fun removeWatched(id: String)
    fun clearLocalHistory()
    fun createPlaylist(name: String)
    fun deletePlaylist(id: Long)
    fun removeFromPlaylist(id: Long, video: Video)
}
