package org.app.vimate.downloads.di

import org.app.vimate.downloads.domain.usecase.GetDownloadOptionsUseCase
import org.app.vimate.downloads.domain.usecase.ObserveBatchUseCase
import org.app.vimate.downloads.ui.BatchViewModel
import org.app.vimate.downloads.ui.DownloadSheetViewModel
import org.app.vimate.downloads.ui.DownloadsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun downloadsDomainModule() = module {
    factory { GetDownloadOptionsUseCase(get(), get()) }
    factory { ObserveBatchUseCase(get(), get()) }
    viewModel { DownloadSheetViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { DownloadsViewModel(get(), get(), get(), get(), get()) }
    viewModel { BatchViewModel(get(), get(), get(), get(), get(), get()) }
}
