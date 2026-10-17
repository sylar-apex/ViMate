package org.app.vimate.me.data.repository

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.me.domain.constant.MeUrls
import org.app.vimate.me.domain.repository.MeRepository
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

class MeRepositoryImpl(
    private val library: Library,
    private val settings: ObservableSettings,
    private val playback: PlaybackLauncher,
) : MeRepository {
    override val watchHistory = library.watchHistory.items
    override val siteHistory = library.siteHistory.items
    override val playlists = library.playlists.items
    override val subscriptions = library.subscriptions.items
    override val settingsVersion = settings.version
    override val signedIn: Boolean get() = YouTube.signedIn
    override val isDark: Boolean get() = settings[AppKeys.theme] == "dark"
    override fun setDark(dark: Boolean) { settings[AppKeys.theme] = if (dark) "dark" else "light" }

    override suspend fun hotVideos(): List<Video> =
        runCatching { YouTube.feed(MeUrls.HOT_QUERY).items.take(MeUrls.HOT_COUNT) }.getOrDefault(emptyList())
    override suspend fun accountFeed(browseId: String) = YouTube.accountFeed(browseId)
    override suspend fun channelVideos(channelId: String) = YouTube.channelVideos(channelId)
    override suspend fun playAudio(videos: List<Video>, index: Int) = playback.audio(videos, index)

    override fun removeWatched(id: String) = library.watchHistory.update { l -> l.filter { it.id != id } }
    override fun clearLocalHistory() { library.watchHistory.clear(); library.localHistory.clear() }
    override fun createPlaylist(name: String) { library.createPlaylist(name) }
    override fun deletePlaylist(id: Long) = library.playlists.update { l -> l.filter { it.id != id } }
    override fun removeFromPlaylist(id: Long, video: Video) =
        library.playlists.update { l -> l.map { if (it.id == id) it.copy(videos = it.videos - video) else it } }
}
