package org.app.vimate.sharedlogic.download

import org.app.kdownloader.KDownloader
import org.app.kdownloader.model.DownloaderConfig
import org.app.kdownloader.store.JsonTaskStore
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.vimate.sharedlogic.download.mapper.DownloadMapper
import org.app.vimate.sharedlogic.download.remote.DownloadService
import org.app.vimate.sharedlogic.download.remote.DownloadServiceImpl
import org.app.vimate.sharedlogic.download.remote.downloadHttpClient
import org.app.vimate.sharedlogic.download.repository.DownloadRepository
import org.app.vimate.sharedlogic.download.repository.DownloadRepositoryImpl
import org.app.vimate.sharedlogic.download.usecase.CancelDownloadUseCase
import org.app.vimate.sharedlogic.download.usecase.DeleteDownloadsUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadBatchUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadLinkUseCase
import org.app.vimate.sharedlogic.download.usecase.DownloadMediaUseCase
import org.app.vimate.sharedlogic.download.usecase.GetDownloadPathUseCase
import org.app.vimate.sharedlogic.download.usecase.GetStorageInfoUseCase
import org.app.vimate.sharedlogic.download.usecase.ObserveDownloadsUseCase
import org.app.vimate.sharedlogic.download.usecase.PauseAllDownloadsUseCase
import org.app.vimate.sharedlogic.download.usecase.PauseDownloadUseCase
import org.app.vimate.sharedlogic.download.usecase.ResumeAllDownloadsUseCase
import org.app.vimate.sharedlogic.download.usecase.ResumeDownloadUseCase
import org.app.vimate.sharedlogic.download.usecase.SetDownloadFolderUseCase
import org.koin.dsl.module

/** The download engine, its repository and the download use cases. */
fun downloadModule() = module {
    single {
        val store = get<StoreFactory>().open(STORE)
        KDownloader(
            config = DownloaderConfig(appFolder = DownloadRepositoryImpl.APP_FOLDER, userAgent = DownloadRepositoryImpl.USER_AGENT),
            store = JsonTaskStore(read = { store.getString(TASKS_KEY) }, write = { store.putString(TASKS_KEY, it) }),
            httpClient = downloadHttpClient(),
        )
    }
    single<DownloadService> { DownloadServiceImpl(get()) }
    factory { DownloadMapper() }
    single<DownloadRepository> { DownloadRepositoryImpl(get(), get(), get()) }
    single(createdAtStart = true) { DownloadCompletionWatcher(get(), get(), get(), get(), get(), get()).apply { start() } }
    factory { ObserveDownloadsUseCase(get()) }
    factory { DownloadMediaUseCase(get(), get(), get(), get()) }
    factory { DownloadLinkUseCase(get(), get()) }
    factory { DownloadBatchUseCase(get(), get(), get(), get()) }
    factory { PauseDownloadUseCase(get()) }
    factory { ResumeDownloadUseCase(get()) }
    factory { CancelDownloadUseCase(get()) }
    factory { DeleteDownloadsUseCase(get(), get()) }
    factory { PauseAllDownloadsUseCase(get(), get()) }
    factory { ResumeAllDownloadsUseCase(get(), get()) }
    factory { GetDownloadPathUseCase(get(), get()) }
    factory { SetDownloadFolderUseCase(get()) }
    factory { GetStorageInfoUseCase(get()) }
}

private const val STORE = "downloads"
private const val TASKS_KEY = "tasks"
