package org.app.vimate.sharedlogic.download.mapper

import org.app.kdownloader.model.Destination
import org.app.kdownloader.model.DownloadRequest
import org.app.kdownloader.model.DownloadStatus
import org.app.kdownloader.model.DownloadTask
import org.app.kdownloader.model.Folder
import org.app.kdownloader.model.MediaType
import org.app.vimate.sharedlogic.download.model.DownloadItem
import org.app.vimate.sharedlogic.download.model.DownloadRequestData
import org.app.vimate.sharedlogic.download.model.DownloadState
import org.app.vimate.sharedlogic.logic.FileNames
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.DownloadFolder

/** Converts between the download engine's tasks and the app's download items. */
class DownloadMapper {
    fun toItem(task: DownloadTask) = DownloadItem(
        id = task.id,
        mediaId = task.extra(KEY_MEDIA_ID),
        title = task.extra(KEY_TITLE) ?: task.fileName,
        url = task.url,
        kind = FileKind.fromWire(task.extra(KEY_KIND)),
        state = task.status.toState(),
        progress = task.progress,
        bytes = task.downloadedBytes,
        totalBytes = task.totalBytes,
        error = task.error,
        localUri = task.localUri,
        speedBytesPerSec = task.speedBytesPerSec,
    )

    fun toRequest(data: DownloadRequestData, folder: DownloadFolder) = DownloadRequest(
        url = data.url,
        fileName = FileNames.forDownload(data.title, data.url),
        destination = destination(data.kind, folder),
        totalBytesHint = data.totalBytes,
        extras = buildMap {
            put(KEY_TITLE, data.title)
            put(KEY_KIND, data.kind.wire)
            data.mediaId?.let { put(KEY_MEDIA_ID, it) }
        },
    )

    fun destination(kind: FileKind, folder: DownloadFolder) = Destination(
        type = if (kind == FileKind.MUSIC) MediaType.AUDIO else MediaType.VIDEO,
        folder = if (folder == DownloadFolder.DOWNLOAD) Folder.DOWNLOADS else Folder.MEDIA,
    )

    private fun DownloadStatus.toState() = when (this) {
        DownloadStatus.QUEUED -> DownloadState.QUEUED
        DownloadStatus.RUNNING -> DownloadState.RUNNING
        DownloadStatus.PAUSED -> DownloadState.PAUSED
        DownloadStatus.COMPLETED -> DownloadState.COMPLETED
        DownloadStatus.FAILED -> DownloadState.FAILED
        DownloadStatus.CANCELED -> DownloadState.CANCELED
    }

    private companion object {
        const val KEY_MEDIA_ID = "mediaId"
        const val KEY_TITLE = "title"
        const val KEY_KIND = "kind"
    }
}
