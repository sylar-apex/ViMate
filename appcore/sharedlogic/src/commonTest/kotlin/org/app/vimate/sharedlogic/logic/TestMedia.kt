package org.app.vimate.sharedlogic.logic

import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.model.MediaKind
import org.app.vimate.sharedlogic.model.Rendition

internal fun testMedia(id: String, adult: Boolean = false, kind: MediaKind = MediaKind.VIDEO) = Media(
    id = id,
    title = id,
    creator = "Creator",
    durationMs = 10_000,
    kind = kind,
    genre = "Test",
    section = "Music",
    blurb = id,
    renditions = listOf(Rendition("480p", "https://cdn.example.com/$id.mp4", 100)),
    adult = adult,
)
