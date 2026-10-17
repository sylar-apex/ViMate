package org.app.vimate.shorts.di

import org.app.vimate.shorts.presentation.ShortsViewModel
import org.app.vimate.shorts.repository.ShortsRepository
import org.app.vimate.shorts.repository.ShortsRepositoryImpl
import org.app.vimate.shorts.usecase.LoadShortInfoUseCase
import org.app.vimate.shorts.usecase.LoadShortsPageUseCase
import org.app.vimate.shorts.usecase.ToggleFollowUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the shorts feature (repositories, use cases, view models). */
fun shortsDomainModule() = module {
    single<ShortsRepository> { ShortsRepositoryImpl() }
    factory { LoadShortsPageUseCase(get()) }
    factory { LoadShortInfoUseCase(get()) }
    factory { ToggleFollowUseCase(get()) }
    viewModel { ShortsViewModel(get(), get(), get(), get()) }
}
