package org.app.vimate.downloads.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.download.model.DownloadItem

class DownloadsContract {
    sealed class Event : UiEvent {
        data class Pause(val id: String) : Event()
        data class Resume(val id: String) : Event()
        data class Cancel(val id: String) : Event()
        data class Play(val item: DownloadItem) : Event()
    }

    data class State(val items: List<DownloadItem> = emptyList()) : UiState

    sealed class Effect : UiEffect
}
