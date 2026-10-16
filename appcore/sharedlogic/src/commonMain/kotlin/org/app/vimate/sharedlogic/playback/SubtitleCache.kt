package org.app.vimate.sharedlogic.playback

/** Saves subtitle text where the player can read it back; returns its uri, or null where unsupported. */
expect object SubtitleCache {
    fun write(name: String, text: String): String?
}
