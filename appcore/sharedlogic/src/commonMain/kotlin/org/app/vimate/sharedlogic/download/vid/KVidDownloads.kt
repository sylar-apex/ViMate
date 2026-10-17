package org.app.vimate.sharedlogic.download.vid

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.app.kdownloader.KDownloader
import org.app.kdownloader.UrlRefresher
import org.app.kdownloader.model.DownloadEvent
import org.app.kdownloader.model.DownloadPart
import org.app.kdownloader.model.DownloadTask
import org.app.kdownloader.model.DownloaderConfig
import org.app.kdownloader.model.TaskExtras
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.kdownloader.model.Folder
import org.app.kdownloader.model.MediaType
import org.app.kdownloader.model.Destination
import org.app.vimate.sharedlogic.yt.YouTube

/**
 * POC `Downloads` on [KDownloader]: settings drive the queue, YouTube links are re-resolved by
 * videoId + itag, finished files get a thumbnail/duration ([MediaProbe]) and an optional SRT subtitle.
 */
class KVidDownloads(
    private val dl: KDownloader,
    private val settings: ObservableSettings,
) : VidDownloads {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val tasks: StateFlow<List<VidTask>> = dl.tasks
        .map { list -> list.sortedByDescending { it.createdAt }.map(VidTaskMapper::toVid) }
        .stateIn(scope, SharingStarted.Eagerly, dl.tasks.value.sortedByDescending { it.createdAt }.map(VidTaskMapper::toVid))

    init {
        dl.urlRefresher = UrlRefresher { task -> refreshUrls(task) }
        scope.launch { settings.version.collect { dl.updateConfig(config()) } }
        scope.launch { dl.events.filterIsInstance<DownloadEvent.Completed>().collect { onCompleted(it.task) } }
    }

    private fun config(): DownloaderConfig = dl.config.value.copy(
        maxParallel = settings[AppKeys.maxTasks],
        connections = settings[AppKeys.connections].coerceIn(1, 8),
        wifiOnly = settings[AppKeys.wifiOnly],
        autoResume = settings[AppKeys.autoResume],
        appFolder = folder(settings),
        userAgent = UA,
    )

    private fun sid(id: Long): String? = dl.tasks.value.firstOrNull { VidTaskMapper.vidId(it) == id }?.id

    override fun speedOf(id: Long): Long = dl.tasks.value.firstOrNull { VidTaskMapper.vidId(it) == id }?.speedBytesPerSec ?: 0

    override fun enqueue(task: VidTask) { dl.enqueue(VidTaskMapper.toRequest(task)) }

    override fun location(): String = dl.locationOf(Destination(MediaType.VIDEO, Folder.DOWNLOADS))

    override fun pause(id: Long) { sid(id)?.let(dl::pause) }

    override fun resume(id: Long) { sid(id)?.let(dl::resume) }

    override fun pauseAll() { dl.pauseAll() }

    override fun resumeAll() { dl.resumeAll() }

    override fun delete(id: Long, withFile: Boolean) { sid(id)?.let { dl.delete(it, withFile) } }

    override fun rename(id: Long, newTitle: String) { sid(id)?.let { dl.rename(it, newTitle) } }

    override fun setHidden(id: Long, hidden: Boolean): Job =
        sid(id)?.let { dl.setHidden(it, hidden) } ?: Job().apply { complete() }

    override fun convertToMusic(id: Long, done: (Boolean) -> Unit): Job = scope.launch {
        val source = dl.tasks.value.firstOrNull { VidTaskMapper.vidId(it) == id }
        val ok = source != null && dl.convertToMusic(
            source.id,
            extras = source.extras - TaskExtras.QUALITY + mapOf(
                TaskExtras.EXT to "m4a", TaskExtras.KIND to Kind.Music.name, VidTaskMapper.VID_ID to nowMs().toString(),
            ),
        ) != null
        withContext(Dispatchers.Main) { done(ok) }
    }

    override fun playableUri(t: VidTask): String? = t.outputUri ?: t.outputPath

    override fun update(id: Long, transform: (VidTask) -> VidTask) {
        val sid = sid(id) ?: return
        dl.update(sid) { d -> d.copy(extras = d.extras + VidTaskMapper.extrasOf(transform(VidTaskMapper.toVid(d)))) }
    }

    /** POC refreshUrls: googlevideo links last ~6 h; re-resolve by videoId and match parts by itag. */
    private suspend fun refreshUrls(task: DownloadTask): List<DownloadPart>? {
        val vid = task.extras[TaskExtras.VIDEO_ID] ?: return null
        val info = YouTube.info(vid)
        val all = info.muxed + info.videoOnly + info.audioOnly
        return task.parts.map { p ->
            all.find { it.itag.toString() == p.key }?.let { p.copy(url = it.url, headers = p.headers + ("User-Agent" to YouTube.streamUA)) } ?: p
        }
    }

    private suspend fun onCompleted(task: DownloadTask) {
        val uri = task.localUri ?: return
        if (task.extras[TaskExtras.THUMBNAIL].isNullOrEmpty() || task.extras[TaskExtras.DURATION].isNullOrEmpty()) runCatching {
            // POC describe(): a frame at 1 s as thumbnail, and the duration, for files that came without them.
            MediaProbe.describe(uri, task.id)?.let { (thumb, ms) ->
                dl.update(task.id) { d ->
                    d.copy(extras = d.extras + mapOf(
                        TaskExtras.THUMBNAIL to d.extras[TaskExtras.THUMBNAIL].orEmpty().ifEmpty { thumb.orEmpty() },
                        TaskExtras.DURATION to d.extras[TaskExtras.DURATION].orEmpty().ifEmpty { if (ms > 0) YouTube.formatDuration(ms / 1000) else "" },
                    ))
                }
            }
        }
        val sub = task.extras[TaskExtras.SUBTITLE_URL]
        if (sub != null && settings[AppKeys.downloadSubtitle]) runCatching {
            val srt = Subtitles.toSrt(YouTube.http.get("$sub&fmt=srv3").bodyAsText())
            MediaProbe.saveText(folder(settings), task.fileName.substringBeforeLast('.') + ".srt", srt)
        }
    }

    companion object {
        const val UA = "com.google.android.apps.youtube.vr.oculus/1.62.27 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip"

        /** POC: `Download/<folder>/download`; a picked absolute folder is used as is. */
        fun folder(settings: ObservableSettings): String {
            val f = settings[AppKeys.downloadFolder]
            return if (f.startsWith("/") || Regex("^[A-Za-z]:[\\\\/]").containsMatchIn(f)) f else "$f/download"
        }
    }
}

