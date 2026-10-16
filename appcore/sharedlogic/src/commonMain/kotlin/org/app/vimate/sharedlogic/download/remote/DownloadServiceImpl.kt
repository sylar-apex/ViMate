package org.app.vimate.sharedlogic.download.remote

import org.app.kdownloader.KDownloader
import org.app.kdownloader.model.Destination
import org.app.kdownloader.model.DownloadRequest
import org.app.kdownloader.model.DownloaderConfig

/** [DownloadService] backed by KDownloader. */
class DownloadServiceImpl(private val downloader: KDownloader) : DownloadService {
    override val tasks get() = downloader.tasks
    override val events get() = downloader.events
    override val waitingForWifi get() = downloader.waitingForWifi

    override fun enqueue(request: DownloadRequest) = downloader.enqueue(request)
    override fun pause(id: String) = downloader.pause(id)
    override fun resume(id: String) = downloader.resume(id)
    override fun cancel(id: String) = downloader.cancel(id)
    override fun remove(ids: Collection<String>) = downloader.remove(ids)
    override fun pauseAll() = downloader.pauseAll()
    override fun resumeAll() = downloader.resumeAll()
    override fun configure(config: DownloaderConfig) = downloader.updateConfig(config)
    override fun locationOf(destination: Destination) = downloader.locationOf(destination)
}
