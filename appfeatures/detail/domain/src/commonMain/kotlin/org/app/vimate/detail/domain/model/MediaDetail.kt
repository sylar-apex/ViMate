package org.app.vimate.detail.domain.model

import org.app.vimate.sharedlogic.model.Media

/** A title's detail page: the title, whether its creator is followed, and up to eight related titles. */
data class MediaDetail(
    val media: Media,
    val following: Boolean,
    val related: List<Media>,
)
