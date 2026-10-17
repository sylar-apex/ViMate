package org.app.vimate

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import com.psbapp.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.app.kdownloader.KDownloader
import org.app.kdownloader.KDownloaderAndroid
import org.app.kdownloader.service.NotificationConfig
import org.app.kmediaplayer.android.KMediaPlayerAndroid
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.di.AppModules
import org.app.vimate.di.configureApp
import org.app.vimate.player.PlayerActivity
import org.app.vimate.sharedlogic.settings.AppKeys
import org.koin.mp.KoinPlatform

class ViMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KMediaPlayerAndroid.init(this, videoActivity = PlayerActivity::class.java, musicActivity = MainActivity::class.java)
        KDownloaderAndroid.init(this)
        initKoin(this, AppModules)
        KoinPlatform.getKoin().configureApp()
        enableDownloadService()
        QuickSearchNotification.bind(this, KoinPlatform.getKoin().get(), CoroutineScope(SupervisorJob() + Dispatchers.Default))
    }

    /** POC DownloadService: ongoing progress, "Download complete" per file (Settings), tap opens My Files. */
    private fun enableDownloadService() {
        val koin = KoinPlatform.getKoin()
        val downloader = koin.get<KDownloader>()
        val settings = koin.get<ObservableSettings>()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            settings.version.map { settings[AppKeys.completeNotify] }.distinctUntilChanged().collect { notify ->
                KDownloaderAndroid.enableForegroundService(
                    downloader,
                    NotificationConfig(
                        channelName = getString(R.string.dl_channel),
                        title = { n -> if (n > 0) resources.getQuantityString(R.plurals.dl_downloading, n, n) else getString(R.string.dl_preparing) },
                        notifyComplete = notify,
                        completeTitle = getString(R.string.dl_complete),
                        contentIntent = { ctx ->
                            PendingIntent.getActivity(
                                ctx, 0,
                                Intent(ctx, MainActivity::class.java).putExtra(MainActivity.EXTRA_TAB, "files").addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                            )
                        },
                    ),
                )
            }
        }
    }
}
