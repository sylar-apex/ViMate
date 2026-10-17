package org.app.vimate.me.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.app.vimate.me.domain.repository.JunkFileSystem

class BoostContract {
    sealed class Event : UiEvent
    data class State(val pct: Int = 0) : UiState
    sealed class Effect : UiEffect
}

/** POC BoostScreen: clears the cache (keeping partial downloads), then counts 0..100%. */
class BoostViewModel(private val fs: JunkFileSystem) : MVIBaseViewModel<BoostContract.Event, BoostContract.State, BoostContract.Effect>() {
    override fun createInitialState() = BoostContract.State()
    override fun handleEffect(effect: BoostContract.Effect) {}
    override fun handleEvent(event: BoostContract.Event) {}

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { fs.clearCacheKeepingDownloads() }
            while (currentState.pct < 100) { delay(25); setState { copy(pct = pct + 1) } }
        }
    }
}
