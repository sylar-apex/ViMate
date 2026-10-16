package org.app.vimate.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.app.vimate.home.domain.model.HomeFeed

interface HomeRepository {
    /** The feed for [chip], updated whenever the basket, hidden titles or filters change. */
    fun feed(chip: String): Flow<HomeFeed>
    fun markShortsSeen()
}
