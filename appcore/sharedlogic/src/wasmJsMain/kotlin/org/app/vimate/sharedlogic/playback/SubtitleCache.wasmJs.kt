package org.app.vimate.sharedlogic.playback

import org.app.kmediaplayer.web.WebBlobStore

/** Keeps the text in the player's in-memory blob store under a made-up url. */
actual object SubtitleCache {
    actual fun write(name: String, text: String): String? = "vimate-subtitle:$name".also { WebBlobStore.put(it, text.encodeToByteArray()) }
}
