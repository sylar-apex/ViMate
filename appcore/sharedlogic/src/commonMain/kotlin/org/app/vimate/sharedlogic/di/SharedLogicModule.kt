package org.app.vimate.sharedlogic.di

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.platform.HostSignals
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
}

/** Binds [StoreFactory], [org.app.vimate.sharedlogic.platform.PlatformActions] and the device library for the current platform. */
expect fun platformDataModule(): Module
