package org.app.vimate.sharedlogic.download.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.app.kdownloader.model.DownloaderConfig
import org.app.kdownloader.model.EnqueueResult
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.download.mapper.DownloadMapper
import org.app.vimate.sharedlogic.download.model.DownloadItem
import org.app.vimate.sharedlogic.download.model.DownloadRequestData
import org.app.vimate.sharedlogic.download.model.EnqueueOutcome
import org.app.vimate.sharedlogic.download.remote.DownloadService
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.settings.DownloadFolder

/** Keeps the download engine in step with the download settings and maps its tasks for the app. */
class DownloadRepositoryImpl(
    private val service: DownloadService,
    private val mapper: DownloadMapper,
    private val settings: ObservableSettings,
) : DownloadRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch { settings.version.collect { service.configure(engineConfig()) } }
    }

    override val downloads: Flow<List<DownloadItem>> =
        service.tasks.map { tasks -> tasks.sortedByDescending { it.createdAt }.map(mapper::toItem) }

    override val waitingForWifi: Flow<Boolean> = service.waitingForWifi

    override fun enqueue(data: DownloadRequestData): EnqueueOutcome {
        val folder = DownloadFolder.fromWire(settings[AppKeys.downloadFolder])
        return when (service.enqueue(mapper.toRequest(data, folder))) {
            is EnqueueResult.Added -> EnqueueOutcome.ADDED
            is EnqueueResult.Duplicate -> EnqueueOutcome.DUPLICATE
        }
    }

    override fun pause(id: String) = service.pause(id)
    override fun resume(id: String) = service.resume(id)
    override fun cancel(id: String) = service.cancel(id)
    override fun delete(ids: Collection<String>) = service.remove(ids)
    override fun pauseAll() = service.pauseAll()
    override fun resumeAll() = service.resumeAll()
    override fun location(kind: FileKind, folder: DownloadFolder) = service.locationOf(mapper.destination(kind, folder))

    /** Wi-Fi only, task limit, fast download (two connections) and auto resume from settings. */
    private fun engineConfig() = DownloaderConfig(
        maxParallel = settings[AppKeys.maxTasks],
        wifiOnly = settings[AppKeys.wifiOnly],
        connections = if (settings[AppKeys.fastDownload]) FAST_CONNECTIONS else 1,
        autoResume = settings[AppKeys.autoResume],
        appFolder = APP_FOLDER,
        userAgent = USER_AGENT,
    )

    companion object {
        const val APP_FOLDER = "ViMate"
        const val USER_AGENT = "ViMate/1.0"
        private const val FAST_CONNECTIONS = 2
    }
}
