package org.app.vimate.detail.contract

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.sharedlogic.yt.Comment
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.app.vimate.sharedlogic.yt.YouTube

object DetailContract {
    sealed interface Event : UiEvent {
        data class Open(val video: Video) : Event
        data object PlayNext : Event
        data object RetryInfo : Event
    }

    data class State(
        val current: Video? = null,
        val info: VideoInfo? = null,
        val infoError: String? = null,
        val next: YouTube.Next? = null,
        val nextLoading: Boolean = false,
        val firstComment: Comment? = null,
    ) : UiState {
        /** POC `v`: full info merged with the avatar/published from /next. */
        val video: Video? get() = current?.let { c ->
            info?.video?.copy(channelAvatar = next?.channelAvatar ?: c.channelAvatar, published = next?.published ?: c.published) ?: c
        }
    }

    sealed interface Effect : UiEffect
}
