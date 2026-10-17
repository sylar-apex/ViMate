package org.app.vimate.batch.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.batch.model.BatchOption
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo

object BatchSelectContract {
    sealed interface Event : UiEvent {
        data class Init(val videos: List<Video>) : Event
        data class Toggle(val id: String) : Event
        data object ToggleAll : Event
        data object OpenSheet : Event
        data object DismissSheet : Event
        data class SelectOption(val key: String) : Event
        data object Download : Event
    }

    data class State(
        val videos: List<Video> = emptyList(),
        val picked: List<String> = emptyList(),
        val sheet: Boolean = false,
        // Sheet
        val sheetVideos: List<Video> = emptyList(),
        val infos: Map<String, VideoInfo> = emptyMap(),
        val resolved: Int = 0,
        val music: List<BatchOption> = emptyList(),
        val video: List<BatchOption> = emptyList(),
        val sel: String = "360P|MP4",
        val folder: String = "",
        val storage: StorageInfo = StorageInfo.Unknown,
    ) : UiState

    sealed interface Effect : UiEffect {
        /** "N added to downloads", then the shell goes to My Files. */
        data class Added(val count: Int) : Effect
    }
}
