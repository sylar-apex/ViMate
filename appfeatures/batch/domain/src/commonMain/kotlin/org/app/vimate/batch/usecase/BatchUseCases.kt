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
/**
 * Queues every picked video right away with the wanted quality; its formats are read when that download starts,
 * taking the nearest available quality there (see KVidDownloads.prepare). Returns how many were queued.
 */
class EnqueueBatchUseCase(private val downloads: VidDownloads) {
    operator fun invoke(videos: List<Video>, sel: String): Int {
        val label = sel.substringBefore('|')
        val music = label.endsWith("K")
        val base = nowMs()
        videos.forEachIndexed { i, v ->
            downloads.enqueue(VidTask(
                id = base * 100 + i, title = v.title, ext = if (music) "m4a" else "mp4", kind = if (music) Kind.Music else Kind.Video,
                quality = label, thumbnail = v.thumbnail, duration = v.duration, pageUrl = v.url, videoId = v.id, parts = emptyList(),
            ))
        }
        return videos.size
    }
}

/** Qualities offered for a batch: fixed, so the sheet needs no per-video lookups. */
object BatchQualities {
    val music = listOf(BatchOption("128K", "M4A", 0), BatchOption("48K", "M4A", 0))
    val video = listOf("144P", "240P", "360P", "480P", "720P", "1080P").map { BatchOption(it, "MP4", 0) }
}
