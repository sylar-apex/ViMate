package org.app.vimate.detail.repository

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

class DetailRepositoryImpl(private val library: Library, private val settings: ObservableSettings) : DetailRepository {
    override suspend fun info(id: String) = YouTube.info(id)
    override suspend fun next(id: String) = YouTube.next(id)
    override suspend fun comments(token: String) = YouTube.comments(token)
    override fun addWatched(v: Video) = library.addWatched(v)
    override val playlists get() = library.playlists.items
    override val subscriptions get() = library.subscriptions.items
    override fun saveToPlaylist(playlistId: Long, v: Video) = library.saveToPlaylist(playlistId, v)
    override fun createPlaylist(name: String) = library.createPlaylist(name)
    override val autoPlay get() = settings[AppKeys.autoPlay]
    override val autoPopup get() = settings[AppKeys.autoPopup]
    override val onlineQuality get() = settings[AppKeys.onlineQuality]
}
