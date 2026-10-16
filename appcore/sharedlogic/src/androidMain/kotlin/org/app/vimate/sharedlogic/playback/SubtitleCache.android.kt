package org.app.vimate.sharedlogic.playback

import java.io.File
import org.app.kmediaplayer.android.KMediaPlayerAndroid

actual object SubtitleCache {
    actual fun write(name: String, text: String): String? = runCatching {
        val dir = File(KMediaPlayerAndroid.context.cacheDir, "subtitles").apply { mkdirs() }
        File(dir, name).apply { writeText(text) }.toURI().toString()
    }.getOrNull()
}
