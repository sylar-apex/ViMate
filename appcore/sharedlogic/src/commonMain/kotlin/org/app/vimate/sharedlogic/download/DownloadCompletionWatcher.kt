package org.app.vimate.sharedlogic.download

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.app.kdownloader.model.DownloadEvent
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kdownloader.model.DownloadTask
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.download.mapper.DownloadMapper
import org.app.vimate.sharedlogic.download.remote.DownloadService
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.MessageKey
import org.app.vimate.sharedlogic.model.LocalFile
import org.app.vimate.sharedlogic.model.Note
import org.app.vimate.sharedlogic.model.NoteTitle
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.settings.AppKeys

/**
 * Reacts to the download engine: a finished file joins My Files with an in-app note and, when the
 * setting is on, a system notification; Wi-Fi-only waits show a toast.
 */
class DownloadCompletionWatcher(
    private val service: DownloadService,
    private val mapper: DownloadMapper,
    private val userData: UserDataRepository,
    private val settings: ObservableSettings,
    private val platform: PlatformActions,
    private val events: AppEvents,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        scope.launch {
            service.events.collect { event ->
                when (event) {
                    is DownloadEvent.Completed, is DownloadEvent.Skipped -> onCompleted(
                        if (event is DownloadEvent.Completed) event.task else (event as DownloadEvent.Skipped).task,
                    )
                    DownloadEvent.WaitingForWifi -> events.message(MessageKey.WIFI_ONLY)
                    is DownloadEvent.Failed, is DownloadEvent.Retrying, is DownloadEvent.Started -> Unit
                }
            }
        }
    }

    private fun onCompleted(task: DownloadTask) {
        val item = mapper.toItem(task)
        val uri = item.localUri ?: return
        val file = LocalFile(
            id = FILE_PREFIX + item.id,
            title = item.title,
            uri = uri,
            kind = item.kind.wire,
            bytes = item.bytes,
            mediaId = item.mediaId,
        )
        val note = Note(NOTE_PREFIX + item.id, NoteTitle.DOWNLOAD_COMPLETE, item.title, nowMs())
        userData.update { data ->
            data.copy(
                files = listOf(file) + data.files.filter { it.id != file.id },
                notifications = listOf(note) + data.notifications,
            )
        }
        if (settings[AppKeys.downloadNotify]) platform.notify(item.title, item.title)
    }

    private companion object {
        const val FILE_PREFIX = "file-"
        const val NOTE_PREFIX = "note-"
    }
}
