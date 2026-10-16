package org.app.vimate.sharedlogic.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import org.app.vimate.sharedlogic.model.Rendition

class RenditionsTest {
    private val open = testMedia("open")

    @Test
    fun renditionsSortByQualityIgnoringCase() {
        val movie = open.copy(
            renditions = listOf(
                Rendition("1080P", "https://cdn.example.com/hi.mov", 30),
                Rendition("240p", "https://cdn.example.com/lo.mp4", 10),
                Rendition("540p", "https://cdn.example.com/mid.mp4", 20),
            ),
        )
        assertEquals(listOf("240p", "540p", "1080P"), Renditions.sorted(movie).map { it.quality })
        assertEquals("1080P", Renditions.topQuality(movie))
        assertEquals("540p", Renditions.pick(movie, "720p").quality)
    }

    @Test
    fun picksTheHighestRenditionAtOrBelowThePreference() {
        val movie = open.copy(
            renditions = listOf(
                Rendition("180p", "https://cdn.example.com/low.mp4", 10),
                Rendition("720p", "https://cdn.example.com/high.mp4", 20),
            ),
        )
        assertEquals("180p", Renditions.pick(movie, "360p").quality)
        assertEquals("720p", Renditions.pick(movie, "1080p").quality)
        assertEquals("480p", Renditions.pick(open, "1080p").quality)
        assertEquals("720p", Renditions.exactOrPick(movie, "720p").quality)
    }
}
