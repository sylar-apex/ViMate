package org.app.vimate.files.usecase

import kotlinx.coroutines.flow.StateFlow
import org.app.kmediaplayer.model.MediaFile
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.platform.DeviceLibrary
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** My Files logic (POC ui/FilesScreen.kt): downloads, device media (POC LocalMedia), private PIN, playback, share. */
class FilesUseCase(
    val downloads: VidDownloads,
    private val device: DeviceLibrary,
    private val settings: ObservableSettings,
    private val library: Library,
    private val launcher: PlaybackLauncher,
    private val platform: PlatformActions,
) {
    val tasks: StateFlow<List<VidTask>> get() = downloads.tasks

    fun storage(): StorageInfo = platform.storageInfo()

    suspend fun hotVideos(): List<Video> = runCatching { YouTube.feed("trending songs").items.take(6) }.getOrDefault(emptyList())

    /** POC LocalMedia.query: newest first; hidden (dot) folders only with Settings › Show hidden files. */
    suspend fun localMedia(audio: Boolean): List<MediaFile> {
        if (!device.hasAccess()) device.requestAccess()
        val showHidden = settings[AppKeys.showHidden]
        return device.load(if (audio) FileKind.MUSIC else FileKind.VIDEO).map { m ->
            MediaFile(m.uri.hashCode().toLong(), m.uri, m.uri.removePrefix("file://"), m.title + if (m.format.isNotEmpty() && !m.title.endsWith(".${m.format}", true)) ".${m.format.lowercase()}" else "",
                m.bytes, m.durationMs, m.dateMs, isAudio = audio)
        }.filter { showHidden || !it.isHidden }
    }

    fun playLocal(all: List<MediaFile>, index: Int, audio: Boolean) = launcher.playFiles(all, index, audio)

    /** POC playTask: plays [t] within [list]; music (or "Play as Music") in the music player. */
    fun playTask(t: VidTask, list: List<VidTask>, asMusic: Boolean = false) {
        val files = list.mapNotNull { x ->
            val uri = downloads.playableUri(x) ?: return@mapNotNull null
            MediaFile(x.id, uri, x.outputPath.orEmpty(), x.fileName, x.totalBytes, 0, x.finishedAt, isAudio = asMusic || x.kind == Kind.Music, album = x.thumbnail)
        }
        val i = files.indexOfFirst { it.id == t.id }.coerceAtLeast(0)
        library.localHistory.update { listOf(t.title) + it.filter { o -> o != t.title } }
        if (asMusic || t.kind == Kind.Music) launcher.playFiles(files, i, asMusic = true)
        else {
            val videos = files.filter { !it.isAudio }
            launcher.playFiles(videos, videos.indexOfFirst { it.id == t.id }.coerceAtLeast(0), asMusic = false)
        }
    }

    /** False when the file is hidden (no shareable uri). */
    fun shareTask(t: VidTask): Boolean {
        val uri = t.outputUri?.takeIf { !t.hidden } ?: return false
        platform.share("", uri)
        return true
    }

    fun shareFile(f: MediaFile) = platform.share("", f.uri)

    var privatePin: String
        get() = settings[AppKeys.privatePin]
        set(value) { settings[AppKeys.privatePin] = value }
}
