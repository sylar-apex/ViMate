package org.app.vimate.sharedlogic.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Whether private files are unlocked; it lasts until the app process ends, as in the reference app. */
class PrivateSession {
    private val _unlocked = MutableStateFlow(false)
    val unlocked: StateFlow<Boolean> = _unlocked.asStateFlow()

    fun unlock() { _unlocked.value = true }
    fun lock() { _unlocked.value = false }
}
