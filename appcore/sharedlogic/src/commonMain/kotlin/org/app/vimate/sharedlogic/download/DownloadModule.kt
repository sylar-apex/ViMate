package org.app.vimate.sharedlogic.download

import org.app.kdownloader.KDownloader
import org.app.kdownloader.model.DownloaderConfig
import org.app.kdownloader.store.JsonTaskStore
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.download.vid.KVidDownloads
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.settings.AppKeys
import org.koin.dsl.module

/** The download engine (KDownloader) and the POC download manager API on top of it. */
fun downloadModule() = module {
    single {
        val store = get<StoreFactory>().open(STORE)
        val settings = get<ObservableSettings>()
        KDownloader(
            config = DownloaderConfig(
                maxParallel = settings[AppKeys.maxTasks],
                connections = settings[AppKeys.connections].coerceIn(1, 8),
                wifiOnly = settings[AppKeys.wifiOnly],
                autoResume = settings[AppKeys.autoResume],
                appFolder = KVidDownloads.folder(settings),
                userAgent = KVidDownloads.UA,
            ),
            store = JsonTaskStore(read = { store.getString(TASKS_KEY) }, write = { store.putString(TASKS_KEY, it) }),
            httpClient = downloadHttpClient(),
        )
    }
    single<VidDownloads>(createdAtStart = true) { KVidDownloads(get(), get()) }
}

private const val STORE = "downloads"
private const val TASKS_KEY = "vid_tasks"
