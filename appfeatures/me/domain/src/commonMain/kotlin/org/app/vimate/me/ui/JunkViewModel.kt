package org.app.vimate.me.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import com.psbapp.utils.dispatcher.DispatcherProvider
import kotlinx.coroutines.launch
import org.app.vimate.me.domain.model.JunkGroup
import org.app.vimate.me.domain.repository.JunkFileSystem
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidDownloads

/** POC JunkCleanScreen: scans the junk folders, then deletes the checked ones. */
class JunkViewModel(private val fs: JunkFileSystem, private val downloads: VidDownloads?, private val dispatcher: DispatcherProvider) :
    MVIBaseViewModel<JunkContract.Event, JunkContract.State, JunkContract.Effect>() {
    override fun createInitialState() = JunkContract.State()
    override fun handleEffect(effect: JunkContract.Effect) {}

    init {
        viewModelScope.launch(dispatcher.io) {
            setState { copy(groups = fs.groups(), current = fs.rootPath) }
            val onEnter: (String) -> Unit = { p -> setState { copy(current = p) } }
            currentState.groups.forEachIndexed { gi, g ->
                g.paths.forEachIndexed { pi, p ->
                    val bytes = fs.sizeOf(p.path, onEnter)
                    updatePath(gi, pi) { it.copy(bytes = bytes) }
                }
            }
            // Keep scanning shared storage for the progress line, like VidMate.
            fs.walkRoot(onEnter)
            setState { copy(scanning = false) }
        }
    }

    private fun updateGroup(gi: Int, f: (JunkGroup) -> JunkGroup) =
        setState { copy(groups = groups.mapIndexed { i, g -> if (i == gi) f(g) else g }) }

    private fun updatePath(gi: Int, pi: Int, f: (org.app.vimate.me.domain.model.JunkPath) -> org.app.vimate.me.domain.model.JunkPath) =
        updateGroup(gi) { g -> g.copy(paths = g.paths.mapIndexed { i, p -> if (i == pi) f(p) else p }) }

    override fun handleEvent(event: JunkContract.Event) {
        when (event) {
            is JunkContract.Event.ToggleOpen -> updateGroup(event.group) { it.copy(open = !it.open) }
            is JunkContract.Event.ToggleGroup -> updateGroup(event.group) { g -> val v = !g.checked; g.copy(paths = g.paths.map { it.copy(checked = v) }) }
            is JunkContract.Event.TogglePath -> updatePath(event.group, event.path) { it.copy(checked = !it.checked) }
            JunkContract.Event.Clean -> clean()
        }
    }

    private fun clean() {
        val active = downloads?.tasks?.value.orEmpty().any { it.state != TaskState.Done && it.state != TaskState.Failed }
        viewModelScope.launch(dispatcher.io) {
            currentState.groups.flatMap { it.paths }.filter { it.checked }.forEach { p ->
                // Never wipe partial downloads that are still running.
                if (active && p.isDownloadTemp) return@forEach
                fs.clear(p.path)
            }
            setState { copy(cleaned = true) }
        }
    }
}
