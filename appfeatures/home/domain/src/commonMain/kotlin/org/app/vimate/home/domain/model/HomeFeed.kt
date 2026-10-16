package org.app.vimate.home.domain.model

import org.app.vimate.sharedlogic.model.Media

/** Everything the Home tab shows for the selected chip. */
data class HomeFeed(
    val chips: List<String> = emptyList(),
    val items: List<Media> = emptyList(),
    val basket: Set<String> = emptySet(),
    val watchLater: Set<String> = emptySet(),
    /** False until the Shorts chip is opened once; it shows a red dot until then. */
    val shortsSeen: Boolean = true,
)
