package org.app.vimate.detail.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import com.psbapp.utils.dispatcher.DispatcherProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.app.vimate.detail.domain.usecase.GetMediaDetailUseCase
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.SheetRequest
import org.app.vimate.sharedlogic.usecase.GetPlayerFilesUseCase
import org.app.vimate.sharedlogic.usecase.ShouldAutoPlayUseCase
import org.app.vimate.sharedlogic.usecase.PlayMediaUseCase
import org.app.vimate.sharedlogic.usecase.ToggleSubscriptionUseCase

class DetailViewModel(
    private val dispatcher: DispatcherProvider,
    private val getDetail: GetMediaDetailUseCase,
    private val play: PlayMediaUseCase,
    private val shouldAutoPlay: ShouldAutoPlayUseCase,
    private val playerFiles: GetPlayerFilesUseCase,
    private val toggleSubscription: ToggleSubscriptionUseCase,
    private val events: AppEvents,
) : MVIBaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    private var loadJob: Job? = null

    override fun createInitialState() = DetailContract.State()
    override fun handleEffect(effect: DetailContract.Effect) {}

    override fun handleEvent(event: DetailContract.Event) {
        val media = currentState.detail?.media
        when (event) {
            is DetailContract.Event.Load -> {
                setState { copy(autoPlay = event.autoPlay && shouldAutoPlay()) }
                load(event.mediaId)
            }
            DetailContract.Event.Play -> media?.let { play(it.id) }
            DetailContract.Event.Download -> media?.let { events.openSheet(SheetRequest.Download(it.id, null)) }
            DetailContract.Event.ToggleFollow -> media?.let { toggleSubscription(it.creator) }
            is DetailContract.Event.OpenRelated -> setEffect(DetailContract.Effect.OpenDetail(event.mediaId))
        }
    }

    private fun load(mediaId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            getDetail(mediaId).flowOn(dispatcher.default).collect { detail ->
                val files = detail?.media?.takeIf { !it.isMusic }?.let(playerFiles::invoke).orEmpty()
                setState { copy(detail = detail, videoFiles = if (videoFiles.isEmpty()) files else videoFiles) }
            }
        }
    }
}
