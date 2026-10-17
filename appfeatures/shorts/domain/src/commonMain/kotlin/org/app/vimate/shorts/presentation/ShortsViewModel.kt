package org.app.vimate.shorts.presentation

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.shorts.constant.ShortsConstants
import org.app.vimate.shorts.presentation.ShortsContract.Effect
import org.app.vimate.shorts.presentation.ShortsContract.Event
import org.app.vimate.shorts.presentation.ShortsContract.State
import org.app.vimate.shorts.usecase.LoadShortInfoUseCase
import org.app.vimate.shorts.usecase.LoadShortsPageUseCase
import org.app.vimate.shorts.usecase.ToggleFollowUseCase

/** POC ShortsScreen logic: paged Shorts search, per-page stream info, follow toggle. */
class ShortsViewModel(
    private val loadPage: LoadShortsPageUseCase,
    private val loadInfo: LoadShortInfoUseCase,
    private val toggleFollow: ToggleFollowUseCase,
    private val library: Library,
) : MVIBaseViewModel<Event, State, Effect>() {

    override fun createInitialState() = State()

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = 8)
    /** One-shot effects for the screen (errors, play requests). */
    val effects = _effects.asSharedFlow()
    private var started = false

    init {
        viewModelScope.launch {
            library.subscriptions.items.collect { subs -> setState { copy(subscribedIds = subs.map { it.id }.toSet()) } }
        }
    }

    fun onEvent(e: Event) = handleEvent(e)

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Start -> if (!started) {
                started = true
                event.start?.let { setState { copy(shorts = listOf(it)) } }
                more()
            }
            is Event.PageShown -> pageShown(event.page)
            is Event.ToggleFollow -> toggleFollow(event.channel)
        }
    }

    override fun handleEffect(effect: Effect) {}

    private fun more() {
        if (currentState.loading) return
        setState { copy(loading = true) }
        viewModelScope.launch {
            runCatching { loadPage(ShortsConstants.QUERIES.random(), currentState.token) }
                .onSuccess { p -> setState { copy(shorts = shorts + p.items.filter { n -> shorts.none { it.id == n.id } }, token = p.continuation) } }
                .onFailure { _effects.tryEmit(Effect.Error(it.message)) }
            setState { copy(loading = false) }
        }
    }

    private fun pageShown(page: Int) {
        val v = currentState.shorts.getOrNull(page) ?: return
        if (page >= currentState.shorts.size - ShortsConstants.PREFETCH) more()
        viewModelScope.launch {
            val i = currentState.info[v.id] ?: runCatching { loadInfo(v.id) }.getOrNull()?.also { setState { copy(info = info + (v.id to it)) } } ?: return@launch
            _effects.tryEmit(Effect.Play(v.id, i.playUrl ?: return@launch))
        }
    }
}
