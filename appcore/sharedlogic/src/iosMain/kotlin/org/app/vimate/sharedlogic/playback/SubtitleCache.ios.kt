package org.app.vimate.sharedlogic.playback

/** The iOS player does not read external subtitle files yet. */
actual object SubtitleCache {
    actual fun write(name: String, text: String): String? = null
}
