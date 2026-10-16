package org.app.vimate.downloads.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.downloads.domain.model.BatchItem
import org.app.vimate.downloads.domain.model.DownloadOptions
import org.app.vimate.downloads.domain.model.QualityChoice
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.logic.LinkClassifier
import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.settings.AppKeys

/** The qualities, formats and sizes the sheet offers for a catalog title ([mediaId]) or a pasted link ([url]). */
class GetDownloadOptionsUseCase(private val catalog: CatalogRepository, private val settings: ObservableSettings) {
    operator fun invoke(mediaId: String?, url: String?): DownloadOptions? {
        val media = mediaId?.let(catalog::media)
        if (media != null) {
            val preferred = if (media.isMusic) settings[AppKeys.musicQuality] else settings[AppKeys.onlineQuality]
            return DownloadOptions(
                media = media,
                url = null,
                kind = media.fileKind,
                defaultName = media.title,
                choices = Renditions.sorted(media).map { QualityChoice(it.quality, it.quality.uppercase(), LinkClassifier.container(it.url), it.bytes) },
                defaultChoice = Renditions.pick(media, preferred).quality,
            )
        }
        val link = url ?: return null
        return DownloadOptions(
            media = null,
            url = link,
            kind = LinkClassifier.kindForUrl(link),
            defaultName = LinkClassifier.lastSegment(link),
            choices = listOf(QualityChoice(DownloadOptions.LINK_KEY, "", LinkClassifier.container(link), 0)),
            defaultChoice = DownloadOptions.LINK_KEY,
        )
    }
}

/** The basket's titles, in basket order, with their renditions. */
class ObserveBatchUseCase(private val catalog: CatalogRepository, private val userData: UserDataRepository) {
    operator fun invoke(): Flow<List<BatchItem>> = userData.data.map { data ->
        data.basket.mapNotNull(catalog::media).map { BatchItem(it, Renditions.sorted(it)) }
    }
}
