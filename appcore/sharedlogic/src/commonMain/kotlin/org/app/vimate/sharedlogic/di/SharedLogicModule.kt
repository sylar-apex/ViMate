package org.app.vimate.sharedlogic.di

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.PrivateSession
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.download.downloadModule
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.platform.HostSignals
import org.app.vimate.sharedlogic.platform.HostWatchers
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.settings.appSettingRegistries
import org.app.vimate.sharedlogic.sites.Instagram
import org.app.vimate.sharedlogic.yt.YouTube
import org.koin.core.module.Module
import org.koin.dsl.module

/** App-wide data: settings, library, YouTube/Instagram clients, the event bus and platform services. */
fun sharedLogicModule() = module {
    includes(platformDataModule(), downloadModule())
    single {
        ObservableSettings(get<StoreFactory>().open("settings"), appSettingRegistries).also {
            YouTube.gl = it[AppKeys.location]
        }
    }
    single { Library(get()) }
    single { YouTube }
    single { Instagram }
    single { AppEvents() }
    single { HostSignals() }
    single { PrivateSession() }
    single(createdAtStart = true) { HostWatchers(get(), get(), get()).apply { start() } }
    single { PlaybackLauncher(get()) }
}

/** Binds [StoreFactory], [org.app.vimate.sharedlogic.platform.PlatformActions] and the device library for the current platform. */
expect fun platformDataModule(): Module
