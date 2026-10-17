package org.app.vimate.shell

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** What a platform launch asked for (POC MainActivity.handle). */
sealed interface LaunchRequest {
    /** EXTRA_TAB, e.g. "files" from the download notification. */
    data class OpenTab(val tab: Int) : LaunchRequest
    /** EXTRA_VIDEO: the popup player returning to a video's page. */
    data class OpenVideo(val id: String) : LaunchRequest
    /** ACTION_VIEW link or the first url of ACTION_SEND text: YouTube → Detail, else Browser. */
    data class OpenUrl(val url: String) : LaunchRequest
}

/** Hand-off from the host (Android intents, desktop args) to the shell. */
object LaunchRequests {
    private val _requests = MutableSharedFlow<LaunchRequest>(replay = 1, extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val requests: SharedFlow<LaunchRequest> = _requests.asSharedFlow()

    fun post(request: LaunchRequest) { _requests.tryEmit(request) }

    /** Shared text (POC ACTION_SEND): opens its first http(s) url, if any. */
    fun postSharedText(text: String?) {
        text?.let { Regex("""https?://\S+""").find(it)?.value }?.let { post(LaunchRequest.OpenUrl(it)) }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    internal fun consume() { _requests.resetReplayCache() }
}
