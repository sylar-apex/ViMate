package org.app.vimate.detail.di

import org.app.vimate.detail.repository.DetailRepository
import org.app.vimate.detail.repository.DetailRepositoryImpl
import org.app.vimate.detail.usecase.BuildQualitiesUseCase
import org.app.vimate.detail.viewmodel.DetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the detail feature (repositories, use cases, view models). */
fun detailDomainModule() = module {
    single<DetailRepository> { DetailRepositoryImpl(get(), get()) }
    factory { BuildQualitiesUseCase() }
    viewModel { DetailViewModel(get()) }
}
