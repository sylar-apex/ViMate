package org.app.vimate.downloads.usecase

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.download.option.DownloadOption
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.option.Options
import org.app.vimate.sharedlogic.download.vid.Part
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.app.vimate.sharedlogic.yt.YouTube

/** Download sheet logic (POC DetailScreen DownloadSheet): formats, default choice, path, storage, enqueue. */
class DownloadSheetUseCase(
    private val downloads: VidDownloads,
    private val settings: ObservableSettings,
    private val platform: PlatformActions,
) {
    suspend fun info(id: String): VideoInfo = YouTube.info(id)

    fun options(info: VideoInfo): Pair<List<DownloadOption>, List<DownloadOption>> = Options.of(info)

    fun defaultOption(opts: Pair<List<DownloadOption>, List<DownloadOption>>): DownloadOption? =
        Options.default(opts.second, settings[AppKeys.onlineQuality]) ?: opts.first.firstOrNull()

    fun folder(): String = settings[AppKeys.downloadFolder]

    fun storage(): StorageInfo = platform.storageInfo()

    /** Where downloads are saved now. */
    fun location(): String = downloads.location()

    /** Opens the platform folder picker and saves the choice; false when the platform has no picker or it was cancelled. */
    fun changeFolder(): Boolean {
        val picked = platform.pickFolder() ?: return false
        settings[AppKeys.downloadFolder] = picked
        return true
    }

    fun enqueue(v: Video, info: VideoInfo, o: DownloadOption, title: String) {
        downloads.enqueue(VidTask(
            id = nowMs(), title = title.trim().ifEmpty { info.video.title }, ext = o.ext, kind = o.kind,
            quality = if (o.kind == Kind.Video) o.label.substringBefore(' ') else o.label,
            thumbnail = v.thumbnail.ifEmpty { info.video.thumbnail }, duration = info.video.duration, pageUrl = info.video.url,
            videoId = info.video.id, parts = o.parts.map { Part(it.url, it.sizeBytes, it.itag, headers = mapOf("User-Agent" to YouTube.streamUA)) },
            subtitleUrl = info.captions.firstOrNull()?.url, totalBytes = o.sizeBytes,
        ))
    }
}
