package org.app.vimate.shorts.usecase

import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.shorts.repository.ShortsRepository

class LoadShortsPageUseCase(private val repo: ShortsRepository) {
    suspend operator fun invoke(query: String, continuation: String?) = repo.page(query, continuation)
}

class LoadShortInfoUseCase(private val repo: ShortsRepository) {
    suspend operator fun invoke(id: String) = repo.info(id)
}

class ToggleFollowUseCase(private val library: Library) {
    operator fun invoke(c: Channel) { library.toggleSubscription(c) }
}
