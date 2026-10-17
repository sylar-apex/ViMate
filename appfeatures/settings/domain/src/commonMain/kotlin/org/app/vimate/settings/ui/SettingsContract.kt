package org.app.vimate.settings.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.kmediaplayer.settings.Setting

class SettingsContract {
    sealed class Event : UiEvent {
        data class Set<T>(val setting: Setting<T>, val value: T) : Event()
        data class Language(val value: String) : Event()
        data class Location(val value: String) : Event()
        data object ResetPrivatePassword : Event()
        data object ClearCache : Event()
    }
    data class State(val version: Int = 0, val cacheBytes: Long = 0) : UiState
    sealed class Effect : UiEffect {
        data object PrivatePasswordCleared : Effect()
    }
}
