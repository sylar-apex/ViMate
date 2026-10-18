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
        // Each root and folder is read on its own: macOS blocks some (e.g. ~/Music/Music) and one refusal must not
        // empty the whole list.
        roots.filter { runCatching { it.isDirectory }.getOrDefault(false) }
            .flatMap { root -> scan(root, exts, depth = 3) }
            .distinctBy { it.absolutePath }
            .sortedByDescending { it.lastModified() }
            .map { f -> DeviceMedia(f.toURI().toString(), f.nameWithoutExtension, f.length(), f.extension.uppercase(), f.lastModified(), 0, kind) }
    }

    private fun scan(dir: File, exts: Set<String>, depth: Int): List<File> {
        val entries = runCatching { dir.listFiles() }.getOrNull() ?: return emptyList()
        return entries.flatMap { f ->
            when {
                f.name.startsWith(".") -> emptyList()
                runCatching { f.isFile }.getOrDefault(false) -> if (f.extension.lowercase() in exts) listOf(f) else emptyList()
                depth > 1 && runCatching { f.isDirectory }.getOrDefault(false) -> scan(f, exts, depth - 1)
                else -> emptyList()
            }
        }
    }

    private companion object {
        val VIDEO = setOf("mp4", "mkv", "webm", "mov", "m4v", "avi", "flv", "3gp", "ts", "wmv")
        val AUDIO = setOf("m4a", "mp3", "aac", "opus", "ogg", "wav", "flac", "weba")
    }
}
