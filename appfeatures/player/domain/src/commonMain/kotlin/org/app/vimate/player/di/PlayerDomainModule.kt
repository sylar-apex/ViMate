package org.app.vimate.player.di

import org.app.kmediaplayer.KMediaPlayer
import org.app.vimate.player.data.ViMatePlayerHost
import org.koin.core.Koin
import org.koin.dsl.module

fun playerDomainModule() = module {
    single { ViMatePlayerHost(get(), get(), get(), get()) }
}

/** Hands the app's settings store and [ViMatePlayerHost] to the media player library. Call once after Koin starts. */
fun Koin.configureMediaPlayer() {
    KMediaPlayer.configure(settings = get(), host = get<ViMatePlayerHost>())
}
