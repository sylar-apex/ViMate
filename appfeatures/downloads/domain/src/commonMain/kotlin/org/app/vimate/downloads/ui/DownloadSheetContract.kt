package org.app.vimate.downloads.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.downloads.domain.model.DownloadForm
import org.app.vimate.downloads.domain.model.DownloadOptions
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.settings.DownloadFolder

class DownloadSheetContract {
    sealed class Event : UiEvent {
        data class Load(val mediaId: String?, val url: String?) : Event()
        data class SetFolder(val folder: DownloadFolder) : Event()
        data class Submit(val form: DownloadForm) : Event()
    }

    data class State(
        val options: DownloadOptions? = null,
        val folder: DownloadFolder = DownloadFolder.MEDIA,
        val path: String = "",
        /** Path of each folder choice for the Change menu; empty where the platform has none. */
        val folderPaths: Map<DownloadFolder, String> = emptyMap(),
        val storage: StorageInfo = StorageInfo.Unknown,
    ) : UiState

    sealed class Effect : UiEffect
}
