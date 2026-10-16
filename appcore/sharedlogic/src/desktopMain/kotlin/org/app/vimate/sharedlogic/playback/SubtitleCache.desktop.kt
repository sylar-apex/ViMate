package org.app.vimate.sharedlogic.playback

import java.io.File

actual object SubtitleCache {
    actual fun write(name: String, text: String): String? = runCatching {
        val dir = File(System.getProperty("java.io.tmpdir"), "vimate/subtitles").apply { mkdirs() }
        File(dir, name).apply { writeText(text) }.toURI().toString()
    }.getOrNull()
}
