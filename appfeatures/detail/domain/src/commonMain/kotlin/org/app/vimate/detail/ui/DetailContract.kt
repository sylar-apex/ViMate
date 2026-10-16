package org.app.vimate.detail.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.kmediaplayer.model.MediaFile
import org.app.vimate.detail.domain.model.MediaDetail

class DetailContract {
    sealed class Event : UiEvent {
        /** [autoPlay]: opened from a list, so the player starts when Auto play is on. */
        data class Load(val mediaId: String, val autoPlay: Boolean) : Event()
        data object Play : Event()
        data object Download : Event()
        data object ToggleFollow : Event()
        data class OpenRelated(val mediaId: String) : Event()
    }

    /** [videoFiles]: what the inline player opens; empty for music. [autoPlay]: start it right away. */
    data class State(
        val detail: MediaDetail? = null,
        val videoFiles: List<MediaFile> = emptyList(),
        val autoPlay: Boolean = false,
    ) : UiState

    sealed class Effect : UiEffect {
        data class OpenDetail(val mediaId: String) : Effect()
    }
}
