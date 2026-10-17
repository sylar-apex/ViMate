package org.app.vimate.me.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.launch
import org.app.vimate.me.domain.repository.MeRepository

/** ME tab: hot videos and the day/night switch (POC MeScreen). */
class MeViewModel(private val repo: MeRepository) : MVIBaseViewModel<MeContract.Event, MeContract.State, MeContract.Effect>() {
    override fun createInitialState() = MeContract.State()
    override fun handleEffect(effect: MeContract.Effect) {}

    init {
        viewModelScope.launch { repo.settingsVersion.collect { setState { copy(dark = repo.isDark) } } }
        viewModelScope.launch { val hot = repo.hotVideos(); setState { copy(hot = hot) } }
    }

    override fun handleEvent(event: MeContract.Event) {
        when (event) {
            is MeContract.Event.SetDark -> repo.setDark(event.dark)
        }
    }
}
