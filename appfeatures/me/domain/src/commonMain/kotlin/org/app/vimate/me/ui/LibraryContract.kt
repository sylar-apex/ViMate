package org.app.vimate.me.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.library.Playlist
import org.app.vimate.sharedlogic.library.SiteVisit
import org.app.vimate.sharedlogic.yt.Video

/** History, playlists and subscriptions (POC MeScreen.kt library pages). */
class LibraryContract {
    sealed class Event : UiEvent {
        data class RemoveWatched(val id: String) : Event()
        data object ClearLocalHistory : Event()
        data class CreatePlaylist(val name: String) : Event()
        data class DeletePlaylist(val id: Long) : Event()
        data class RemoveFromPlaylist(val id: Long, val video: Video) : Event()
        data class PlayAllAudio(val videos: List<Video>) : Event()
    }
    data class State(
        val watch: List<Video> = emptyList(),
        val sites: List<SiteVisit> = emptyList(),
        val playlists: List<Playlist> = emptyList(),
        val subscriptions: List<Channel> = emptyList(),
    ) : UiState
    sealed class Effect : UiEffect {
        data class Error(val message: String) : Effect()
    }
}
