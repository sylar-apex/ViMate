package org.app.vimate.sharedlogic.download.remote

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.app.kdownloader.model.Destination
import org.app.kdownloader.model.DownloadEvent
import org.app.kdownloader.model.DownloadRequest
import org.app.kdownloader.model.DownloadTask
import org.app.kdownloader.model.DownloaderConfig
import org.app.kdownloader.model.EnqueueResult

/** The download engine as the app uses it. */
interface DownloadService {
    val tasks: StateFlow<List<DownloadTask>>
    val events: SharedFlow<DownloadEvent>
    val waitingForWifi: StateFlow<Boolean>

    fun enqueue(request: DownloadRequest): EnqueueResult
    fun pause(id: String)
    fun resume(id: String)
    fun cancel(id: String)
    fun remove(ids: Collection<String>)
    fun pauseAll(): Int
    fun resumeAll(): Int
    fun configure(config: DownloaderConfig)
    fun locationOf(destination: Destination): String
}
