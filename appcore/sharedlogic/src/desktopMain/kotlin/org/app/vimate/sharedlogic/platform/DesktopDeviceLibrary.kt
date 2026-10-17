package org.app.vimate.sharedlogic.platform

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import org.app.vimate.sharedlogic.model.DeviceMedia
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.ImportedMedia

/**
 * Desktop "Videos" / "Music" tabs: media files in the app's download folder (where downloads land) and the
 * user's Movies / Music folders, newest first. No permission is needed on desktop.
 */
class DesktopDeviceLibrary(private val downloadDir: () -> String) : DeviceLibrary {
    override val available = true
    override val imports: SharedFlow<List<ImportedMedia>> = MutableSharedFlow()
    override fun hasAccess() = true
    override fun requestAccess() {}

    override suspend fun load(kind: FileKind): List<DeviceMedia> = withContext(Dispatchers.IO) {
        val home = System.getProperty("user.home")
        val roots = listOf(File(downloadDir()), File(home, if (kind == FileKind.MUSIC) "Music" else "Movies"))
        val exts = if (kind == FileKind.MUSIC) AUDIO else VIDEO
        roots.filter { it.isDirectory }
            .flatMap { root -> root.walkTopDown().maxDepth(3).filter { it.isFile && it.extension.lowercase() in exts && !it.name.startsWith(".") }.toList() }
            .distinctBy { it.canonicalPath }
            .sortedByDescending { it.lastModified() }
            .map { f -> DeviceMedia(f.toURI().toString(), f.nameWithoutExtension, f.length(), f.extension.uppercase(), f.lastModified(), 0, kind) }
    }

    private companion object {
        val VIDEO = setOf("mp4", "mkv", "webm", "mov", "m4v", "avi", "flv", "3gp", "ts", "wmv")
        val AUDIO = setOf("m4a", "mp3", "aac", "opus", "ogg", "wav", "flac", "weba")
    }
}
