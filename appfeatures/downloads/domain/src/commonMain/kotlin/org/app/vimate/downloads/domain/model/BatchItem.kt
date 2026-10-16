package org.app.vimate.downloads.domain.model

import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.model.Rendition

/** A basket title with every rendition it can be downloaded in. */
data class BatchItem(val media: Media, val renditions: List<Rendition>) {
    /** The quality this title downloads in for the batch-wide [preset]: the best one at or below it. */
    fun qualityFor(preset: String): String = Renditions.pick(media, preset).quality
}
