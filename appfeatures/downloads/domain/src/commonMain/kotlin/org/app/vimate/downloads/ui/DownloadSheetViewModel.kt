package org.app.vimate.downloads.ui

import com.psbapp.mvi.MVIBaseViewModel
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.downloads.domain.usecase.GetDownloadOptionsUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadLinkUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadMediaUseCase
import org.app.vimate.sharedlogic.download.usecase.GetDownloadPathUseCase
import org.app.vimate.sharedlogic.download.usecase.GetStorageInfoUseCase
import org.app.vimate.sharedlogic.download.usecase.SetDownloadFolderUseCase
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.settings.DownloadFolder

/** The download sheet: qualities with sizes, rename, save folder with free space, then DOWNLOAD. */
class DownloadSheetViewModel(
    private val getOptions: GetDownloadOptionsUseCase,
    private val getPath: GetDownloadPathUseCase,
    private val setFolder: SetDownloadFolderUseCase,
    private val storageInfo: GetStorageInfoUseCase,
    private val downloadMedia: DownloadMediaUseCase,
    private val downloadLink: DownloadLinkUseCase,
    private val settings: ObservableSettings,
) : MVIBaseViewModel<DownloadSheetContract.Event, DownloadSheetContract.State, DownloadSheetContract.Effect>() {

    override fun createInitialState() = DownloadSheetContract.State()
    override fun handleEffect(effect: DownloadSheetContract.Effect) {}

    override fun handleEvent(event: DownloadSheetContract.Event) {
        when (event) {
            is DownloadSheetContract.Event.Load -> load(event.mediaId, event.url)
            is DownloadSheetContract.Event.SetFolder -> {
                setFolder(event.folder)
                refreshPath()
            }
            is DownloadSheetContract.Event.Submit -> {
                val form = event.form
                when {
                    form.mediaId != null -> downloadMedia(form.mediaId, form.choice, form.name)
                    form.url != null -> downloadLink(form.url, form.name)
                }
            }
        }
    }

    private fun load(mediaId: String?, url: String?) {
        setState { copy(options = getOptions(mediaId, url), storage = storageInfo()) }
        refreshPath()
    }

    private fun refreshPath() {
        val kind = currentState.options?.kind ?: return
        val folder = DownloadFolder.fromWire(settings[AppKeys.downloadFolder])
        setState {
            copy(
                folder = folder,
                path = getPath(kind, folder),
                folderPaths = DownloadFolder.entries.associateWith { getPath(kind, it) },
            )
        }
    }
}
