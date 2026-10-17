package org.app.vimate.sites.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import java.io.ByteArrayOutputStream
import java.io.File

actual object MediaProbe {
    actual fun imageBounds(bytes: ByteArray): Pair<Int, Int>? {
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, o)
        return if (o.outWidth > 0) o.outWidth to o.outHeight else null
    }

    actual fun imageThumb(bytes: ByteArray, width: Int, height: Int): ByteArray? = runCatching {
        val sample = generateSequence(1) { it * 2 }.first { width / it <= 320 || height / it <= 320 }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })?.let(::encode)
    }.getOrNull()

    actual fun media(url: String, headers: Map<String, String>, frame: Boolean): ProbeMedia? = runCatching {
        val m = MediaMetadataRetriever()
        try {
            m.setDataSource(url, headers)
            ProbeMedia(
                m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0,
                m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0,
                m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0,
                if (frame) m.getFrameAtTime(1_000_000)?.let { encode(scaled(it)) } else null,
            )
        } finally { m.release() }
    }.getOrNull()

    actual fun segmentFrame(bytes: ByteArray): ByteArray? = runCatching {
        val tmp = File.createTempFile("seg", ".ts")
        try {
            tmp.writeBytes(bytes)
            val m = MediaMetadataRetriever()
            try { m.setDataSource(tmp.path); m.getFrameAtTime(0)?.let { encode(scaled(it)) } } finally { m.release() }
        } finally { tmp.delete() }
    }.getOrNull()

    private fun scaled(b: Bitmap) = Bitmap.createScaledBitmap(b, 320, (320f * b.height / b.width).toInt().coerceAtLeast(1), true)

    private fun encode(b: Bitmap): ByteArray = ByteArrayOutputStream().also { b.compress(Bitmap.CompressFormat.JPEG, 85, it) }.toByteArray()
}
