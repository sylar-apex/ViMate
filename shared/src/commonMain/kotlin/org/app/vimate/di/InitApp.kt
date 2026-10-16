package org.app.vimate.di

import com.psbapp.di.initKoin
import org.app.vimate.player.di.configureMediaPlayer
import org.koin.core.Koin
import org.koin.mp.KoinPlatform

private var started = false

/** Starts Koin and hands the app's settings and player host to the media library. For desktop, iOS and web. */
fun initApp() {
    if (started) return
    started = true
    initKoin(AppModules)
    KoinPlatform.getKoin().configureApp()
}

/** Connects the media player library to the app. Call once after Koin starts. */
fun Koin.configureApp() {
    configureMediaPlayer()
}
