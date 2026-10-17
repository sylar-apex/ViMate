package org.app.vimate.sharedlogic.download.vid

import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import org.app.kdownloader.KDownloaderAndroid

actual object MediaProbe {
    /** POC Downloads.describe: frame at 1 s saved as JPEG, plus the duration. */
    actual fun describe(uri: String, id: String): Pair<String?, Long>? {
        val context = KDownloaderAndroid.context
        val mmr = MediaMetadataRetriever()
        try {
            if (uri.startsWith("content:")) mmr.setDataSource(context, Uri.parse(uri)) else mmr.setDataSource(uri.removePrefix("file://"))
            val ms = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
            val thumb = mmr.getFrameAtTime(1_000_000)?.let { bmp ->
                val f = File(File(context.filesDir, "thumbs").apply { mkdirs() }, "$id.jpg")
                f.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 85, it) }
                Uri.fromFile(f).toString()
            }
            return thumb to ms
        } finally { mmr.release() }
    }

    actual fun saveText(folder: String, name: String, text: String) {
        val context = KDownloaderAndroid.context
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/x-subrip")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$folder")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return
            context.contentResolver.openOutputStream(uri)?.use { it.write(text.encodeToByteArray()) }
        } else {
            @Suppress("DEPRECATION")
            val dst = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "$folder/$name").apply { parentFile?.mkdirs() }
            dst.writeText(text)
        }
    }
}
