package org.app.vimate.onboarding.ui

import com.psbapp.mvi.MVIBaseViewModel
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.settings.AppKeys

/** Three intro pages; finishing or skipping stores the flag and the app shell moves on to Home. */
class OnboardingViewModel(
    private val settings: ObservableSettings,
) : MVIBaseViewModel<OnboardingContract.Event, OnboardingContract.State, OnboardingContract.Effect>() {

    override fun createInitialState() = OnboardingContract.State()
    override fun handleEffect(effect: OnboardingContract.Effect) {}

    override fun handleEvent(event: OnboardingContract.Event) {
        when (event) {
            OnboardingContract.Event.Next -> if (currentState.isLast) finish() else setState { copy(page = page + 1) }
            OnboardingContract.Event.Skip -> finish()
        }
    }

    private fun finish() {
        settings[AppKeys.onboardingDone] = true
    }
}
