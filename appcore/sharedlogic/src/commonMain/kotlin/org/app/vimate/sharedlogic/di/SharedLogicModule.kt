package org.app.vimate.sharedlogic.di

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.platform.HostSignals
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.usecase.MarkNotInterestedUseCase
import org.app.vimate.sharedlogic.usecase.GetPlayerFilesUseCase
import org.app.vimate.sharedlogic.usecase.PlayMediaUseCase
import org.app.vimate.sharedlogic.usecase.RemoveFromBasketUseCase
import org.app.vimate.sharedlogic.usecase.ReportMediaUseCase
import org.app.vimate.sharedlogic.usecase.ShouldAutoPlayUseCase
import org.app.vimate.sharedlogic.usecase.ShareMediaUseCase
import org.app.vimate.sharedlogic.usecase.ToggleBasketUseCase
import org.app.vimate.sharedlogic.usecase.ToggleSubscriptionUseCase
import org.app.vimate.sharedlogic.usecase.ToggleWatchLaterUseCase
import org.app.vimate.sharedlogic.settings.appSettingRegistries
import org.koin.core.module.Module
import org.koin.dsl.module

/** App-wide data: settings, user records, the catalog, the event bus and platform services. */
fun sharedLogicModule() = module {
    includes(platformDataModule())
    single { ObservableSettings(get<StoreFactory>().open("settings"), appSettingRegistries) }
    single { UserDataRepository(get<StoreFactory>().open("user")) }
    single { CatalogRepository() }
    single { AppEvents() }
    single { HostSignals() }
    single { PlaybackLauncher(get(), get(), get()) }
    factory { ToggleBasketUseCase(get()) }
    factory { RemoveFromBasketUseCase(get()) }
    factory { ToggleWatchLaterUseCase(get(), get()) }
    factory { MarkNotInterestedUseCase(get(), get()) }
    factory { ReportMediaUseCase(get(), get()) }
    factory { ShareMediaUseCase(get(), get(), get()) }
    factory { PlayMediaUseCase(get()) }
    factory { ToggleSubscriptionUseCase(get(), get()) }
    factory { ShouldAutoPlayUseCase(get()) }
    factory { GetPlayerFilesUseCase(get()) }
}

/** Binds [StoreFactory], [org.app.vimate.sharedlogic.platform.PlatformActions] and the device library for the current platform. */
expect fun platformDataModule(): Module
