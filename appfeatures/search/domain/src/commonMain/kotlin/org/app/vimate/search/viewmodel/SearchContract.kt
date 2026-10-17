package org.app.vimate.search.viewmodel

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.search.constant.SearchTab
import org.app.vimate.sharedlogic.yt.Video

object SearchContract {
    sealed interface Event : UiEvent {
        data class Start(val initial: String) : Event
        data class QueryChanged(val query: String) : Event
        data class Submit(val query: String) : Event
        data object ClearQuery : Event
        data object ClearHistory : Event
        data class SelectTab(val tab: SearchTab) : Event
        data object LoadMore : Event
        data object Retry : Event
        data class PlaySong(val index: Int) : Event
    }

    data class State(
        val query: String = "",
        val submitted: String = "",
        val tab: SearchTab = SearchTab.All,
        val history: List<String> = emptyList(),
        val suggestions: List<String> = emptyList(),
        val results: List<Video> = emptyList(),
        val token: String? = null,
        val loading: Boolean = false,
        val error: String? = null,
        val songs: List<Video> = emptyList(),
        val musicLoading: Boolean = false,
        /** Music failure; "" means a failure without message (UI shows "Network error"). */
        val musicError: String? = null,
    ) : UiState

    sealed interface Effect : UiEffect {
        /** Link typed: `nav.back(); nav.open(url)`. */
        data class OpenLink(val url: String) : Effect
        data class OpenBrowser(val url: String) : Effect
        /** Toast; null message → "Error". */
        data class Toast(val message: String?) : Effect
    }
}
