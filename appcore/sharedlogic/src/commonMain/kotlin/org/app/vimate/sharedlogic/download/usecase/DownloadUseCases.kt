package org.app.vimate.sharedlogic.download.usecase

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.download.model.DownloadRequestData
import org.app.vimate.sharedlogic.download.model.EnqueueOutcome
import org.app.vimate.sharedlogic.download.repository.DownloadRepository
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.MessageKey
import org.app.vimate.sharedlogic.logic.LinkClassifier
import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.LinkKind
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.settings.DownloadFolder

class ObserveDownloadsUseCase(private val repository: DownloadRepository) {
    operator fun invoke() = repository.downloads
}

/** Queues a catalog title at [quality] (else the quality setting), saved as [title] (else its own title). */
class DownloadMediaUseCase(
    private val catalog: CatalogRepository,
    private val settings: ObservableSettings,
    private val repository: DownloadRepository,
    private val events: AppEvents,
) {
    operator fun invoke(mediaId: String, quality: String? = null, title: String? = null) {
        val media = catalog.media(mediaId) ?: return
        val preferred = if (media.isMusic) settings[AppKeys.musicQuality] else settings[AppKeys.onlineQuality]
        val rendition = quality?.let { q -> media.renditions.firstOrNull { it.quality == q } } ?: Renditions.pick(media, preferred)
        val name = title?.trim()?.takeIf { it.isNotEmpty() } ?: media.title
        val outcome = repository.enqueue(DownloadRequestData(media.id, name, rendition.url, media.fileKind, rendition.bytes))
        events.closeSheet()
        events.message(if (outcome == EnqueueOutcome.ADDED) MessageKey.ADDED_TO_DOWNLOADS else MessageKey.ALREADY_QUEUED)
    }
}

/** Queues a pasted direct media link; page links and plain text are refused with a reason. */
class DownloadLinkUseCase(private val repository: DownloadRepository, private val events: AppEvents) {
    operator fun invoke(raw: String, title: String? = null) {
        when (LinkClassifier.classify(raw)) {
            LinkKind.NOT_A_URL -> events.message(MessageKey.NOT_A_URL)
            LinkKind.PAGE -> events.message(MessageKey.PAGE_NOT_MEDIA)
            LinkKind.DIRECT_MEDIA -> {
                val url = raw.trim()
                val name = title?.trim()?.takeIf { it.isNotEmpty() } ?: LinkClassifier.lastSegment(url)
                val outcome = repository.enqueue(DownloadRequestData(null, name, url, LinkClassifier.kindForUrl(url), 0))
                events.closeSheet()
                events.message(if (outcome == EnqueueOutcome.ADDED) MessageKey.ADDED_TO_DOWNLOADS else MessageKey.ALREADY_QUEUED)
            }
        }
    }
}

/** Queues each basket title at the quality chosen for it, then empties those from the basket. */
class DownloadBatchUseCase(
    private val catalog: CatalogRepository,
    private val userData: UserDataRepository,
    private val repository: DownloadRepository,
    private val events: AppEvents,
) {
    operator fun invoke(choices: Map<String, String>) {
        if (choices.isEmpty()) return
        choices.forEach { (id, quality) ->
            val media = catalog.media(id) ?: return@forEach
            val rendition = Renditions.exactOrPick(media, quality)
            repository.enqueue(DownloadRequestData(media.id, media.title, rendition.url, media.fileKind, rendition.bytes))
        }
        userData.update { data -> data.copy(basket = data.basket.filter { it !in choices.keys }) }
        events.message(MessageKey.ADDED_N_TO_DOWNLOADS, choices.size.toString())
    }
}

class PauseDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(id: String) = repository.pause(id)
}

class ResumeDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(id: String) = repository.resume(id)
}

class CancelDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(id: String) = repository.cancel(id)
}

/** Removes tasks from the list; running ones stop and their partial files are deleted. Finished files stay in My Files. */
class DeleteDownloadsUseCase(private val repository: DownloadRepository, private val events: AppEvents) {
    operator fun invoke(ids: Collection<String>) {
        repository.delete(ids)
        events.message(MessageKey.DELETED_N, ids.size.toString())
    }
}

class PauseAllDownloadsUseCase(private val repository: DownloadRepository, private val events: AppEvents) {
    operator fun invoke() {
        val count = repository.pauseAll()
        if (count == 0) events.message(MessageKey.NO_DOWNLOADS_RUNNING) else events.message(MessageKey.PAUSED_N, count.toString())
    }
}

class ResumeAllDownloadsUseCase(private val repository: DownloadRepository, private val events: AppEvents) {
    operator fun invoke() {
        val count = repository.resumeAll()
        if (count == 0) events.message(MessageKey.NOTHING_TO_RESUME) else events.message(MessageKey.RESUMED_N, count.toString())
    }
}

/** The save folder shown on the download sheet for [kind], with the folder setting or [folder]. */
class GetDownloadPathUseCase(private val repository: DownloadRepository, private val settings: ObservableSettings) {
    operator fun invoke(kind: FileKind, folder: DownloadFolder = DownloadFolder.fromWire(settings[AppKeys.downloadFolder])) =
        repository.location(kind, folder)
}

class SetDownloadFolderUseCase(private val settings: ObservableSettings) {
    operator fun invoke(folder: DownloadFolder) { settings[AppKeys.downloadFolder] = folder.wire }
}

class GetStorageInfoUseCase(private val platform: PlatformActions) {
    operator fun invoke(): StorageInfo = platform.storageInfo()
}
