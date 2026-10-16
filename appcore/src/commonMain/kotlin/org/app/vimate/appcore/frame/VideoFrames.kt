package org.app.vimate.appcore.frame

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/** A decoded frame of the video at [url], or null while loading or where frame extraction is unavailable. */
@Composable
expect fun rememberVideoFrame(url: String, atMs: Long): ImageBitmap?
