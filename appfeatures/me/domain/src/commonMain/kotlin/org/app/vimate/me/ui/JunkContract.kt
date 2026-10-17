package org.app.vimate.me.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.me.domain.model.JunkGroup

class JunkContract {
    sealed class Event : UiEvent {
        data class ToggleOpen(val group: Int) : Event()
        data class ToggleGroup(val group: Int) : Event()
        data class TogglePath(val group: Int, val path: Int) : Event()
        data object Clean : Event()
    }
    data class State(
        val groups: List<JunkGroup> = emptyList(),
        val scanning: Boolean = true,
        val current: String = "",
        val cleaned: Boolean = false,
    ) : UiState {
        val total: Long get() = groups.sumOf { it.bytes }
        val selected: Long get() = groups.sumOf { g -> g.paths.filter { it.checked }.sumOf { it.bytes } }
    }
    sealed class Effect : UiEffect
}
