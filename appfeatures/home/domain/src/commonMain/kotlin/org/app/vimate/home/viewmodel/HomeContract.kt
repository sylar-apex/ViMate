package org.app.vimate.home.viewmodel

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.yt.Video

object HomeContract {
    sealed interface Event : UiEvent {
        data object Start : Event
        data class SelectChip(val index: Int) : Event
        data object LoadMore : Event
        /** POC ErrorBox retry: `load(feed.isEmpty())`. */
        data object Retry : Event
    }

    data class State(
        val chip: Int = 0,
        val feed: List<Video> = emptyList(),
        val token: String? = null,
        val loading: Boolean = false,
        val error: String? = null,
        val activeDownloads: Int = 0,
    ) : UiState

    sealed interface Effect : UiEffect
}
