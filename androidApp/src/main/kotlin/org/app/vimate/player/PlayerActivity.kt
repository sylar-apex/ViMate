package org.app.vimate.player

import androidx.compose.runtime.Composable
import org.app.kmediaplayer.ui.android.KMediaPlayerActivity
import org.app.vimate.player.ui.ViMatePlayerContent

/** The library's video player in ViMate's theme. */
class PlayerActivity : KMediaPlayerActivity() {
    @Composable
    override fun Content() = ViMatePlayerContent(this)
}
