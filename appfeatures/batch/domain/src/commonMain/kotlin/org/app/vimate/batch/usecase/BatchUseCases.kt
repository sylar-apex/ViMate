package org.app.vimate.batch.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.batch.model.BatchOption
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

/** Resolves every video's formats, 4 at a time; [onResolved] runs after each (success or not). */
class ResolveBatchInfosUseCase {
    suspend operator fun invoke(videos: List<Video>, onResolved: (Video, VideoInfo?) -> Unit) = coroutineScope {
        val gate = Semaphore(4)
        videos.map { v -> async(Dispatchers.Default) { gate.withPermit { onResolved(v, runCatching { YouTube.info(v.id) }.getOrNull()) } } }.awaitAll()
    }
}

/** Labels offered: union of what each video has; size = sum over videos that have that label. Music, then Video by height. */
class BatchOptionsUseCase {
    operator fun invoke(infos: Collection<VideoInfo>): Pair<List<BatchOption>, List<BatchOption>> {
        val perVideo = infos.map { Options.of(it) }
        fun totals(pick: (Pair<List<DownloadOption>, List<DownloadOption>>) -> List<DownloadOption>) =
            perVideo.flatMap(pick).groupBy { it.label + "|" + it.container }
                .map { (k, list) -> BatchOption(k.substringBefore('|'), k.substringAfter('|'), list.sumOf { it.sizeBytes }) }
        val music = totals { it.first }
        val video = totals { it.second }.sortedBy { if (it.label == "4K") 2160 else it.label.filter(Char::isDigit).toIntOrNull() ?: 0 }
        return music to video
    }
}

/** Path and free space lines of the sheet. */
class BatchStorageUseCase(private val platform: PlatformActions, private val settings: ObservableSettings) {
    val folder: String get() = settings[AppKeys.downloadFolder]
    fun storage(): StorageInfo = platform.storageInfo()
}

/** POC BatchSheet "Download": one VidTask per resolved video in the picked format; returns how many were added. */
class EnqueueBatchUseCase(private val downloads: VidDownloads) {
    operator fun invoke(videos: List<Video>, infos: Map<String, VideoInfo>, sel: String): Int {
        val (label, cont) = sel.substringBefore('|') to sel.substringAfter('|')
        var added = 0
        val base = nowMs()
        videos.forEachIndexed { i, v ->
            val info = infos[v.id] ?: return@forEachIndexed
            val (m, vd) = Options.of(info)
            val o = (m + vd).find { it.label == label && it.container == cont }
                ?: if (vd.any { it.label == label }) null else Options.default(vd, label) ?: vd.lastOrNull()
            o ?: return@forEachIndexed
            downloads.enqueue(VidTask(
                id = base * 100 + i, title = info.video.title, ext = o.ext, kind = o.kind,
                quality = if (o.kind == Kind.Video) o.label.substringBefore(' ') else o.label,
                thumbnail = v.thumbnail.ifEmpty { info.video.thumbnail }, duration = info.video.duration, pageUrl = info.video.url,
                videoId = info.video.id, parts = o.parts.map { Part(it.url, it.sizeBytes, it.itag, headers = mapOf("User-Agent" to YouTube.streamUA)) },
                subtitleUrl = info.captions.firstOrNull()?.url, totalBytes = o.sizeBytes,
            ))
            added++
        }
        return added
    }
}
