package org.app.vimate.search.viewmodel

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.vimate.search.constant.SearchTab
import org.app.vimate.search.constant.URL_INPUT
import org.app.vimate.search.usecase.GetSuggestionsUseCase
import org.app.vimate.search.usecase.SearchHistoryUseCase
import org.app.vimate.search.usecase.SearchMusicUseCase
import org.app.vimate.search.usecase.SearchVideosUseCase
import org.app.vimate.search.usecase.SiteSearchUrl
import org.app.vimate.search.viewmodel.SearchContract.Effect
import org.app.vimate.search.viewmodel.SearchContract.Event
import org.app.vimate.search.viewmodel.SearchContract.State
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.yt.YouTube

/** POC SearchScreen + SearchResults + MusicResults state. */
class SearchViewModel(
    private val history: SearchHistoryUseCase,
    private val suggest: GetSuggestionsUseCase,
    private val searchVideos: SearchVideosUseCase,
    private val searchMusic: SearchMusicUseCase,
    private val playback: PlaybackLauncher,
) : MVIBaseViewModel<Event, State, Effect>() {
    private var started = false
    private var suggestJob: Job? = null
    private var resultsJob: Job? = null

    override fun createInitialState() = State()
    override fun handleEffect(effect: Effect) {}

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Start -> if (!started) {
                started = true
                viewModelScope.launch { history.history.collect { h -> setState { copy(history = h) } } }
                setState { copy(query = event.initial, submitted = event.initial) }
                refreshResults()
            }
            is Event.QueryChanged -> { setState { copy(query = event.query) }; refreshSuggestions() }
            is Event.Submit -> submit(event.query)
            Event.ClearQuery -> { setState { copy(query = "", submitted = "") }; refreshSuggestions(); refreshResults() }
            Event.ClearHistory -> history.clear()
            is Event.SelectTab -> if (event.tab != currentState.tab) { setState { copy(tab = event.tab) }; refreshResults() }
            Event.LoadMore -> load(false)
            Event.Retry -> if (currentState.tab == SearchTab.Music) refreshResults() else load(currentState.results.isEmpty())
            is Event.PlaySong -> viewModelScope.launch {
                runCatching { playback.tap(currentState.songs, event.index) }.onFailure { setEffect(Effect.Toast(it.message)) }
            }
        }
    }

    private fun submit(q: String) {
        val t = q.trim(); if (t.isEmpty()) return
        if (URL_INPUT.matches(t)) { setEffect(Effect.OpenLink(t)); return }
        history.add(t)
        val changed = t != currentState.submitted
        setState { copy(query = t, submitted = t) }
        refreshSuggestions()
        if (changed) refreshResults()
    }

    /** POC produceState(query, submitted): suggestions while typing a non-link that is not the submitted query. */
    private fun refreshSuggestions() {
        suggestJob?.cancel()
        val s = currentState
        if (!(s.query.isNotBlank() && s.query != s.submitted && !s.query.contains("://"))) { setState { copy(suggestions = emptyList()) }; return }
        suggestJob = viewModelScope.launch {
            val list = runCatching { suggest(s.query) }.getOrDefault(emptyList())
            setState { copy(suggestions = list) }
        }
    }

    /** Re-keys the results on (submitted, tab), as the POC's remember(q, filter). */
    private fun refreshResults() {
        resultsJob?.cancel()
        setState { copy(results = emptyList(), token = null, loading = false, error = null, songs = emptyList(), musicLoading = false, musicError = null) }
        val s = currentState
        if (s.submitted.isEmpty()) return
        when (s.tab) {
            SearchTab.Picture, SearchTab.Facebook, SearchTab.Instagram -> {
                setEffect(Effect.OpenBrowser(SiteSearchUrl.of(s.tab, s.submitted)!!))
                setState { copy(tab = SearchTab.All) }
                refreshResults()
            }
            SearchTab.Music -> {
                setState { copy(musicLoading = true) }
                resultsJob = viewModelScope.launch {
                    runCatching { searchMusic(s.submitted) }
                        .onSuccess { setState { copy(songs = it) } }
                        .onFailure { setState { copy(musicError = it.message ?: "") } }
                    setState { copy(musicLoading = false) }
                }
            }
            else -> load(true)
        }
    }

    private fun filter() = if (currentState.tab == SearchTab.Playlist) YouTube.Filter.Playlists else YouTube.Filter.All

    private fun load(first: Boolean) {
        val s = currentState
        if (s.submitted.isEmpty() || s.tab == SearchTab.Music) return
        if (s.loading || (!first && s.token == null)) return
        setState { copy(loading = true, error = null) }
        val f = filter()
        resultsJob = viewModelScope.launch {
            runCatching { searchVideos(s.submitted, f, if (first) null else s.token) }
                .onSuccess { p -> setState { copy(results = results + p.items.filter { n -> results.none { it.id == n.id } }, token = p.continuation) } }
                .onFailure { setState { copy(error = it.message) } }
            setState { copy(loading = false) }
        }
    }
}
