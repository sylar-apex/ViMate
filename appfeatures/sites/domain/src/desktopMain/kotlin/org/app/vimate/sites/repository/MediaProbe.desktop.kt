package org.app.vimate.sites.repository

import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/** Desktop: image headers via ImageIO; no video frame decoder. */
actual object MediaProbe {
    actual fun imageBounds(bytes: ByteArray): Pair<Int, Int>? = runCatching {
        ImageIO.createImageInputStream(ByteArrayInputStream(bytes)).use { input ->
            val reader = ImageIO.getImageReaders(input).asSequence().firstOrNull() ?: return null
            try { reader.input = input; reader.getWidth(0) to reader.getHeight(0) } finally { reader.dispose() }
        }
    }.getOrNull()

    actual fun imageThumb(bytes: ByteArray, width: Int, height: Int): ByteArray? =
        runCatching { ImageIO.read(ByteArrayInputStream(bytes))?.let { bytes } }.getOrNull()

    actual fun media(url: String, headers: Map<String, String>, frame: Boolean): ProbeMedia? = null
    actual fun segmentFrame(bytes: ByteArray): ByteArray? = null
}
