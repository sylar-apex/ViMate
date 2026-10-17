package org.app.vimate.batch.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.app.vimate.batch.ui.BatchSelectContract.Effect
import org.app.vimate.batch.ui.BatchSelectContract.Event
import org.app.vimate.batch.ui.BatchSelectContract.State
import org.app.vimate.batch.usecase.BatchOptionsUseCase
import org.app.vimate.batch.usecase.BatchStorageUseCase
import org.app.vimate.batch.usecase.EnqueueBatchUseCase
import org.app.vimate.batch.usecase.ResolveBatchInfosUseCase

class BatchSelectViewModel(
    private val resolve: ResolveBatchInfosUseCase,
    private val options: BatchOptionsUseCase,
    private val storage: BatchStorageUseCase,
    private val enqueue: EnqueueBatchUseCase,
) : MVIBaseViewModel<Event, State, Effect>() {
    private var initialized = false
    private var sheetJob: Job? = null

    override fun createInitialState() = State()

    override fun handleEvent(event: Event) {
        when (event) {
            is Event.Init -> if (!initialized) { initialized = true; setState { copy(videos = event.videos, picked = event.videos.map { it.id }) } }
            is Event.Toggle -> setState { copy(picked = if (event.id in picked) picked - event.id else picked + event.id) }
            Event.ToggleAll -> setState { copy(picked = if (picked.size == videos.size) emptyList() else videos.map { it.id }) }
            Event.OpenSheet -> if (currentState.picked.isNotEmpty()) openSheet()
            Event.DismissSheet -> closeSheet()
            is Event.SelectOption -> setState { copy(sel = event.key) }
            Event.Download -> {
                val s = currentState
                if (s.infos.isEmpty()) return
                val added = enqueue(s.sheetVideos, s.infos, s.sel)
                closeSheet()
                setEffect(Effect.Added(added))
            }
        }
    }

    private fun openSheet() {
        val picked = currentState.videos.filter { it.id in currentState.picked }
        setState { copy(sheet = true, sheetVideos = picked, infos = emptyMap(), resolved = 0, music = emptyList(), video = emptyList(), sel = "360P|MP4", folder = this@BatchSelectViewModel.storage.folder, storage = this@BatchSelectViewModel.storage.storage()) }
        sheetJob = viewModelScope.launch {
            resolve(picked) { v, info ->
                updateState {
                    val infos = if (info != null) infos + (v.id to info) else infos
                    val (m, vd) = options(infos.values)
                    copy(infos = infos, resolved = resolved + 1, music = m, video = vd)
                }
            }
        }
    }

    private fun closeSheet() {
        sheetJob?.cancel(); sheetJob = null
        setState { copy(sheet = false) }
    }

    override fun handleEffect(effect: Effect) = Unit
}
