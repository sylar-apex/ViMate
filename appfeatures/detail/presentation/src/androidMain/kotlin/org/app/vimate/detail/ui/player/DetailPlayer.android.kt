package org.app.vimate.detail.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.app.kmediaplayer.ui.inline.InlineVideoSurface

@Composable
actual fun rememberDetailPlayer(): DetailPlayer = rememberEngineDetailPlayer()

@Composable
actual fun VideoSurface(player: DetailPlayer, modifier: Modifier) = InlineVideoSurface(modifier)
