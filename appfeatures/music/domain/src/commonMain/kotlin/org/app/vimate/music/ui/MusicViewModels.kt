package org.app.vimate.music.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.vimate.music.usecase.GetMusicCollectionUseCase
import org.app.vimate.music.usecase.GetMusicShelvesUseCase
import org.app.vimate.music.usecase.GetYtPlaylistUseCase
import org.app.vimate.music.usecase.SearchMusicUseCase
import org.app.vimate.sharedlogic.playback.PlaybackLauncher

/** Play calls report failures as a toast, as the POC's runCatching { Play.* }.onFailure { toast }. */
private fun <E : com.psbapp.mvi.UiEvent, S : com.psbapp.mvi.UiState> MVIBaseViewModel<E, S, MusicEffect>.playing(block: suspend () -> Unit) {
    viewModelScope.launch { runCatching { block() }.onFailure { setEffect(MusicEffect.Toast(it.message ?: "")) } }
}

class MusicViewModel(private val shelves: GetMusicShelvesUseCase, private val play: PlaybackLauncher) :
    MVIBaseViewModel<MusicContract.Event, MusicContract.State, MusicEffect>() {
    private var job: Job? = null

    override fun createInitialState() = MusicContract.State()

    init { val labels = shelves.tabs.map { it.label }; setState { copy(tabLabels = labels) }; load() }

    private fun load() {
        job?.cancel()
        val tab = shelves.tabs[currentState.tab]
        job = viewModelScope.launch {
            setState { copy(loading = true, error = null) }
            runCatching { shelves(tab) }.onSuccess { setState { copy(shelves = it) } }.onFailure { setState { copy(error = LoadError(it.message)) } }
            setState { copy(loading = false) }
        }
    }

    override fun handleEvent(event: MusicContract.Event) {
        when (event) {
            is MusicContract.Event.SelectTab -> if (event.index != currentState.tab) { setState { copy(tab = event.index, shelves = emptyList()) }; load() }
            MusicContract.Event.Retry -> load()
            is MusicContract.Event.PlayAll -> playing { play.audio(event.videos, 0) }
            is MusicContract.Event.Tap -> playing { play.tap(event.videos, event.index) }
            is MusicContract.Event.PlaySingle -> playing { play.audio(listOf(event.video), 0) }
        }
    }

    override fun handleEffect(effect: MusicEffect) = Unit
}

class MusicCollectionViewModel(private val load: GetMusicCollectionUseCase, private val play: PlaybackLauncher) :
    MVIBaseViewModel<MusicCollectionContract.Event, MusicCollectionContract.State, MusicEffect>() {
    private var key: Pair<String, String?>? = null

    override fun createInitialState() = MusicCollectionContract.State()

    private fun fetch() {
        val (id, params) = key ?: return
        viewModelScope.launch {
            setState { copy(loading = true, error = null) }
            runCatching { load(id, params) }.onSuccess { setState { copy(title = it.first, songs = it.second) } }.onFailure { setState { copy(error = LoadError(it.message)) } }
            setState { copy(loading = false) }
        }
    }

    override fun handleEvent(event: MusicCollectionContract.Event) {
        val songs = currentState.songs
        when (event) {
            is MusicCollectionContract.Event.Load -> if (key != event.browseId to event.params) { key = event.browseId to event.params; fetch() }
            MusicCollectionContract.Event.Retry -> fetch()
            MusicCollectionContract.Event.PlayAll -> playing { play.audio(songs, 0) }
            MusicCollectionContract.Event.Shuffle -> playing { play.audio(songs.shuffled(), 0) }
            is MusicCollectionContract.Event.Tap -> playing { play.tap(songs, event.index) }
        }
    }

    override fun handleEffect(effect: MusicEffect) = Unit
}

class MusicSearchViewModel(private val search: SearchMusicUseCase, private val play: PlaybackLauncher) :
    MVIBaseViewModel<MusicSearchContract.Event, MusicSearchContract.State, MusicEffect>() {
    private var job: Job? = null

    override fun createInitialState() = MusicSearchContract.State()

    private fun fetch() {
        job?.cancel()
        val q = currentState.submitted
        job = viewModelScope.launch {
            setState { copy(loading = true, error = null, songs = emptyList()) }
            runCatching { search(q) }.onSuccess { setState { copy(songs = it) } }.onFailure { setState { copy(error = LoadError(it.message)) } }
            setState { copy(loading = false) }
        }
    }

    override fun handleEvent(event: MusicSearchContract.Event) {
        when (event) {
            is MusicSearchContract.Event.Query -> setState { copy(query = event.text) }
            MusicSearchContract.Event.Submit -> { val q = currentState.query.trim(); if (q != currentState.submitted) { setState { copy(submitted = q) }; fetch() } }
            MusicSearchContract.Event.Retry -> fetch()
            is MusicSearchContract.Event.Tap -> { val songs = currentState.songs; playing { play.tap(songs, event.index) } }
        }
    }

    override fun handleEffect(effect: MusicEffect) = Unit
}

class YtPlaylistViewModel(private val load: GetYtPlaylistUseCase) :
    MVIBaseViewModel<YtPlaylistContract.Event, YtPlaylistContract.State, MusicEffect>() {
    private var listId: String? = null

    override fun createInitialState() = YtPlaylistContract.State()

    private fun fetch() {
        val id = listId ?: return
        viewModelScope.launch {
            setState { copy(loading = true, error = null) }
            runCatching { load(id) }.onSuccess { p -> setState { copy(title = p.title, owner = p.owner, videos = p.videos) } }
                .onFailure { setState { copy(error = LoadError(it.message)) } }
            setState { copy(loading = false) }
        }
    }

    override fun handleEvent(event: YtPlaylistContract.Event) {
        when (event) {
            is YtPlaylistContract.Event.Load -> if (listId != event.listId) { listId = event.listId; fetch() }
            YtPlaylistContract.Event.Retry -> fetch()
        }
    }

    override fun handleEffect(effect: MusicEffect) = Unit
}

