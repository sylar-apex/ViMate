package org.app.vimate.player.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.video.ComposePlayerWindow

/** Shows the video player over the app while [KMediaPlayer.videoRequested] is set (desktop, iOS, web). */
@Composable
fun EmbeddedPlayer() {
    val requested by KMediaPlayer.videoRequested.collectAsState()
    if (!requested) return
    val scope = rememberCoroutineScope()
    val window = remember { ComposePlayerWindow(scope) { KMediaPlayer.videoRequested.value = false } }
    LaunchedEffect(window) {
        when {
            window.startPending() -> Unit
            // An inline player asked for full screen: continue its playback.
            KMediaPlayer.queue.value.isNotEmpty() -> window.state.reattach()
            else -> KMediaPlayer.videoRequested.value = false
        }
    }
    ViMatePlayerContent(window)
}
