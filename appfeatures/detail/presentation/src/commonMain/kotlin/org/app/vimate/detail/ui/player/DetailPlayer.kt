package org.app.vimate.detail.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.app.vimate.detail.model.Quality

/** Events of the detail player (POC Player.Listener subset). */
interface DetailPlayerListener {
    fun onIsPlayingChanged(playing: Boolean) {}
    fun onBuffering(buffering: Boolean) {}
    fun onEnded() {}
    fun onError(code: String) {}
}

/** The detail page's own player (POC `rememberExo()`): one [Quality] at a time, with optional separate audio. */
interface DetailPlayer {
    val isPlaying: Boolean
    val currentPosition: Long
    val duration: Long
    fun play()
    fun pause()
    fun seekTo(ms: Long)
    fun setPlaybackSpeed(speed: Float)
    fun load(q: Quality, positionMs: Long)
    fun addListener(l: DetailPlayerListener)
    fun removeListener(l: DetailPlayerListener)
    /** Another view (the popup) continues this playback, so leaving the page must not pause it. */
    fun handOff() {}
}

/** A player owned by the composable; released (or paused, for the shared engine) when it leaves composition. */
@Composable
expect fun rememberDetailPlayer(): DetailPlayer

/** Draws [player]'s video. */
@Composable
expect fun VideoSurface(player: DetailPlayer, modifier: Modifier = Modifier)
