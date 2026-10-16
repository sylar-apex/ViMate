package org.app.vimate.home.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.home.domain.model.HomeFeed
import org.app.vimate.sharedlogic.settings.FeedChip

class HomeContract {
    sealed class Event : UiEvent {
        data class SelectChip(val chip: String) : Event()
        data class OpenMedia(val mediaId: String) : Event()
        data class ToggleBasket(val mediaId: String) : Event()
        data class Download(val mediaId: String) : Event()
        data class ToggleWatchLater(val mediaId: String) : Event()
        data class Share(val mediaId: String) : Event()
        data class NotInterested(val mediaId: String) : Event()
        data class Report(val mediaId: String) : Event()
        data object OpenShorts : Event()
    }

    data class State(
        val chip: String = FeedChip.ALL,
        val feed: HomeFeed = HomeFeed(),
    ) : UiState

    sealed class Effect : UiEffect {
        data class OpenDetail(val mediaId: String) : Effect()
        data object OpenShorts : Effect()
        /** The chip changed: the list scrolls back to the top. */
        data object ScrollToTop : Effect()
    }
}
