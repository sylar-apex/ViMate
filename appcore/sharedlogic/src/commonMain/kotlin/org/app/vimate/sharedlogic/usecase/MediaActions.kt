package org.app.vimate.sharedlogic.usecase

import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.MessageKey
import org.app.vimate.sharedlogic.logic.UserDataEdits.toggle
import org.app.vimate.sharedlogic.model.Report
import org.app.vimate.sharedlogic.model.ReportReason
import org.app.vimate.sharedlogic.platform.PlatformActions

/** Adds or removes a title from the batch-download basket. */
class ToggleBasketUseCase(private val userData: UserDataRepository) {
    operator fun invoke(mediaId: String) = userData.update { it.copy(basket = it.basket.toggle(mediaId)) }
}

/** Removes titles from the basket, after they were queued or deleted from the batch list. */
class RemoveFromBasketUseCase(private val userData: UserDataRepository) {
    operator fun invoke(ids: Collection<String>) = userData.update { data -> data.copy(basket = data.basket.filter { it !in ids }) }
}

/** Adds a title to the front of Watch Later, or removes it. */
class ToggleWatchLaterUseCase(private val userData: UserDataRepository, private val events: AppEvents) {
    operator fun invoke(mediaId: String) {
        val added = mediaId !in userData.value.watchLater
        userData.update { it.copy(watchLater = if (added) listOf(mediaId) + it.watchLater else it.watchLater - mediaId) }
        events.message(if (added) MessageKey.WATCH_LATER_ADDED else MessageKey.WATCH_LATER_REMOVED)
    }
}

/** Hides a title from Home and drops it from the basket. */
class MarkNotInterestedUseCase(private val userData: UserDataRepository, private val events: AppEvents) {
    operator fun invoke(mediaId: String) {
        userData.update { it.copy(notInterested = it.notInterested + mediaId, basket = it.basket - mediaId) }
        events.message(MessageKey.HIDDEN_FROM_HOME)
    }
}

/** Stores a report on the device. */
class ReportMediaUseCase(private val userData: UserDataRepository, private val events: AppEvents) {
    operator fun invoke(mediaId: String, reason: ReportReason) {
        userData.update { it.copy(reports = listOf(Report(mediaId, reason, nowMs())) + it.reports) }
        events.closeSheet()
        events.message(MessageKey.REPORT_SAVED)
    }
}

/** Opens the share sheet with the title and creator, attaching the downloaded file when there is one. */
class ShareMediaUseCase(
    private val catalog: CatalogRepository,
    private val userData: UserDataRepository,
    private val platform: PlatformActions,
) {
    operator fun invoke(mediaId: String) {
        val media = catalog.media(mediaId) ?: return
        platform.share(media.title + " — " + media.creator, userData.value.files.firstOrNull { it.mediaId == mediaId }?.uri)
    }
}

/** Follows or unfollows a creator. */
class ToggleSubscriptionUseCase(private val userData: UserDataRepository, private val events: AppEvents) {
    operator fun invoke(creator: String) {
        val following = creator !in userData.value.subscriptions
        userData.update { it.copy(subscriptions = it.subscriptions.toggle(creator)) }
        events.message(if (following) MessageKey.FOLLOWING else MessageKey.UNFOLLOWED, creator)
    }
}
