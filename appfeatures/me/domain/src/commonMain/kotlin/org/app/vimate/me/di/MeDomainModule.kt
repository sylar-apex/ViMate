package org.app.vimate.me.di

import org.app.vimate.me.data.junk.platformJunkFileSystem
import org.app.vimate.me.data.repository.MeRepositoryImpl
import org.app.vimate.me.domain.repository.JunkFileSystem
import org.app.vimate.me.domain.repository.MeRepository
import org.app.vimate.me.ui.BoostViewModel
import org.app.vimate.me.ui.JunkViewModel
import org.app.vimate.me.ui.LibraryViewModel
import org.app.vimate.me.ui.MeViewModel
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the me feature (repositories, use cases, view models). */
fun meDomainModule() = module {
    single<MeRepository> { MeRepositoryImpl(get(), get(), get()) }
    single<JunkFileSystem> { platformJunkFileSystem() }
    viewModel { MeViewModel(get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { JunkViewModel(get(), getOrNull<VidDownloads>(), get()) }
    viewModel { BoostViewModel(get(), get()) }
}
