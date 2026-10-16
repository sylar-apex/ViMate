package org.app.vimate.detail.domain.usecase

import org.app.vimate.detail.domain.repository.DetailRepository

class GetMediaDetailUseCase(private val repository: DetailRepository) {
    operator fun invoke(mediaId: String) = repository.detail(mediaId)
}
