package org.app.vimate.onboarding.ui

import com.psbapp.mvi.UiEffect
import com.psbapp.mvi.UiEvent
import com.psbapp.mvi.UiState
import org.app.vimate.onboarding.domain.constant.OnboardingLimits

class OnboardingContract {
    sealed class Event : UiEvent {
        data object Next : Event()
        data object Skip : Event()
    }

    data class State(val page: Int = 0) : UiState {
        val isLast: Boolean get() = page == OnboardingLimits.PAGE_COUNT - 1
    }

    sealed class Effect : UiEffect
}
