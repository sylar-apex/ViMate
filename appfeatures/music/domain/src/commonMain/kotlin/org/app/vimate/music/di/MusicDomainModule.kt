package org.app.vimate.music.di

import org.app.vimate.music.data.MusicRepository
import org.app.vimate.music.data.MusicRepositoryImpl
import org.app.vimate.music.usecase.GetMusicCollectionUseCase
import org.app.vimate.music.usecase.GetMusicShelvesUseCase
import org.app.vimate.music.usecase.GetYtPlaylistUseCase
import org.app.vimate.music.usecase.SearchMusicUseCase
import org.app.vimate.music.ui.MusicCollectionViewModel
import org.app.vimate.music.ui.MusicSearchViewModel
import org.app.vimate.music.ui.MusicViewModel
import org.app.vimate.music.ui.YtPlaylistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the music feature (repositories, use cases, view models). */
fun musicDomainModule() = module {
    single<MusicRepository> { MusicRepositoryImpl() }
    factory { GetMusicShelvesUseCase(get()) }
    factory { GetMusicCollectionUseCase(get()) }
    factory { SearchMusicUseCase(get()) }
    factory { GetYtPlaylistUseCase(get()) }
    viewModel { MusicViewModel(get(), get()) }
    viewModel { MusicCollectionViewModel(get(), get()) }
    viewModel { MusicSearchViewModel(get(), get()) }
    viewModel { YtPlaylistViewModel(get()) }
}
