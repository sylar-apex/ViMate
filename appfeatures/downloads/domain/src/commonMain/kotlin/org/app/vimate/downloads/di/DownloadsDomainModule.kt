package org.app.vimate.downloads.di

import org.app.vimate.downloads.usecase.DownloadSheetUseCase
import org.koin.dsl.module

/** Koin bindings of the downloads feature (repositories, use cases, view models). */
fun downloadsDomainModule() = module {
    factory { DownloadSheetUseCase(get(), get(), get()) }
}
