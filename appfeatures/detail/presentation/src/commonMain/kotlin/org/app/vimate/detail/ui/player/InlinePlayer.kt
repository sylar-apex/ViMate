package org.app.vimate.detail.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import org.app.vimate.appcore.theme.VRed
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_auto
import org.app.vimate.appres.resources.detail_cc
import org.app.vimate.appres.resources.detail_collapse
import org.app.vimate.appres.resources.detail_fullscreen
import org.app.vimate.appres.resources.detail_next
import org.app.vimate.appres.resources.detail_play
import org.app.vimate.appres.resources.detail_play_audio_desc
import org.app.vimate.appres.resources.ic_p_audio
import org.app.vimate.appres.resources.ic_p_chev
import org.app.vimate.appres.resources.ic_p_full
import org.app.vimate.appres.resources.ic_p_next
import org.app.vimate.appres.resources.ic_p_pause
import org.app.vimate.appres.resources.ic_p_play
import org.app.vimate.detail.model.Quality
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.app.vimate.sharedlogic.yt.YouTube
import org.jetbrains.compose.resources.stringResource

/**
 * Detail-page player: surface plus VidMate-style overlay (autoplay switch, CC, quality, speed,
 * audio-only, play/pause, next, seek bar, fullscreen).
 */
@Composable
fun InlinePlayer(
    player: DetailPlayer,
    qualities: List<Quality>,
    captions: Boolean,
    autoNext: Boolean,
    onAutoNext: (Boolean) -> Unit,
    onNext: () -> Unit,
    onAudioOnly: () -> Unit,
    onFullscreen: () -> Unit,
    onCollapse: () -> Unit,
    onError: (String) -> Unit,
    audioMode: Boolean = false,
    onPlayAsVideo: () -> Unit = {},
    onQuality: (Quality) -> Unit = {},
    wide: Boolean = false,
) {
    var visible by remember { mutableStateOf(true) }
    var playing by remember { mutableStateOf(player.isPlaying) }
    var pos by remember { mutableLongStateOf(0) }
    var dur by remember { mutableLongStateOf(0) }
    var buffering by remember { mutableStateOf(true) }
    var quality by remember(qualities) { mutableStateOf(qualities.firstOrNull()) }
    var speed by remember { mutableFloatStateOf(1f) }
    var menu by remember { mutableStateOf<String?>(null) }
    var cc by remember { mutableStateOf(false) }
    val autoNow by rememberUpdatedState(autoNext)
    val nextNow by rememberUpdatedState(onNext)
    val errorNow by rememberUpdatedState(onError)

    DisposableEffect(player) {
        val l = object : DetailPlayerListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
            override fun onBuffering(isBuffering: Boolean) { buffering = isBuffering }
            override fun onEnded() { if (autoNow) nextNow() }
            override fun onError(code: String) = errorNow(code)
        }
        player.addListener(l)
        onDispose { player.removeListener(l) }
    }
    LaunchedEffect(quality) {
        val q = quality ?: return@LaunchedEffect
        onQuality(q)
        val at = PopupResume.resume?.takeIf { player.currentPosition < 1000 }?.second?.also { PopupResume.resume = null } ?: player.currentPosition
        player.load(q, at)
    }
    LaunchedEffect(Unit) { while (true) { pos = player.currentPosition; dur = player.duration.coerceAtLeast(0); delay(DetailLayoutSpec.POLL_MS) } }
    LaunchedEffect(visible, playing) { if (visible && playing) { delay(DetailLayoutSpec.HIDE_CONTROLS_MS); visible = false } }

    val speedText = "${speed.toString().removeSuffix(".0")}x"
    // The menu lists qualities highest first with Auto last, whatever order playback preference picked.
    val menuQualities = remember(qualities) { qualities.sortedWith(compareByDescending { it.label.takeWhile(Char::isDigit).toIntOrNull() ?: -1 }) }
    Box(Modifier.fillMaxWidth().aspectRatio(DetailLayoutSpec.PLAYER_RATIO).background(Color.Black).clickable(interactionSource = null, indication = null) { visible = !visible }) {
        VideoSurface(player, Modifier.fillMaxSize())
        if (audioMode) {
            AudioModeOverlay(player, playing, pos, dur, onPlayAsVideo, onNext, onFullscreen)
            Box(Modifier.offset(x = 14.dp, y = 8.dp).size(28.dp).clickable(onClick = onCollapse), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_p_chev, Modifier.size(24.dp), Color.White) }
            AutoplaySwitch(autoNext, onAutoNext, 213)
            Text(speedText, Modifier.offset(x = 273.dp, y = 10.dp), color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp)
            Box(Modifier.offset(x = 311.dp, y = 11.dp).size(22.dp).clickable(onClick = onAudioOnly), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_p_audio, Modifier.size(22.dp), Color.White) }
            return@Box
        }
        if (buffering) CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color.White)
        if (wide && (visible || !playing)) {
            // Desktop / tablet, YouTube-style: setting chips top right, big play button while paused,
            // full-width red seek bar over a play / next / time / fullscreen row. Autoplay lives in the related column.
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(110.dp).background(Brush.verticalGradient(listOf(Color.Transparent, ViColors.shade60))))
            Row(Modifier.align(Alignment.TopEnd).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (captions) Chip(stringResource(Res.string.detail_cc), selected = cc) { cc = !cc }
                Box {
                    Chip("${quality?.label ?: stringResource(Res.string.detail_auto)} ▾") { menu = "q" }
                    DropdownMenu(menu == "q", { menu = null }) {
                        Column(Modifier.width(160.dp)) {
                            menuQualities.forEach { q ->
                                DropdownMenuItem(text = { Text(q.label) }, onClick = { quality = q; menu = null },
                                    trailingIcon = { if (q == quality) Icon(Icons.Filled.Check, null) })
                            }
                        }
                    }
                }
                Box {
                    Chip(speedText) { menu = "s" }
                    DropdownMenu(menu == "s", { menu = null }) {
                        Column(Modifier.width(120.dp)) {
                            DetailLayoutSpec.SPEEDS.forEach { sp ->
                                DropdownMenuItem(text = { Text("${sp}x") }, onClick = { speed = sp; player.setPlaybackSpeed(sp); menu = null })
                            }
                        }
                    }
                }
                Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.shade60).clickable(onClick = onAudioOnly), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_p_audio, Modifier.size(18.dp), Color.White, stringResource(Res.string.detail_play_audio_desc))
                }
            }
            if (!playing && !buffering) Box(Modifier.align(Alignment.Center).size(68.dp).clip(CircleShape).background(ViColors.shade60).clickable { player.play() }, contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_p_play, Modifier.size(40.dp), Color.White, stringResource(Res.string.detail_play))
            }
            Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
                val frac = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
                BoxWithConstraints(Modifier.fillMaxWidth().height(16.dp)
                    .pointerInput(dur) { detectTapGestures { o -> if (dur > 0) { val t = (o.x / size.width * dur).toLong(); player.seekTo(t); pos = t } } }) {
                    Box(Modifier.align(Alignment.CenterStart).fillMaxWidth().height(4.dp).clip(CircleShape).background(ViColors.playerSeekTrack))
                    Box(Modifier.align(Alignment.CenterStart).fillMaxWidth(frac).height(4.dp).clip(CircleShape).background(VRed))
                    Box(Modifier.align(Alignment.CenterStart).offset(x = (maxWidth - 14.dp) * frac).size(14.dp).clip(CircleShape).background(VRed))
                }
                Row(Modifier.fillMaxWidth().height(36.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(32.dp).clickable { if (playing) player.pause() else player.play() }, contentAlignment = Alignment.Center) {
                        Ic(if (playing) Res.drawable.ic_p_pause else Res.drawable.ic_p_play, Modifier.size(26.dp), Color.White, stringResource(Res.string.detail_play))
                    }
                    Box(Modifier.size(32.dp).clickable(onClick = onNext), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_p_next, Modifier.size(22.dp), Color.White, stringResource(Res.string.detail_next))
                    }
                    Text("${YouTube.formatDuration(pos / 1000)} / ${YouTube.formatDuration(dur / 1000)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.size(32.dp).clickable(onClick = onFullscreen), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_p_full, Modifier.size(20.dp), Color.White, stringResource(Res.string.detail_fullscreen))
                    }
                }
            }
        } else if (visible) {
            Box(Modifier.fillMaxSize().background(ViColors.shade40))
            // Top row measured from VidMate (dp from the player's top-left, row centred at y=22).
            Box(Modifier.offset(x = 14.dp, y = 8.dp).size(28.dp).clickable(onClick = onCollapse), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_p_chev, Modifier.size(24.dp), Color.White, stringResource(Res.string.detail_collapse))
            }
            AutoplaySwitch(autoNext, onAutoNext, 131)
            Box(Modifier.offset(x = 178.dp, y = 13.5.dp).size(28.dp, 17.dp).border(1.3.dp, if (cc) Color.White else ViColors.playerCcOff, RoundedCornerShape(3.dp))
                .clickable(enabled = captions) { cc = !cc }, contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.detail_cc), color = if (cc) Color.White else ViColors.playerCcOff, fontSize = 12.sp, lineHeight = 13.sp)
            }
            Box(Modifier.offset(x = 218.dp, y = 10.dp)) {
                Text(quality?.label ?: stringResource(Res.string.detail_auto), Modifier.clickable { menu = "q" }, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp)
                DropdownMenu(menu == "q", { menu = null }) {
                    Column(Modifier.width(160.dp)) {
                        menuQualities.forEach { q ->
                            DropdownMenuItem(text = { Text(q.label) }, onClick = { quality = q; menu = null },
                                trailingIcon = { if (q == quality) Icon(Icons.Filled.Check, null) })
                        }
                    }
                }
            }
            Box(Modifier.offset(x = 273.dp, y = 10.dp)) {
                Text(speedText, Modifier.clickable { menu = "s" }, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp)
                DropdownMenu(menu == "s", { menu = null }) {
                    Column(Modifier.width(120.dp)) {
                        DetailLayoutSpec.SPEEDS.forEach { sp ->
                            DropdownMenuItem(text = { Text("${sp}x") }, onClick = { speed = sp; player.setPlaybackSpeed(sp); menu = null })
                        }
                    }
                }
            }
            Box(Modifier.offset(x = 311.dp, y = 11.dp).size(22.dp).clickable(onClick = onAudioOnly), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_p_audio, Modifier.size(22.dp), Color.White, stringResource(Res.string.detail_play_audio_desc))
            }
            // Centre: play/pause at x=167dp, next at x=237dp (no rewind button in VidMate).
            Box(Modifier.offset(x = 160.dp, y = 76.dp).size(46.dp).clickable { if (playing) player.pause() else player.play() }, contentAlignment = Alignment.Center) {
                Ic(if (playing) Res.drawable.ic_p_pause else Res.drawable.ic_p_play, Modifier.wrapContentSize(unbounded = true).size(56.dp), Color.White, stringResource(Res.string.detail_play))
            }
            Box(Modifier.offset(x = 231.dp, y = 82.dp).size(38.dp), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_p_next, Modifier.wrapContentSize(unbounded = true).size(46.dp).clickable(onClick = onNext), Color.White, stringResource(Res.string.detail_next))
            }
            // Bottom bar measured from VidMate: times white 15sp, thin grey track x=55..249dp with blue progress and 12dp blue thumb,
            // centred 18dp above the bottom; corner fullscreen icon at x=316dp.
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(36.dp)) {
                Text(YouTube.formatDuration(pos / 1000), Modifier.offset(x = 16.dp).align(Alignment.CenterStart), color = Color.White, fontSize = 15.sp, lineHeight = 18.sp)
                val frac = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
                Box(Modifier.offset(x = 55.dp).width(194.dp).fillMaxHeight().align(Alignment.CenterStart)
                    .pointerInput(dur) { detectTapGestures { o -> if (dur > 0) { val t = (o.x / size.width * dur).toLong(); player.seekTo(t); pos = t } } }) {
                    Box(Modifier.align(Alignment.CenterStart).fillMaxWidth().height(1.3.dp).background(ViColors.playerSeekTrack))
                    Box(Modifier.align(Alignment.CenterStart).fillMaxWidth(frac).height(1.3.dp).background(ViColors.playerSeekBlue))
                    Box(Modifier.align(Alignment.CenterStart).offset(x = 188.dp * frac).size(12.dp).clip(CircleShape).background(ViColors.playerSeekBlue))
                }
                Text(YouTube.formatDuration(dur / 1000), Modifier.offset(x = 265.dp).align(Alignment.CenterStart), color = Color.White, fontSize = 15.sp, lineHeight = 18.sp)
                Box(Modifier.offset(x = 314.dp).size(22.dp).align(Alignment.CenterStart).clickable(onClick = onFullscreen), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_p_full, Modifier.size(20.dp), Color.White, stringResource(Res.string.detail_fullscreen))
                }
            }
        }
    }
}

/** Translucent rounded chip over the wide player (CC, quality, speed). */
@Composable
private fun Chip(text: String, selected: Boolean = false, onClick: () -> Unit) {
    Box(Modifier.height(34.dp).clip(RoundedCornerShape(10.dp)).background(if (selected) Color.White else ViColors.shade60).clickable(onClick = onClick)
        .padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
        Text(text, color = if (selected) Color.Black else Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Autoplay switch: blue track, white knob with a grey play glyph, at [x] dp. */
@Composable
private fun BoxScope.AutoplaySwitch(on: Boolean, onChange: (Boolean) -> Unit, x: Int) {
    Box(Modifier.offset(x = x.dp, y = 14.dp).size(36.dp, 16.dp).clip(CircleShape).background(if (on) ViColors.playerAutoplayOn else ViColors.playerAutoplayOff).clickable { onChange(!on) }) {
        Box(Modifier.align(if (on) Alignment.CenterEnd else Alignment.CenterStart).size(16.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_p_play, Modifier.size(10.dp), ViColors.playerAutoplayGlyph)
        }
    }
}
