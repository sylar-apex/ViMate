package org.app.vimate.detail.ui.player

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import org.app.vimate.detail.model.Quality
import org.app.vimate.sharedlogic.yt.YouTube

/** POC `rememberExo()` + InlinePlayer source building: ExoPlayer with YouTube's stream UA, merged split streams, HLS. */
class ExoDetailPlayer(val exo: ExoPlayer) : DetailPlayer {
    private val bridges = mutableMapOf<DetailPlayerListener, Player.Listener>()
    override val isPlaying get() = exo.isPlaying
    override val currentPosition get() = exo.currentPosition
    override val duration get() = exo.duration
    override fun play() = exo.play()
    override fun pause() = exo.pause()
    override fun seekTo(ms: Long) = exo.seekTo(ms)
    override fun setPlaybackSpeed(speed: Float) = exo.setPlaybackSpeed(speed)
    override fun load(q: Quality, positionMs: Long) {
        val http = DefaultHttpDataSource.Factory().setUserAgent(q.ua ?: YouTube.streamUA).setAllowCrossProtocolRedirects(true)
        fun prog(u: String) = ProgressiveMediaSource.Factory(http).createMediaSource(MediaItem.fromUri(u))
        val source = when {
            q.hls -> HlsMediaSource.Factory(http).createMediaSource(MediaItem.Builder().setUri(q.url).setMimeType(MimeTypes.APPLICATION_M3U8).build())
            q.audioUrl != null -> MergingMediaSource(prog(q.url), prog(q.audioUrl!!))
            else -> prog(q.url)
        }
        exo.setMediaSource(source, positionMs)
        exo.prepare(); exo.play()
    }
    override fun addListener(l: DetailPlayerListener) {
        val b = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = l.onIsPlayingChanged(isPlaying)
            override fun onPlaybackStateChanged(state: Int) {
                l.onBuffering(state == Player.STATE_BUFFERING)
                if (state == Player.STATE_ENDED) l.onEnded()
            }
            override fun onPlayerError(error: PlaybackException) { android.util.Log.e("VMPOC", "playback", error); l.onError(error.errorCodeName) }
        }
        bridges[l] = b; exo.addListener(b)
    }
    override fun removeListener(l: DetailPlayerListener) { bridges.remove(l)?.let(exo::removeListener) }
}

@Composable
actual fun rememberDetailPlayer(): DetailPlayer {
    val ctx = LocalContext.current
    val player = remember {
        ExoDetailPlayer(ExoPlayer.Builder(ctx).setMediaSourceFactory(DefaultMediaSourceFactory(
            DataSource.Factory { DefaultHttpDataSource.Factory().setUserAgent(YouTube.streamUA).setAllowCrossProtocolRedirects(true).createDataSource() })).build()
            .apply { repeatMode = Player.REPEAT_MODE_OFF })
    }
    DisposableEffect(Unit) { onDispose { player.exo.release() } }
    return player
}

@Composable
actual fun VideoSurface(player: DetailPlayer, modifier: Modifier) {
    val exo = (player as ExoDetailPlayer).exo
    AndroidView(factory = { SurfaceView(it).also(exo::setVideoSurfaceView) }, modifier = modifier, onRelease = { exo.clearVideoSurfaceView(it) })
}
