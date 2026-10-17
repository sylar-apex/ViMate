package org.app.vimate.sharedlogic.download.vid

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.download.option.Kind

/** Download state as the app shows it (POC `TaskState`). */
@Serializable
enum class TaskState { Queued, Running, Paused, Merging, Done, Failed }

/** One HTTP resource of a task. YouTube video downloads have two (video + audio) merged at the end. */
@Serializable
data class Part(
    val url: String,
    val size: Long = -1,
    val itag: Int = 0,
    val doneSegments: Set<Int> = emptySet(),
    val hls: Boolean = false,
    /** Extra request headers (browser downloads carry the page's User-Agent, Referer and cookies). */
    val headers: Map<String, String> = emptyMap(),
    /** Web pages/docs grabbed on purpose: an HTML response is the content, not an error. */
    val allowHtml: Boolean = false,
)

/** A download as the screens build and list it (POC `DownloadTask`). */
@Serializable
data class VidTask(
    val id: Long,
    val title: String,
    val ext: String,
    val kind: Kind,
    val quality: String = "",
    val thumbnail: String = "",
    val duration: String = "",
    val pageUrl: String = "",
    val videoId: String? = null,
    val parts: List<Part>,
    val subtitleUrl: String? = null,
    val state: TaskState = TaskState.Queued,
    val totalBytes: Long = -1,
    val doneBytes: Long = 0,
    val error: String? = null,
    val outputUri: String? = null,
    val outputPath: String? = null,
    val createdAt: Long = nowMs(),
    val finishedAt: Long = 0,
    val hidden: Boolean = false,
) {
    val fileName get() = "${title.replace(Regex("""[\\/:*?"<>|]"""), "_").take(120)}${if (quality.isNotEmpty()) "($quality)" else ""}.$ext"
    val progress get() = if (totalBytes > 0) doneBytes.toFloat() / totalBytes else 0f
}

/**
 * The POC's download manager API (`Downloads`), implemented on KDownloader. Screens use exactly
 * these calls, as the POC's screens did.
 */
interface VidDownloads {
    val tasks: StateFlow<List<VidTask>>
    fun speedOf(id: Long): Long
    fun enqueue(task: VidTask)
    fun pause(id: Long)
    fun resume(id: Long)
    fun pauseAll()
    fun resumeAll()
    fun delete(id: Long, withFile: Boolean = true)
    fun rename(id: Long, newTitle: String)
    fun setHidden(id: Long, hidden: Boolean): Job
    fun convertToMusic(id: Long, done: (Boolean) -> Unit): Job
    fun playableUri(t: VidTask): String?
    fun update(id: Long, transform: (VidTask) -> VidTask)
}
