package org.app.vimate.shorts.presentation

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo

object ShortsContract {
    data class State(
        val shorts: List<Video> = emptyList(),
        val token: String? = null,
        val loading: Boolean = false,
        val info: Map<String, VideoInfo> = emptyMap(),
        val subscribedIds: Set<String> = emptySet(),
    ) : UiState

    sealed interface Event : UiEvent {
        data class Start(val start: Video?) : Event
        data class PageShown(val page: Int) : Event
        data class ToggleFollow(val channel: Channel) : Event
    }

    sealed interface Effect : UiEffect {
        data class Error(val message: String?) : Effect
        /** Play this stream for the current page. */
        data class Play(val videoId: String, val url: String) : Effect
    }
}
