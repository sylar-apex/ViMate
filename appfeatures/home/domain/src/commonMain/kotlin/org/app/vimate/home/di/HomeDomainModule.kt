package org.app.vimate.home.di

import org.app.vimate.home.usecase.GetHomeFeedUseCase
import org.app.vimate.home.usecase.ObserveActiveDownloadsUseCase
import org.app.vimate.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the home feature (repositories, use cases, view models). */
fun homeDomainModule() = module {
    factory { GetHomeFeedUseCase(get()) }
    factory { ObserveActiveDownloadsUseCase(get()) }
    viewModel { HomeViewModel(get(), get()) }
}
