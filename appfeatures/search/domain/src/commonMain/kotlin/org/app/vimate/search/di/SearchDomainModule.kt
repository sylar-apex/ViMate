package org.app.vimate.search.di

import org.app.vimate.search.usecase.GetSuggestionsUseCase
import org.app.vimate.search.usecase.SearchHistoryUseCase
import org.app.vimate.search.usecase.SearchMusicUseCase
import org.app.vimate.search.usecase.SearchVideosUseCase
import org.app.vimate.search.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the search feature (repositories, use cases, view models). */
fun searchDomainModule() = module {
    factory { SearchHistoryUseCase(get()) }
    factory { GetSuggestionsUseCase(get()) }
    factory { SearchVideosUseCase(get()) }
    factory { SearchMusicUseCase(get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get()) }
}
