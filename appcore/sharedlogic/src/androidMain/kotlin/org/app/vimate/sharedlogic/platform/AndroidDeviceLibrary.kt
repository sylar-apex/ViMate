package org.app.vimate.sharedlogic.platform

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import org.app.vimate.sharedlogic.model.DeviceMedia
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.ImportedMedia

/** Reads videos and music through MediaStore, sorted by file modified time like the reference app. */
class AndroidDeviceLibrary(private val context: Context) : DeviceLibrary {
    override val available = true

    private val _imports = MutableSharedFlow<List<ImportedMedia>>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val imports: SharedFlow<List<ImportedMedia>> = _imports.asSharedFlow()

    init {
        AndroidHost.onImported = { _imports.tryEmit(it) }
    }

    override fun hasAccess(): Boolean =
        mediaPermissions().all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

    override fun requestAccess() {
        AndroidHost.mediaAccessLauncher?.invoke()
    }

    override suspend fun load(kind: FileKind): List<DeviceMedia> = withContext(Dispatchers.IO) {
        if (!hasAccess()) return@withContext emptyList()
        val music = kind == FileKind.MUSIC
        val collection = if (music) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            if (music) MediaStore.Audio.Media.DURATION else MediaStore.Video.Media.DURATION,
            MediaStore.MediaColumns.DATA,
        )
        val selection = if (music) "${MediaStore.Audio.Media.IS_MUSIC} != 0" else null
        val out = mutableListOf<DeviceMedia>()
        runCatching {
            context.contentResolver.query(collection, projection, selection, null, "${MediaStore.MediaColumns.DATE_ADDED} DESC")?.use { c ->
                while (c.moveToNext()) {
                    val name = c.getString(1) ?: continue
                    val modified = c.getString(5)?.let { File(it).lastModified() }?.takeIf { it > 0 } ?: (c.getLong(3) * MILLIS)
                    out += DeviceMedia(
                        uri = ContentUris.withAppendedId(collection, c.getLong(0)).toString(),
                        title = name.substringBeforeLast('.'),
                        bytes = c.getLong(2),
                        format = name.substringAfterLast('.', "").uppercase(),
                        dateMs = modified,
                        durationMs = c.getLong(4),
                        kind = kind,
                    )
                }
            }
        }
        out.sortedByDescending { it.dateMs }
    }

    private companion object {
        const val MILLIS = 1000L
    }
}

/** The runtime permissions needed to list the device's videos and music. */
fun mediaPermissions(): Array<String> =
    if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
    else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
