package org.app.vimate.sites.repository

/** Platform decoders the grabber probes with (POC BitmapFactory / MediaMetadataRetriever). */
expect object MediaProbe {
    /** Pixel size from image header bytes, or null. */
    fun imageBounds(bytes: ByteArray): Pair<Int, Int>?
    /** A thumbnail (≤ ~320px, encoded) from complete image bytes, or null when they don't decode. */
    fun imageThumb(bytes: ByteArray, width: Int, height: Int): ByteArray?
    /** Width/height/duration (and a frame at 1s when [frame]) of a remote video/audio. */
    fun media(url: String, headers: Map<String, String>, frame: Boolean): ProbeMedia?
    /** First frame of a downloaded HLS segment. */
    fun segmentFrame(bytes: ByteArray): ByteArray?
}

data class ProbeMedia(val width: Int, val height: Int, val durationMs: Long, val frame: ByteArray?)
