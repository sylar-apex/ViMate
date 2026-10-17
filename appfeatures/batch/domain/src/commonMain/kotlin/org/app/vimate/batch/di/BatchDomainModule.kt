package org.app.vimate.batch.di

import org.app.vimate.batch.usecase.BatchOptionsUseCase
import org.app.vimate.batch.usecase.BatchStorageUseCase
import org.app.vimate.batch.usecase.EnqueueBatchUseCase
import org.app.vimate.batch.usecase.ResolveBatchInfosUseCase
import org.app.vimate.batch.ui.BatchSelectViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the batch feature (repositories, use cases, view models). */
fun batchDomainModule() = module {
    factory { ResolveBatchInfosUseCase() }
    factory { BatchOptionsUseCase() }
    factory { BatchStorageUseCase(get(), get()) }
    factory { EnqueueBatchUseCase(get()) }
    viewModel { BatchSelectViewModel(get(), get(), get(), get()) }
}
