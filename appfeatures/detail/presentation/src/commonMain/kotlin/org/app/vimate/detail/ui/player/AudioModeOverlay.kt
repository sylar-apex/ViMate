package org.app.vimate.detail.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_audio_hint
import org.app.vimate.appres.resources.detail_play_as_video
import org.app.vimate.appres.resources.detail_play_next
import org.app.vimate.appres.resources.ic_p_full
import org.app.vimate.appres.resources.ic_p_next
import org.app.vimate.appres.resources.ic_p_pause
import org.app.vimate.appres.resources.ic_p_play
import org.app.vimate.sharedlogic.yt.YouTube
import org.jetbrains.compose.resources.stringResource

/** VidMate audio mode: dark-red backdrop, note + two pill buttons, spinning record, pause + seek bar. */
@Composable
internal fun AudioModeOverlay(player: DetailPlayer, playing: Boolean, pos: Long, dur: Long, onPlayAsVideo: () -> Unit, onNext: () -> Unit, onFullscreen: () -> Unit) {
    val spin by rememberInfiniteTransition(label = "disc").animateFloat(0f, 360f,
        infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "spin")
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(ViColors.playerAudioBgStart, ViColors.playerAudioBgEnd), radius = 900f))) {
        Text(stringResource(Res.string.detail_audio_hint), Modifier.offset(x = 17.dp, y = 61.dp), color = Color.White, fontSize = 12.6.sp, lineHeight = 17.5.sp)
        Row(Modifier.offset(x = 17.dp, y = 111.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AudioPill(true, stringResource(Res.string.detail_play_as_video), onPlayAsVideo)
            AudioPill(false, stringResource(Res.string.detail_play_next), onNext)
        }
        // Record: black vinyl with red label, rotating while playing.
        Box(Modifier.align(Alignment.TopEnd).offset(x = (-12).dp, y = 42.dp).size(92.dp).graphicsLayer(rotationZ = if (playing) spin else 0f).clip(CircleShape)
            .background(Brush.radialGradient(listOf(ViColors.playerVinylStart, ViColors.playerVinylEnd))), contentAlignment = Alignment.Center) {
            Box(Modifier.size(54.dp).clip(CircleShape).background(ViColors.playerVinylLabel), contentAlignment = Alignment.Center) { Box(Modifier.size(4.dp).clip(CircleShape).background(Color.Black)) }
        }
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(36.dp)) {
            Box(Modifier.offset(x = 14.dp).size(22.dp).align(Alignment.CenterStart).clickable { if (playing) player.pause() else player.play() }, contentAlignment = Alignment.Center) {
                Ic(if (playing) Res.drawable.ic_p_pause else Res.drawable.ic_p_play, Modifier.size(26.dp), Color.White)
            }
            Text(YouTube.formatDuration(pos / 1000), Modifier.offset(x = 40.dp).align(Alignment.CenterStart), color = Color.White, fontSize = 15.sp)
            val frac = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
            Box(Modifier.offset(x = 74.dp).width(132.dp).fillMaxHeight().align(Alignment.CenterStart)) {
                Box(Modifier.align(Alignment.CenterStart).fillMaxWidth().height(1.3.dp).background(ViColors.playerSeekTrack))
                Box(Modifier.align(Alignment.CenterStart).offset(x = 126.dp * frac).size(12.dp).clip(CircleShape).background(ViColors.playerSeekBlue))
            }
            Text(YouTube.formatDuration(dur / 1000), Modifier.offset(x = 220.dp).align(Alignment.CenterStart), color = Color.White, fontSize = 15.sp)
            Box(Modifier.offset(x = 314.dp).size(22.dp).align(Alignment.CenterStart).clickable(onClick = onFullscreen), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_p_full, Modifier.size(20.dp), Color.White)
            }
        }
    }
}

@Composable
private fun AudioPill(isPlay: Boolean, label: String, onClick: () -> Unit) {
    Row(Modifier.height(29.dp).clip(CircleShape).background(ViColors.playerAudioPill).clickable(onClick = onClick).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).clip(CircleShape).background(if (isPlay) Color.White else Color.Transparent), contentAlignment = Alignment.Center) {
            Ic(if (isPlay) Res.drawable.ic_p_play else Res.drawable.ic_p_next, Modifier.size(if (isPlay) 11.dp else 16.dp), if (isPlay) ViColors.playerAudioPlayGlyph else Color.White)
        }
        Text(label, Modifier.padding(start = 6.dp), color = Color.White, fontSize = 12.6.sp)
    }
}
