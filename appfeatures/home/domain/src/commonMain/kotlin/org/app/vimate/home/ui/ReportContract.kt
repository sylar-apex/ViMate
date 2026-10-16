package org.app.vimate.home.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.model.ReportReason

class ReportContract {
    sealed class Event : UiEvent {
        data class Submit(val mediaId: String, val reason: ReportReason) : Event()
    }

    data object State : UiState

    sealed class Effect : UiEffect
}
