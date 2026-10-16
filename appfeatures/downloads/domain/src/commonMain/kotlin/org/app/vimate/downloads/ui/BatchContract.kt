package org.app.vimate.downloads.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.downloads.domain.model.BatchItem
import org.app.vimate.sharedlogic.settings.DownloadFolder

class BatchContract {
    sealed class Event : UiEvent {
        /** Queue [choices] (title id → quality). */
        data class Download(val choices: Map<String, String>) : Event()
        data class Remove(val ids: Collection<String>) : Event()
        data class SetFolder(val folder: DownloadFolder) : Event()
    }

    data class State(
        val items: List<BatchItem> = emptyList(),
        /** The quality preset the screen opens with: the online quality setting. */
        val defaultPreset: String = "",
        val path: String = "",
        val folder: DownloadFolder = DownloadFolder.MEDIA,
        val folderPaths: Map<DownloadFolder, String> = emptyMap(),
        val loaded: Boolean = false,
    ) : UiState

    sealed class Effect : UiEffect {
        /** The basket is empty: leave the screen. */
        data object Close : Effect()
    }
}
