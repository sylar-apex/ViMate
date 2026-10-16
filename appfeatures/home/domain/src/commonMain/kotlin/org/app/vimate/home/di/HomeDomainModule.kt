package org.app.vimate.home.di

import org.app.vimate.home.data.repository.HomeRepositoryImpl
import org.app.vimate.home.domain.repository.HomeRepository
import org.app.vimate.home.domain.usecase.GetHomeFeedUseCase
import org.app.vimate.home.domain.usecase.MarkShortsSeenUseCase
import org.app.vimate.home.ui.HomeViewModel
import org.app.vimate.home.ui.ReportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun homeDomainModule() = module {
    factory<HomeRepository> { HomeRepositoryImpl(get(), get(), get()) }
    factory { GetHomeFeedUseCase(get()) }
    factory { MarkShortsSeenUseCase(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ReportViewModel(get()) }
}
