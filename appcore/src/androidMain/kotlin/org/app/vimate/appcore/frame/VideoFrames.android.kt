package org.app.vimate.appcore.frame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.app.kmediaplayer.android.KMediaPlayerAndroid

private const val MEMORY_FRAMES = 48
private const val PARALLEL_RETRIEVERS = 2
private const val FRAME_WIDTH = 640
private const val FRAME_HEIGHT = 360
private const val JPEG_QUALITY = 82
private const val DARK_LEVEL = 20
private const val DISK_DIR = "frames-trimmed"
private const val USER_AGENT = "ViMate/1.0"

private val frames = LruCache<String, ImageBitmap>(MEMORY_FRAMES)
private val retrievers = Semaphore(PARALLEL_RETRIEVERS)

@Composable
actual fun rememberVideoFrame(url: String, atMs: Long): ImageBitmap? {
    val key = "$url#$atMs"
    var frame by remember(key) { mutableStateOf(frames.get(key)) }
    LaunchedEffect(key) {
        if (frame != null) return@LaunchedEffect
        frame = withContext(Dispatchers.IO) { loadFrame(url, atMs, key) }
    }
    return frame
}

/** Memory cache, then the disk cache, then a fresh extraction saved to disk. */
private suspend fun loadFrame(url: String, atMs: Long, key: String): ImageBitmap? {
    val context = KMediaPlayerAndroid.context
    val dir = File(context.cacheDir, DISK_DIR).apply { mkdirs() }
    val file = File(dir, key.hashCode().toUInt().toString(16) + ".jpg")
    if (file.length() > 0) {
        BitmapFactory.decodeFile(file.path)?.let { bitmap -> return bitmap.asImageBitmap().also { frames.put(key, it) } }
    }
    val bitmap = retrievers.withPermit { extract(url, atMs) } ?: return null
    runCatching { FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) } }
    return bitmap.asImageBitmap().also { frames.put(key, it) }
}

private fun extract(url: String, atMs: Long): Bitmap? {
    val context = KMediaPlayerAndroid.context
    if (url.startsWith("content://") && Build.VERSION.SDK_INT >= 29) {
        return runCatching { context.contentResolver.loadThumbnail(Uri.parse(url), Size(FRAME_WIDTH, FRAME_HEIGHT), null) }
            .getOrNull()?.let(::trimLetterbox)
    }
    val retriever = MediaMetadataRetriever()
    return try {
        if (url.startsWith("content://") || url.startsWith("file://")) retriever.setDataSource(context, Uri.parse(url))
        else retriever.setDataSource(url, mapOf("User-Agent" to USER_AGENT))
        val timeUs = atMs * 1000
        val frame = if (Build.VERSION.SDK_INT >= 27) {
            retriever.getScaledFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, FRAME_WIDTH, FRAME_HEIGHT)
        } else {
            retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        }
        frame?.let(::trimLetterbox)
    } catch (_: Exception) {
        null
    } finally {
        runCatching { retriever.release() }
    }
}

/** Crops black bars above and below the picture. */
private fun trimLetterbox(bitmap: Bitmap): Bitmap {
    val w = bitmap.width
    val row = IntArray(w)
    fun dark(y: Int): Boolean {
        bitmap.getPixels(row, 0, w, 0, y, w, 1)
        return row.all { (it shr 16 and 0xFF) < DARK_LEVEL && (it shr 8 and 0xFF) < DARK_LEVEL && (it and 0xFF) < DARK_LEVEL }
    }
    var top = 0
    var bottom = bitmap.height
    while (top < bitmap.height / 3 && dark(top)) top++
    while (bottom > bitmap.height * 2 / 3 && dark(bottom - 1)) bottom--
    if (top == 0 && bottom == bitmap.height) return bitmap
    return Bitmap.createBitmap(bitmap, 0, top, w, bottom - top)
}
