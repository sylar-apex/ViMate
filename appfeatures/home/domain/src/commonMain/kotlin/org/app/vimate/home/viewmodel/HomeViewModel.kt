package org.app.vimate.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.vimate.home.constant.HOME_CHIP_TOPICS
import org.app.vimate.home.usecase.GetHomeFeedUseCase
import org.app.vimate.home.usecase.ObserveActiveDownloadsUseCase
import org.app.vimate.home.viewmodel.HomeContract.Effect
import org.app.vimate.home.viewmodel.HomeContract.Event
import org.app.vimate.home.viewmodel.HomeContract.State

/** POC HomeScreen feed: per-chip list with continuation paging and de-duplication by id. */
class HomeViewModel(
    private val getFeed: GetHomeFeedUseCase,
    private val observeDownloads: ObserveActiveDownloadsUseCase,
) : MVIBaseViewModel<Event, State, Effect>() {
    private var started = false
    private var job: Job? = null

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            Event.Start -> if (!started) {
                started = true
                viewModelScope.launch { observeDownloads().collect { n -> setState { copy(activeDownloads = n) } } }
                load(true)
            }
            is Event.SelectChip -> if (event.index != currentState.chip) {
                job?.cancel()
                setState { copy(chip = event.index, feed = emptyList(), token = null, loading = false, error = null) }
                load(true)
            }
            Event.LoadMore -> if (currentState.feed.isNotEmpty()) load(false)
            Event.Retry -> load(currentState.feed.isEmpty())
        }
    }

    override fun handleEffect(effect: Effect) {}

    private fun load(first: Boolean) {
        val s = currentState
        if (s.loading || (!first && s.token == null)) return
        setState { copy(loading = true, error = null) }
        val chip = s.chip
        job = viewModelScope.launch {
            runCatching { getFeed(HOME_CHIP_TOPICS[chip], if (first) null else s.token) }
                .onSuccess { p -> setState { copy(feed = feed + p.items.filter { n -> feed.none { it.id == n.id } }, token = p.continuation) } }
                .onFailure { setState { copy(error = it.message) } }
            setState { copy(loading = false) }
        }
    }
}
