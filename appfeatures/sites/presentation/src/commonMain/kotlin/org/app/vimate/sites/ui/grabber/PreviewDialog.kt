package org.app.vimate.sites.ui.grabber

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.engine.EngineError
import org.app.kmediaplayer.engine.EngineListener
import org.app.kmediaplayer.engine.RepeatMode
import org.app.kmediaplayer.ui.inline.InlineVideoSurface
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.grab_cant_play
import org.app.vimate.appres.resources.ic_meta_music
import org.app.vimate.appres.resources.ic_p_play
import org.app.vimate.appres.resources.ic_pp_close
import org.app.vimate.appres.resources.sites_close
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sites.logic.fmtSize
import org.app.vimate.sites.model.GrabType
import org.app.vimate.sites.repository.GrabProbeRepository
import org.app.vimate.sites.ui.web.BrowserController
import org.app.vimate.sites.ui.web.PlatformWebView
import org.app.vimate.sites.ui.web.WebCallbacks
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Full-screen preview: images shown, video/audio played with controls, other links opened in a web view. */
@Composable
internal fun PreviewDialog(g: GrabItem, userAgent: String?, referer: String, onClose: () -> Unit) {
    val probe = koinInject<GrabProbeRepository>()
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(ViColors.black)) {
            when (g.type) {
                GrabType.Image -> {
                    val full by produceState(g.thumb, g.url) { value = probe.fetch(g.url, referer, userAgent) ?: g.thumb }
                    full?.let { AsyncImage(it, null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit) }
                }
                GrabType.Video, GrabType.Audio -> MediaPreview(g)
                else -> PlatformWebView(g.url, remember { BrowserController() }, WebCallbacks(),
                    Modifier.fillMaxSize().statusBarsPadding().padding(top = 48.dp), sniff = false, userAgent = userAgent)
            }
            Row(Modifier.align(Alignment.TopStart).fillMaxWidth().background(ViColors.shade60).statusBarsPadding().height(48.dp).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clickable(onClick = onClose), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_pp_close, Modifier.size(22.dp), ViColors.white, stringResource(Res.string.sites_close)) }
                Column(Modifier.weight(1f)) {
                    Text(g.name, color = ViColors.white, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(listOfNotNull(g.resolution.ifEmpty { null }, if (g.size > 0) fmtSize(g.size) else null, g.type.label()).joinToString(" · "), color = ViColors.white.copy(alpha = .7f), fontSize = 11.sp)
                }
            }
        }
    }
}

/** Video/audio preview on the shared engine (repeat one), tap to pause, tap-to-seek bar. */
@Composable
private fun MediaPreview(g: GrabItem) {
    val player = KMediaPlayer.engine
    var playing by remember { mutableStateOf(true) }
    var pos by remember { mutableLongStateOf(0) }
    var dur by remember { mutableLongStateOf(0) }
    var err by remember { mutableStateOf<String?>(null) }
    DisposableEffect(g.url) {
        val l = object : EngineListener {
            override fun onError(error: EngineError) { err = error.codeName }
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        }
        player.addListener(l)
        player.setStream(g.url)
        player.repeatMode = RepeatMode.One
        player.prepare(); player.play()
        onDispose { player.removeListener(l); player.pause(); player.repeatMode = RepeatMode.Off }
    }
    LaunchedEffect(g.url) { while (true) { pos = player.position; dur = player.duration.coerceAtLeast(0); delay(300) } }
    val errText = err?.let { stringResource(Res.string.grab_cant_play, it) }
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().clickable { if (player.isPlaying) player.pause() else player.play() }, contentAlignment = Alignment.Center) {
            if (g.type == GrabType.Video) InlineVideoSurface(Modifier.fillMaxWidth().aspectRatio(if (g.width > 0 && g.height > 0) g.width.toFloat() / g.height else 16f / 9))
            else Ic(Res.drawable.ic_meta_music, Modifier.size(96.dp), ViColors.white.copy(alpha = .6f))
            if (!playing) Box(Modifier.size(64.dp).clip(CircleShape).background(ViColors.shade53), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_p_play, Modifier.size(40.dp), ViColors.white) }
            errText?.let { Text(it, Modifier.align(Alignment.Center).padding(24.dp), color = ViColors.white) }
        }
        Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(YouTube.formatDuration(pos / 1000), color = ViColors.white, fontSize = 12.sp)
            val frac = if (dur > 0) (pos.toFloat() / dur).coerceIn(0f, 1f) else 0f
            BoxWithConstraints(Modifier.weight(1f).height(28.dp).padding(horizontal = 10.dp)
                .pointerInput(dur) { detectTapGestures { o -> if (dur > 0) player.seekTo((o.x / size.width * dur).toLong()) } }) {
                val w = maxWidth
                Box(Modifier.align(Alignment.CenterStart).fillMaxWidth().height(2.dp).background(ViColors.shortsFollowing))
                Box(Modifier.align(Alignment.CenterStart).fillMaxWidth(frac).height(2.dp).background(VRed))
                Box(Modifier.align(Alignment.CenterStart).offset(x = w * frac - 6.dp).size(12.dp).clip(CircleShape).background(VRed))
            }
            Text(YouTube.formatDuration(dur / 1000), color = ViColors.white, fontSize = 12.sp)
        }
    }
}
