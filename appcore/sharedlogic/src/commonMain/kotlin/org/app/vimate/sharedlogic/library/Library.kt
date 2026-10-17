package org.app.vimate.sharedlogic.library

import kotlinx.serialization.builtins.serializer
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.yt.Video

/** App-wide persisted state: history, playlists, subscriptions, search history (POC data/Stores.kt Library). */
class Library(stores: StoreFactory) {
    private val store = stores.open("library")
    val watchHistory = PersistedList(store, "history", Video.serializer())
    val localHistory = PersistedList(store, "local_history", String.serializer())
    val siteHistory = PersistedList(store, "site_history", SiteVisit.serializer())
    val searches = PersistedList(store, "searches", String.serializer(), limit = 30)
    val playlists = PersistedList(store, "playlists", Playlist.serializer())
    val subscriptions = PersistedList(store, "subscriptions", Channel.serializer())
    val favoriteSites = PersistedList(store, "fav_sites", String.serializer())

    fun addWatched(v: Video) = watchHistory.update { listOf(v) + it.filter { o -> o.id != v.id } }
    fun addSearch(q: String) = searches.update { listOf(q) + it.filter { o -> !o.equals(q, true) } }
    fun addSite(url: String, title: String) =
        siteHistory.update { listOf(SiteVisit(url, title, nowMs())) + it.filter { o -> o.url != url } }

    fun saveToPlaylist(playlistId: Long, v: Video) = playlists.update { list ->
        list.map { if (it.id == playlistId && it.videos.none { o -> o.id == v.id }) it.copy(videos = it.videos + v) else it }
    }

    fun createPlaylist(name: String): Long {
        val id = nowMs()
        playlists.update { it + Playlist(id, name) }
        return id
    }

    fun isSubscribed(id: String) = subscriptions.items.value.any { it.id == id }
    fun toggleSubscription(c: Channel) = subscriptions.update { list ->
        if (list.any { it.id == c.id }) list.filter { it.id != c.id } else list + c
    }
}
