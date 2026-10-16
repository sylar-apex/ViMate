package org.app.vimate.player.ui

import androidx.compose.runtime.Composable
import org.app.kmediaplayer.ui.video.PlayerWindow
import org.app.kmediaplayer.ui.video.VideoPlayerScreen
import org.app.vimate.appcore.theme.ViTheme

/** The library's video player inside ViMate's theme. */
@Composable
fun ViMatePlayerContent(window: PlayerWindow) {
    ViTheme { VideoPlayerScreen(window) }
}
