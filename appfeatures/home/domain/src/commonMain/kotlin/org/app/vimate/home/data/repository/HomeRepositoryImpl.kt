package org.app.vimate.home.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.home.domain.model.HomeFeed
import org.app.vimate.home.domain.repository.HomeRepository
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.logic.CatalogFilters
import org.app.vimate.sharedlogic.settings.AppKeys

class HomeRepositoryImpl(
    private val catalog: CatalogRepository,
    private val userData: UserDataRepository,
    private val settings: ObservableSettings,
) : HomeRepository {

    override fun feed(chip: String): Flow<HomeFeed> = combine(userData.data, settings.version) { data, _ ->
        HomeFeed(
            chips = CatalogFilters.homeChips(catalog.items),
            items = CatalogFilters.homeFeed(catalog.items, chip, settings[AppKeys.restrictedRecommendations], data.notInterested),
            basket = data.basket.toSet(),
            watchLater = data.watchLater.toSet(),
            shortsSeen = settings[AppKeys.shortsSeen],
        )
    }

    override fun markShortsSeen() {
        if (!settings[AppKeys.shortsSeen]) settings[AppKeys.shortsSeen] = true
    }
}
