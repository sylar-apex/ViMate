package org.app.vimate.detail.viewmodel

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.vimate.detail.contract.DetailContract.Effect
import org.app.vimate.detail.contract.DetailContract.Event
import org.app.vimate.detail.contract.DetailContract.State
import org.app.vimate.detail.repository.DetailRepository

/** POC DetailScreen loading: info + next per current video, first comment, history record, auto next. */
class DetailViewModel(private val repo: DetailRepository) : MVIBaseViewModel<Event, State, Effect>() {
    private var jobs = listOf<Job>()

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Open -> if (currentState.current?.id != event.video.id) load(event.video)
            Event.PlayNext -> currentState.next?.related?.firstOrNull()?.let(::load)
            Event.RetryInfo -> currentState.current?.let(::load)
        }
    }

    override fun handleEffect(effect: Effect) {}

    private fun load(v: org.app.vimate.sharedlogic.yt.Video) {
        jobs.forEach { it.cancel() }
        setState { State(current = v, nextLoading = true) }
        record()
        jobs = listOf(
            viewModelScope.launch {
                runCatching { repo.info(v.id) }.onSuccess { i -> setState { copy(info = i) }; record() }
                    .onFailure { e -> setState { copy(infoError = e.message ?: "Network error") } }
            },
            viewModelScope.launch {
                val n = runCatching { repo.next(v.id) }.getOrNull()
                setState { copy(next = n, nextLoading = false) }
                val first = n?.commentsToken?.let { t -> runCatching { repo.comments(t).items.firstOrNull() }.getOrNull() }
                setState { copy(firstComment = first) }
            },
        )
    }

    /** Record on open (like VidMate), refreshed with full details once they load. */
    private fun record() { currentState.video?.takeIf { it.title.isNotEmpty() }?.let(repo::addWatched) }
}
