package org.app.vimate.sharedlogic.download.repository

import kotlinx.coroutines.flow.Flow
import org.app.vimate.sharedlogic.download.model.DownloadItem
import org.app.vimate.sharedlogic.download.model.DownloadRequestData
import org.app.vimate.sharedlogic.download.model.EnqueueOutcome
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.DownloadFolder

interface DownloadRepository {
    /** Every download, newest first. */
    val downloads: Flow<List<DownloadItem>>
    val waitingForWifi: Flow<Boolean>

    fun enqueue(data: DownloadRequestData): EnqueueOutcome
    fun pause(id: String)
    fun resume(id: String)
    fun cancel(id: String)
    fun delete(ids: Collection<String>)
    fun pauseAll(): Int
    fun resumeAll(): Int

    /** The folder files of [kind] are saved to with [folder], for display. */
    fun location(kind: FileKind, folder: DownloadFolder): String
}
