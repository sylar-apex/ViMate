package org.app.vimate.sites.repository

/** No synchronous decoder here: the grabber falls back to loading the URL itself for thumbnails. */
actual object MediaProbe {
    actual fun imageBounds(bytes: ByteArray): Pair<Int, Int>? = null
    actual fun imageThumb(bytes: ByteArray, width: Int, height: Int): ByteArray? = null
    actual fun media(url: String, headers: Map<String, String>, frame: Boolean): ProbeMedia? = null
    actual fun segmentFrame(bytes: ByteArray): ByteArray? = null
}
