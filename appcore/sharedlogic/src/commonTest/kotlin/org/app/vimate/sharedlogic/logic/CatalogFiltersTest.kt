package org.app.vimate.sharedlogic.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.app.vimate.sharedlogic.model.MediaKind
import org.app.vimate.sharedlogic.settings.FeedChip

class CatalogFiltersTest {
    private val open = testMedia("open")
    private val restricted = testMedia("restricted", adult = true)
    private val short = testMedia("short", kind = MediaKind.SHORT)
    private val song = testMedia("song", kind = MediaKind.MUSIC)

    @Test
    fun homeFeedHonorsShortsAndRestrictedMode() {
        val items = listOf(open, restricted, short, song)
        assertEquals(listOf(open.id), CatalogFilters.homeFeed(items, FeedChip.ALL, hideAdult = true).map { it.id })
        assertEquals(listOf(short.id), CatalogFilters.homeFeed(items, FeedChip.SHORTS, hideAdult = false).map { it.id })
        assertTrue(CatalogFilters.homeFeed(items, FeedChip.ALL, hideAdult = false).any { it.id == "restricted" })
    }

    @Test
    fun homeChipsComeFromVideoGenresAndHideNotInterested() {
        val trailer = open.copy(id = "trailer", genre = "Trailers")
        val items = listOf(open, trailer, short, song)
        assertEquals(listOf(FeedChip.ALL, "Test", "Trailers"), CatalogFilters.homeChips(items))
        assertEquals(listOf("trailer"), CatalogFilters.homeFeed(items, "Trailers", hideAdult = false).map { it.id })
        assertEquals(listOf("trailer"), CatalogFilters.homeFeed(items, FeedChip.ALL, hideAdult = false, hidden = listOf("open")).map { it.id })
    }

    @Test
    fun searchUsesItsOwnRestrictionFlag() {
        val items = listOf(open, restricted)
        assertTrue(CatalogFilters.search(items, "open", hideAdult = true).any { it.id == "open" })
        assertTrue(CatalogFilters.search(items, "restricted", hideAdult = false).any { it.id == "restricted" })
        assertTrue(CatalogFilters.search(items, "restricted", hideAdult = true).isEmpty())
        assertTrue(CatalogFilters.search(items, "   ", hideAdult = false).isEmpty())
    }

    @Test
    fun relatedKeepsTheSameKindAndSkipsTheCurrentTitle() {
        val other = testMedia("other")
        assertEquals(listOf("other"), CatalogFilters.related(listOf(open, other, short), open, hideAdult = false).map { it.id })
    }
}
