package org.app.vimate.music.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.yt.Shelf
import org.app.vimate.sharedlogic.yt.Video

/** Error/toast text from a failed call; null message → the screen shows its fallback text. */
sealed interface MusicEffect : UiEffect {
    data class Toast(val message: String) : MusicEffect
}

/** Loading flag + error (null message falls back to "Network error") shared by the music screens. */
data class LoadError(val message: String?)

object MusicContract {
    sealed interface Event : UiEvent {
        data class SelectTab(val index: Int) : Event
        data object Retry : Event
        data class PlayAll(val videos: List<Video>) : Event
        data class Tap(val videos: List<Video>, val index: Int) : Event
        data class PlaySingle(val video: Video) : Event
    }
    data class State(
        val tabLabels: List<String> = emptyList(),
        val tab: Int = 0,
        val loading: Boolean = false,
        val error: LoadError? = null,
        val shelves: List<Shelf> = emptyList(),
    ) : UiState
}

object MusicCollectionContract {
    sealed interface Event : UiEvent {
        data class Load(val browseId: String, val params: String?) : Event
        data object Retry : Event
        data object PlayAll : Event
        data object Shuffle : Event
        data class Tap(val index: Int) : Event
    }
    data class State(
        val loading: Boolean = false,
        val error: LoadError? = null,
        val title: String? = null,
        val songs: List<Video> = emptyList(),
    ) : UiState
}

object MusicSearchContract {
    sealed interface Event : UiEvent {
        data class Query(val text: String) : Event
        data object Submit : Event
        data object Retry : Event
        data class Tap(val index: Int) : Event
    }
    data class State(
        val query: String = "",
        val submitted: String = "",
        val loading: Boolean = false,
        val error: LoadError? = null,
        val songs: List<Video> = emptyList(),
    ) : UiState
}

object YtPlaylistContract {
    sealed interface Event : UiEvent {
        data class Load(val listId: String) : Event
        data object Retry : Event
    }
    data class State(
        val loading: Boolean = false,
        val error: LoadError? = null,
        val title: String = "",
        val owner: String = "",
        val videos: List<Video> = emptyList(),
    ) : UiState
}
