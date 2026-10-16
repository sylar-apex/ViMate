package org.app.vimate.sharedlogic.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App-wide UI requests that cross feature boundaries: the toast, the bottom sheet over the app and
 * navigation to another tab. Features post here; the app shell renders them.
 */
class AppEvents {
    private val _messages = MutableSharedFlow<AppMessage>(extraBufferCapacity = 8, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val messages: SharedFlow<AppMessage> = _messages.asSharedFlow()

    private val _sheet = MutableStateFlow<SheetRequest?>(null)
    val sheet: StateFlow<SheetRequest?> = _sheet.asStateFlow()

    private val _routes = MutableSharedFlow<AppRoute>(replay = 1, extraBufferCapacity = 4, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val routes: SharedFlow<AppRoute> = _routes.asSharedFlow()

    fun message(key: MessageKey, vararg args: String) {
        _messages.tryEmit(AppMessage(key, args.toList()))
    }

    fun openSheet(request: SheetRequest) { _sheet.value = request }

    fun closeSheet() { _sheet.value = null }

    fun open(route: AppRoute) { _routes.tryEmit(route) }

    /** Forgets the last route once the shell has handled it, so it is not replayed. */
    fun consumeRoute() { _routes.resetReplayCache() }
}
