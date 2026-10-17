package org.app.vimate.settings.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import com.psbapp.utils.dispatcher.DispatcherProvider
import kotlinx.coroutines.launch
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kmediaplayer.settings.Setting
import org.app.vimate.settings.domain.constant.SettingsGroups
import org.app.vimate.settings.domain.repository.AppCache
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.yt.YouTube

/** POC SettingsGroupScreen logic: reads/writes AppKeys, clears the cache. */
class SettingsViewModel(private val settings: ObservableSettings, private val cache: AppCache, private val dispatcher: DispatcherProvider) :
    MVIBaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {
    override fun createInitialState() = SettingsContract.State()
    override fun handleEffect(effect: SettingsContract.Effect) {}

    operator fun <T> get(s: Setting<T>): T = settings[s]

    init {
        viewModelScope.launch { settings.version.collect { v -> setState { copy(version = v) } } }
        viewModelScope.launch(dispatcher.io) { val b = cache.size(); setState { copy(cacheBytes = b) } }
    }

    override fun handleEvent(event: SettingsContract.Event) {
        when (event) {
            is SettingsContract.Event.Set<*> -> set(event)
            is SettingsContract.Event.Language -> { settings[AppKeys.language] = event.value; YouTube.hl = SettingsGroups.hlOf(event.value) }
            is SettingsContract.Event.Location -> { settings[AppKeys.location] = event.value; YouTube.gl = event.value }
            SettingsContract.Event.ResetPrivatePassword -> {
                settings[AppKeys.privatePin] = ""
                setEffect(SettingsContract.Effect.PrivatePasswordCleared)
            }
            SettingsContract.Event.ClearCache -> viewModelScope.launch(dispatcher.io) { cache.clear(); setState { copy(cacheBytes = 0) } }
        }
    }

    private fun <T> set(e: SettingsContract.Event.Set<T>) { settings[e.setting] = e.value }
}
