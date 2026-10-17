package org.app.vimate.files.di

import org.app.vimate.files.usecase.FilesUseCase
import org.koin.dsl.module

/** Koin bindings of the files feature (repositories, use cases, view models). */
fun filesDomainModule() = module {
    factory { FilesUseCase(get(), get(), get(), get(), get(), get()) }
}
