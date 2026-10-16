package org.app.vimate.di

import com.psbapp.di.initKoin
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.host.PlayerHost
import org.koin.core.Koin
import org.koin.mp.KoinPlatform

private var started = false

/** Starts Koin and hands the app's settings to the media library. For desktop, iOS and web. */
fun initApp() {
    if (started) return
    started = true
    initKoin(AppModules)
    KoinPlatform.getKoin().configureApp()
}

/** Shares the settings store with the media player library. Call once after Koin starts. */
fun Koin.configureApp() {
    KMediaPlayer.configure(settings = get(), host = PlayerHost.None)
}
