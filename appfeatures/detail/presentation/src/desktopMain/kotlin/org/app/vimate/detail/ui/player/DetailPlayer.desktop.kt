package org.app.vimate.detail.ui.player

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.app.kmediaplayer.ui.inline.InlineVideoSurface
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.detail.model.WebStream

@Composable
actual fun rememberDetailPlayer(): DetailPlayer = rememberEngineDetailPlayer()

@Composable
actual fun VideoSurface(player: DetailPlayer, modifier: Modifier) = InlineVideoSurface(modifier)

@Composable
actual fun EmbedPlayer(id: String, thumbnail: String) = Thumb(thumbnail, Modifier.fillMaxWidth().aspectRatio(16f / 9), corner = 0)

@Composable
actual fun rememberWebFallback(): (suspend (videoId: String) -> List<WebStream>)? = null

@Composable
actual fun rememberPopupController(): PopupController = NoPopup
