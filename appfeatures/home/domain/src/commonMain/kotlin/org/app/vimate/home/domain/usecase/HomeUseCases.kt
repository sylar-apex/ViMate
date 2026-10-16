package org.app.vimate.home.domain.usecase

import org.app.vimate.home.domain.repository.HomeRepository

class GetHomeFeedUseCase(private val repository: HomeRepository) {
    operator fun invoke(chip: String) = repository.feed(chip)
}

class MarkShortsSeenUseCase(private val repository: HomeRepository) {
    operator fun invoke() = repository.markShortsSeen()
}
