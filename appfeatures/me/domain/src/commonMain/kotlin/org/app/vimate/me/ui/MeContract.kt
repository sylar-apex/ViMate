package org.app.vimate.me.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.yt.Video

class MeContract {
    sealed class Event : UiEvent {
        data class SetDark(val dark: Boolean) : Event()
    }
    data class State(val hot: List<Video> = emptyList(), val dark: Boolean = false) : UiState
    sealed class Effect : UiEffect
}
