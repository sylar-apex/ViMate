package org.app.vimate.detail.di

import org.app.vimate.detail.data.repository.DetailRepositoryImpl
import org.app.vimate.detail.domain.repository.DetailRepository
import org.app.vimate.detail.domain.usecase.GetMediaDetailUseCase
import org.app.vimate.detail.ui.DetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun detailDomainModule() = module {
    factory<DetailRepository> { DetailRepositoryImpl(get(), get(), get()) }
    factory { GetMediaDetailUseCase(get()) }
    viewModel { DetailViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
