package org.app.vimate.sharedlogic.platform

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Lifecycle signals from the host window: the app came to the foreground, or media access changed. */
class HostSignals {
    private val _resumed = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val resumed: SharedFlow<Unit> = _resumed.asSharedFlow()

    fun onResume() { _resumed.tryEmit(Unit) }
}