/** Converts YouTube's srv3 timed-text XML into SRT. */
object Subtitles {
    fun toSrt(xml: String): String {
        val sb = StringBuilder()
        Regex("""<p t="(\d+)"(?: d="(\d+)")?[^>]*>(.*?)</p>""", RegexOption.DOT_MATCHES_ALL).findAll(xml).forEachIndexed { i, m ->
            val start = m.groupValues[1].toLong(); val dur = m.groupValues[2].toLongOrNull() ?: 2000
            val text = m.groupValues[3].replace(Regex("<[^>]+>"), "").replace("&amp;", "&").replace("&#39;", "'").replace("&quot;", "\"").trim()
            if (text.isEmpty()) return@forEachIndexed
            sb.append(i + 1).append('\n').append(ts(start)).append(" --> ").append(ts(start + dur)).append('\n').append(text).append("\n\n")
        }
        return sb.toString()
    }

    private fun p(n: Long, w: Int) = n.toString().padStart(w, '0')
    private fun ts(ms: Long) = "${p(ms / 3600000, 2)}:${p(ms / 60000 % 60, 2)}:${p(ms / 1000 % 60, 2)},${p(ms % 1000, 3)}"
}

/** Platform media helpers for finished downloads. */
expect object MediaProbe {
    /** Thumbnail uri (frame at 1 s) and duration ms of [uri], or null where unsupported. */
    fun describe(uri: String, id: String): Pair<String?, Long>?

    /** Saves a text file (subtitle) next to downloads under `Download/<folder>`. */
    fun saveText(folder: String, name: String, text: String)
}
