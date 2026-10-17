package org.app.vimate.detail.ui.player

import androidx.compose.runtime.Composable
import org.app.vimate.detail.model.WebStream
import org.app.vimate.sharedlogic.yt.Video

/** YouTube's embedded player, used when stream URLs are unavailable (Android WebView; elsewhere the thumbnail). */
@Composable
expect fun EmbedPlayer(id: String, thumbnail: String)

/** POC WebResolver.capture bound to the platform (Android only); null where there is no WebView. */
@Composable
expect fun rememberWebFallback(): (suspend (videoId: String) -> List<WebStream>)?

/** VidMate "Popup" floating window over other apps (POC PopupPlayer). */
interface PopupController {
    /** False where the platform has no overlay window; the Popup action then does nothing. */
    val supported: Boolean
    fun canDraw(): Boolean
    fun requestPermission()
    fun show(v: Video, videoUrl: String, audioUrl: String?, qualityLabel: String, positionMs: Long)
}

/** Read by the video page after the popup's Expand: resume this video at this position. */
object PopupResume {
    var resume: Pair<String, Long>? = null
}

object NoPopup : PopupController {
    override val supported = false
    override fun canDraw() = false
    override fun requestPermission() {}
    override fun show(v: Video, videoUrl: String, audioUrl: String?, qualityLabel: String, positionMs: Long) {}
}

@Composable
expect fun rememberPopupController(): PopupController
