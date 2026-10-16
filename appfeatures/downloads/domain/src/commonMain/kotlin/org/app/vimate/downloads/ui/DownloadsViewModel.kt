package org.app.vimate.downloads.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.launch
import org.app.vimate.sharedlogic.download.usecase.CancelDownloadUseCase
import org.app.vimate.sharedlogic.download.usecase.ObserveDownloadsUseCase
import org.app.vimate.sharedlogic.download.usecase.PauseDownloadUseCase
import org.app.vimate.sharedlogic.download.usecase.ResumeDownloadUseCase
import org.app.vimate.sharedlogic.playback.PlaybackLauncher

/** The Downloads page: every task with progress, pause, resume, cancel and play once finished. */
class DownloadsViewModel(
    observe: ObserveDownloadsUseCase,
    private val pause: PauseDownloadUseCase,
    private val resume: ResumeDownloadUseCase,
    private val cancel: CancelDownloadUseCase,
    private val launcher: PlaybackLauncher,
) : MVIBaseViewModel<DownloadsContract.Event, DownloadsContract.State, DownloadsContract.Effect>() {

    override fun createInitialState() = DownloadsContract.State()
    override fun handleEffect(effect: DownloadsContract.Effect) {}

    init {
        viewModelScope.launch { observe().collect { setState { copy(items = it) } } }
    }

    override fun handleEvent(event: DownloadsContract.Event) {
        when (event) {
            is DownloadsContract.Event.Pause -> pause(event.id)
            is DownloadsContract.Event.Resume -> resume(event.id)
            is DownloadsContract.Event.Cancel -> cancel(event.id)
            is DownloadsContract.Event.Play -> {
                val item = event.item
                val uri = item.localUri ?: return
                launcher.playLocal(uri, item.title, item.kind, bytes = item.bytes)
            }
        }
    }
}
