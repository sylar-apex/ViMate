package org.app.vimate.detail.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.engine.EngineError
import org.app.kmediaplayer.engine.EngineListener
import org.app.kmediaplayer.engine.EngineState
import org.app.kmediaplayer.engine.MediaEngine
import org.app.kmediaplayer.model.MediaFile
import org.app.kmediaplayer.model.StreamExtras
import org.app.vimate.detail.model.Quality
import org.app.vimate.sharedlogic.yt.YouTube

/**
 * [DetailPlayer] on kmediaplayer's shared engine (libVLC on Android, iOS and desktop; the browser's video
 * element on the web). A split video+audio [Quality] passes its audio stream as [StreamExtras.audioUri].
 */
class EngineDetailPlayer(private val engine: MediaEngine = KMediaPlayer.engine) : DetailPlayer {
    private val bridges = mutableMapOf<DetailPlayerListener, EngineListener>()
    internal var handedOff = false
    override val isPlaying get() = engine.isPlaying
    override val currentPosition get() = engine.position
    override val duration get() = engine.duration
    override fun play() = engine.play()
    override fun pause() = engine.pause()
    override fun seekTo(ms: Long) = engine.seekTo(ms)
    override fun setPlaybackSpeed(speed: Float) { engine.speed = speed }
    override fun load(q: Quality, positionMs: Long) {
        val extras = StreamExtras(audioUri = q.audioUrl, userAgent = q.ua ?: YouTube.streamUA)
        engine.setItems(listOf(MediaFile(q.url.hashCode().toLong(), q.url, q.url, q.label, 0, 0, 0,
            mimeType = if (q.hls) "application/x-mpegURL" else "video/mp4", extras = extras)), 0, positionMs)
        engine.prepare(); engine.play()
    }
    override fun addListener(l: DetailPlayerListener) {
        val b = object : EngineListener {
            override fun onIsPlayingChanged(playing: Boolean) = l.onIsPlayingChanged(playing)
            override fun onStateChanged(state: EngineState) {
                l.onBuffering(state == EngineState.Buffering)
                if (state == EngineState.Ended) l.onEnded()
            }
            override fun onError(error: EngineError) = l.onError(error.codeName)
        }
        bridges[l] = b; engine.addListener(b)
    }
    override fun removeListener(l: DetailPlayerListener) { bridges.remove(l)?.let(engine::removeListener) }
    override fun handOff() { handedOff = true }
}

/** The detail page's player on every platform: kmediaplayer's shared engine. */
@Composable
internal fun rememberEngineDetailPlayer(): DetailPlayer {
    val p = remember { EngineDetailPlayer() }
    DisposableEffect(Unit) { onDispose { if (!p.handedOff) p.pause() } }
    return p
}
