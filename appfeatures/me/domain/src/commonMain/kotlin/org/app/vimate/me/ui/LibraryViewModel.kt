package org.app.vimate.me.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.launch
import org.app.vimate.me.domain.repository.MeRepository
import org.app.vimate.sharedlogic.yt.Video

class LibraryViewModel(private val repo: MeRepository) :
    MVIBaseViewModel<LibraryContract.Event, LibraryContract.State, LibraryContract.Effect>() {
    override fun createInitialState() = LibraryContract.State()
    override fun handleEffect(effect: LibraryContract.Effect) {}

    val signedIn: Boolean get() = repo.signedIn

    init {
        viewModelScope.launch { repo.watchHistory.collect { setState { copy(watch = it) } } }
        viewModelScope.launch { repo.siteHistory.collect { setState { copy(sites = it) } } }
        viewModelScope.launch { repo.playlists.collect { setState { copy(playlists = it) } } }
        viewModelScope.launch { repo.subscriptions.collect { setState { copy(subscriptions = it) } } }
    }

    suspend fun accountFeed(browseId: String): List<Video> = repo.accountFeed(browseId)
    suspend fun channelVideos(channelId: String): List<Video> = repo.channelVideos(channelId)

    override fun handleEvent(event: LibraryContract.Event) {
        when (event) {
            is LibraryContract.Event.RemoveWatched -> repo.removeWatched(event.id)
            LibraryContract.Event.ClearLocalHistory -> repo.clearLocalHistory()
            is LibraryContract.Event.CreatePlaylist -> repo.createPlaylist(event.name)
            is LibraryContract.Event.DeletePlaylist -> repo.deletePlaylist(event.id)
            is LibraryContract.Event.RemoveFromPlaylist -> repo.removeFromPlaylist(event.id, event.video)
            is LibraryContract.Event.PlayAllAudio -> viewModelScope.launch {
                runCatching { repo.playAudio(event.videos, 0) }.onFailure { setEffect(LibraryContract.Effect.Error(it.message ?: "")) }
            }
        }
    }
}
