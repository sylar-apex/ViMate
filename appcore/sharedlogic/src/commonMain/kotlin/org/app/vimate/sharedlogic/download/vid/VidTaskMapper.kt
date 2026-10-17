package org.app.vimate.sharedlogic.download.vid

import org.app.kdownloader.model.Destination
import org.app.kdownloader.model.DownloadPart
import org.app.kdownloader.model.DownloadRequest
import org.app.kdownloader.model.DownloadStatus
import org.app.kdownloader.model.DownloadTask
import org.app.kdownloader.model.Folder
import org.app.kdownloader.model.MediaType
import org.app.kdownloader.model.TaskExtras
import org.app.kdownloader.model.duration
import org.app.kdownloader.model.ext
import org.app.kdownloader.model.pageUrl
import org.app.kdownloader.model.quality
import org.app.kdownloader.model.subtitleUrl
import org.app.kdownloader.model.thumbnail
import org.app.kdownloader.model.title
import org.app.kdownloader.model.videoId
import org.app.vimate.sharedlogic.download.option.Kind

/** Maps the app's [VidTask] (POC DownloadTask) to KDownloader requests/tasks and back. */
internal object VidTaskMapper {
    /** Extras key holding the app's numeric task id. */
    const val VID_ID = "vidId"

    fun vidId(task: DownloadTask): Long = task.extras[VID_ID]?.toLongOrNull() ?: task.id.hashCode().toLong()

    fun extrasOf(t: VidTask): Map<String, String> = buildMap {
        put(VID_ID, t.id.toString())
        put(TaskExtras.TITLE, t.title)
        put(TaskExtras.EXT, t.ext)
        put(TaskExtras.KIND, t.kind.name)
        put(TaskExtras.QUALITY, t.quality)
        put(TaskExtras.THUMBNAIL, t.thumbnail)
        put(TaskExtras.DURATION, t.duration)
        put(TaskExtras.PAGE_URL, t.pageUrl)
        t.videoId?.let { put(TaskExtras.VIDEO_ID, it) }
        t.subtitleUrl?.let { put(TaskExtras.SUBTITLE_URL, it) }
    }

    fun toRequest(t: VidTask): DownloadRequest = DownloadRequest(
        url = t.pageUrl.ifEmpty { t.parts.firstOrNull()?.url.orEmpty() } + "#${t.id}",
        fileName = t.fileName,
        destination = Destination(
            when (t.kind) { Kind.Video -> MediaType.VIDEO; Kind.Music -> MediaType.AUDIO; else -> MediaType.OTHER },
            Folder.DOWNLOADS,
        ),
        totalBytesHint = t.totalBytes.coerceAtLeast(0),
        extras = extrasOf(t),
        parts = t.parts.map { DownloadPart(it.url, it.headers, it.size, it.allowHtml, it.hls, it.itag.toString(), it.doneSegments) },
    )

    fun state(s: DownloadStatus): TaskState = when (s) {
        DownloadStatus.QUEUED -> TaskState.Queued
        DownloadStatus.RUNNING -> TaskState.Running
        DownloadStatus.PAUSED -> TaskState.Paused
        DownloadStatus.MERGING -> TaskState.Merging
        DownloadStatus.COMPLETED -> TaskState.Done
        DownloadStatus.FAILED, DownloadStatus.CANCELED -> TaskState.Failed
    }

    fun toVid(d: DownloadTask): VidTask = VidTask(
        id = vidId(d),
        title = d.title,
        ext = d.ext,
        kind = d.extras[TaskExtras.KIND]?.let { k -> Kind.entries.firstOrNull { it.name == k } }
            ?: when (d.destination.type) { MediaType.VIDEO -> Kind.Video; MediaType.AUDIO -> Kind.Music; else -> Kind.Other },
        quality = d.quality,
        thumbnail = d.thumbnail,
        duration = d.duration,
        pageUrl = d.pageUrl,
        videoId = d.videoId,
        parts = d.parts.map { Part(it.url, it.size, it.key.toIntOrNull() ?: 0, it.doneSegments, it.hls, it.headers, it.allowHtml) },
        subtitleUrl = d.subtitleUrl,
        state = state(d.status),
        totalBytes = if (d.totalBytes > 0) d.totalBytes else -1,
        doneBytes = d.downloadedBytes,
        error = d.error,
        outputUri = d.localUri,
        outputPath = d.localUri?.takeIf { it.startsWith("/") || it.startsWith("file:") }?.removePrefix("file://"),
        createdAt = d.createdAt,
        finishedAt = d.finishedAt,
        hidden = d.hidden,
    )
}
