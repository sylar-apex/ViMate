package org.app.vimate.detail.domain.repository

import kotlinx.coroutines.flow.Flow
import org.app.vimate.detail.domain.model.MediaDetail

interface DetailRepository {
    /** Null when [mediaId] is not a known title. */
    fun detail(mediaId: String): Flow<MediaDetail?>
}
