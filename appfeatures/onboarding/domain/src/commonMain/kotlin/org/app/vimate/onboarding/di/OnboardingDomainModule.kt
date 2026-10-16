package org.app.vimate.onboarding.di

import org.app.vimate.onboarding.ui.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun onboardingDomainModule() = module {
    viewModel { OnboardingViewModel(get()) }
}
