package org.app.vimate.settings.di

import org.app.vimate.settings.data.platformAppCache
import org.app.vimate.settings.domain.repository.AppCache
import org.app.vimate.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Koin bindings of the settings feature (repositories, use cases, view models). */
fun settingsDomainModule() = module {
    single<AppCache> { platformAppCache() }
    viewModel { SettingsViewModel(get(), get(), get()) }
}
