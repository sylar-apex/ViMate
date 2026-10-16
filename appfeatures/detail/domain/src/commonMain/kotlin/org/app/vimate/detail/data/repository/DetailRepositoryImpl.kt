package org.app.vimate.detail.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.detail.domain.model.MediaDetail
import org.app.vimate.detail.domain.repository.DetailRepository
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.logic.CatalogFilters
import org.app.vimate.sharedlogic.settings.AppKeys

class DetailRepositoryImpl(
    private val catalog: CatalogRepository,
    private val userData: UserDataRepository,
    private val settings: ObservableSettings,
) : DetailRepository {
    override fun detail(mediaId: String): Flow<MediaDetail?> = combine(userData.data, settings.version) { data, _ ->
        catalog.media(mediaId)?.let { media ->
            MediaDetail(
                media = media,
                following = media.creator in data.subscriptions,
                related = CatalogFilters.related(catalog.items, media, settings[AppKeys.restrictedBrowsing]),
            )
        }
    }
}
