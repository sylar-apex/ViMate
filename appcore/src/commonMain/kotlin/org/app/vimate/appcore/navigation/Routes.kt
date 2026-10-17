package org.app.vimate.appcore.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.app.vimate.sharedlogic.yt.Video

private val routeJson = Json { ignoreUnknownKeys = true }

/**
 * Every destination of the main window (POC ui/Nav.kt `Route`). The four tabs live inside [Tabs].
 * Routes carrying a [Video] store it as JSON (navigation arguments must be primitives); use the
 * `of(...)` factories and the decoded properties.
 */
object Routes {
    @Serializable data object Tabs
    @Serializable data class Search(val query: String = "")

    @Serializable data class Detail(val videoJson: String) {
        val video: Video get() = routeJson.decodeFromString(Video.serializer(), videoJson)
        companion object { fun of(video: Video) = Detail(routeJson.encodeToString(Video.serializer(), video)) }
    }

    @Serializable data class Shorts(val startJson: String? = null) {
        val start: Video? get() = startJson?.let { routeJson.decodeFromString(Video.serializer(), it) }
        companion object { fun of(start: Video?) = Shorts(start?.let { routeJson.encodeToString(Video.serializer(), it) }) }
    }

    @Serializable data object Sites
    @Serializable data class Browser(val url: String)
    @Serializable data class YtPlaylist(val listId: String)
    @Serializable data object YtSignIn
    @Serializable data object GoogleLogin
    @Serializable data class AccountFeed(val title: String, val browseId: String)

    @Serializable data class BatchSelect(val videosJson: String) {
        val videos: List<Video> get() = routeJson.decodeFromString(videoList, videosJson)
        companion object {
            private val videoList = kotlinx.serialization.builtins.ListSerializer(Video.serializer())
            fun of(videos: List<Video>) = BatchSelect(routeJson.encodeToString(videoList, videos))
        }
    }

    @Serializable data class MusicCollection(val browseId: String, val title: String, val params: String? = null)
    @Serializable data object MusicSearch
    @Serializable data object History
    @Serializable data object Playlists
    @Serializable data class PlaylistDetail(val id: Long)
    @Serializable data object Subscriptions
    @Serializable data object JunkClean
    @Serializable data object Boost
    @Serializable data object Help
    @Serializable data object Feedback
    @Serializable data object Settings
    @Serializable data class SettingsGroup(val group: String)
    @Serializable data object Private
    @Serializable data object Apps
}
