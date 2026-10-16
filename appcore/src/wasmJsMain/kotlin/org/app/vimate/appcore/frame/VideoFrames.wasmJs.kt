package org.app.vimate.appcore.frame

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/** No frame extraction on this platform; thumbnails keep their gradient. */
@Composable
actual fun rememberVideoFrame(url: String, atMs: Long): ImageBitmap? = null
