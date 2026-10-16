package org.app.vimate.home.ui

import androidx.lifecycle.viewModelScope
import com.psbapp.mvi.MVIBaseViewModel
import com.psbapp.utils.dispatcher.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.app.vimate.home.domain.usecase.GetHomeFeedUseCase
import org.app.vimate.home.domain.usecase.MarkShortsSeenUseCase
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.SheetRequest
import org.app.vimate.sharedlogic.settings.FeedChip
import org.app.vimate.sharedlogic.usecase.MarkNotInterestedUseCase
import org.app.vimate.sharedlogic.usecase.ShareMediaUseCase
import org.app.vimate.sharedlogic.usecase.ToggleBasketUseCase
import org.app.vimate.sharedlogic.usecase.ToggleWatchLaterUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    dispatcher: DispatcherProvider,
    getFeed: GetHomeFeedUseCase,
    private val markShortsSeen: MarkShortsSeenUseCase,
    private val toggleBasket: ToggleBasketUseCase,
    private val toggleWatchLater: ToggleWatchLaterUseCase,
    private val shareMedia: ShareMediaUseCase,
    private val markNotInterested: MarkNotInterestedUseCase,
    private val events: AppEvents,
) : MVIBaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    private val selectedChip = MutableStateFlow(FeedChip.ALL)

    override fun createInitialState() = HomeContract.State()
    override fun handleEffect(effect: HomeContract.Effect) {}

    init {
        viewModelScope.launch {
            selectedChip.flatMapLatest { selected -> getFeed(selected) }
                .flowOn(dispatcher.default)
                .collect { feed -> setState { copy(chip = selectedChip.value, feed = feed) } }
        }
    }

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.SelectChip -> {
                selectedChip.value = event.chip
                setEffect(HomeContract.Effect.ScrollToTop)
            }
            is HomeContract.Event.OpenMedia -> setEffect(HomeContract.Effect.OpenDetail(event.mediaId))
            is HomeContract.Event.ToggleBasket -> toggleBasket(event.mediaId)
            is HomeContract.Event.Download -> events.openSheet(SheetRequest.Download(event.mediaId, null))
            is HomeContract.Event.ToggleWatchLater -> toggleWatchLater(event.mediaId)
            is HomeContract.Event.Share -> shareMedia(event.mediaId)
            is HomeContract.Event.NotInterested -> markNotInterested(event.mediaId)
            is HomeContract.Event.Report -> events.openSheet(SheetRequest.Report(event.mediaId))
            HomeContract.Event.OpenShorts -> {
                markShortsSeen()
                setEffect(HomeContract.Effect.OpenShorts)
            }
        }
    }
}
