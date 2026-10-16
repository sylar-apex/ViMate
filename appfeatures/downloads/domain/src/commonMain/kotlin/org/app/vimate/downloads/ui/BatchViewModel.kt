package org.app.vimate.downloads.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.launch
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.downloads.domain.usecase.ObserveBatchUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadBatchUseCase
import org.app.vimate.sharedlogic.download.usecase.GetDownloadPathUseCase
import org.app.vimate.sharedlogic.download.usecase.SetDownloadFolderUseCase
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.settings.DownloadFolder
import org.app.vimate.sharedlogic.usecase.RemoveFromBasketUseCase

/** The batch download screen for the basket: one quality preset, per-title overrides, edit and delete. */
class BatchViewModel(
    observeBatch: ObserveBatchUseCase,
    private val downloadBatch: DownloadBatchUseCase,
    private val removeFromBasket: RemoveFromBasketUseCase,
    private val getPath: GetDownloadPathUseCase,
    private val setFolder: SetDownloadFolderUseCase,
    private val settings: ObservableSettings,
) : MVIBaseViewModel<BatchContract.Event, BatchContract.State, BatchContract.Effect>() {

    override fun createInitialState() = BatchContract.State()
    override fun handleEffect(effect: BatchContract.Effect) {}

    init {
        setState { copy(defaultPreset = settings[AppKeys.onlineQuality].lowercase()) }
        refreshPath()
        viewModelScope.launch {
            observeBatch().collect { items ->
                setState { copy(items = items, loaded = true) }
                if (items.isEmpty()) setEffect(BatchContract.Effect.Close)
            }
        }
    }

    override fun handleEvent(event: BatchContract.Event) {
        when (event) {
            is BatchContract.Event.Download -> downloadBatch(event.choices)
            is BatchContract.Event.Remove -> removeFromBasket(event.ids)
            is BatchContract.Event.SetFolder -> {
                setFolder(event.folder)
                refreshPath()
            }
        }
    }

    private fun refreshPath() {
        val folder = DownloadFolder.fromWire(settings[AppKeys.downloadFolder])
        setState {
            copy(
                folder = folder,
                path = getPath(FileKind.VIDEO, folder),
                folderPaths = DownloadFolder.entries.associateWith { getPath(FileKind.VIDEO, it) },
            )
        }
    }
}
