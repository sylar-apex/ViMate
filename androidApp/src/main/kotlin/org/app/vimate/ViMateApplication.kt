package org.app.vimate

import android.app.Application
import com.psbapp.di.initKoin
import org.app.kdownloader.KDownloaderAndroid
import org.app.kmediaplayer.android.KMediaPlayerAndroid
import org.app.vimate.di.AppModules
import org.app.vimate.di.configureApp
import org.app.vimate.player.PlayerActivity
import org.koin.mp.KoinPlatform

class ViMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KMediaPlayerAndroid.init(this, videoActivity = PlayerActivity::class.java, musicActivity = MainActivity::class.java)
        KDownloaderAndroid.init(this)
        initKoin(this, AppModules)
        KoinPlatform.getKoin().configureApp()
    }
}
