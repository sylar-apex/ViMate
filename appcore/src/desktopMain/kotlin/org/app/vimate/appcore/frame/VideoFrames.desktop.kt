package org.app.vimate.appcore.frame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.app.kmediaplayer.desktop.VlcThumbnailer
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/** Frames decoded once per file with the bundled libVLC (one at a time), kept for the session. */
private val cache = java.util.concurrent.ConcurrentHashMap<String, ImageBitmap>()

/** A frame of a local video, grabbed by libVLC (works for MKV, MP4, WebM…); null while it loads or for remote urls. */
@Composable
actual fun rememberVideoFrame(url: String, atMs: Long): ImageBitmap? = produceState(cache[url], url) {
    if (value != null || !(url.startsWith("file:") || url.startsWith("/"))) return@produceState
    val path = if (url.startsWith("file:")) java.io.File(java.net.URI(url)).path else url
    val f = VlcThumbnailer.frame(path) ?: return@produceState
    val bmp = Bitmap().apply {
        allocPixels(ImageInfo(f.width, f.height, ColorType.BGRA_8888, ColorAlphaType.OPAQUE))
        installPixels(f.pixels)
    }
    value = bmp.asComposeImageBitmap().also { cache[url] = it }
}.value
