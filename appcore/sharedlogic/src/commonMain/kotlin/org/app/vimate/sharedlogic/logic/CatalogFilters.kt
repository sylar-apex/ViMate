package org.app.vimate.sharedlogic.logic

import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.model.MediaKind
import org.app.vimate.sharedlogic.settings.FeedChip
import org.app.vimate.sharedlogic.settings.MusicSection

/** The lists each screen shows out of the catalog, with the restricted-mode filters applied. */
object CatalogFilters {
    private const val RELATED_LIMIT = 8

    fun allowed(media: Media, hideAdult: Boolean) = !hideAdult || !media.adult

    /** "All" followed by every video genre. */
    fun homeChips(items: List<Media>): List<String> =
        listOf(FeedChip.ALL) + items.filter { it.kind == MediaKind.VIDEO }.map { it.genre }.distinct()

    fun homeFeed(items: List<Media>, chip: String, hideAdult: Boolean, hidden: List<String> = emptyList()): List<Media> {
        val base = items.filter { allowed(it, hideAdult) && it.kind != MediaKind.MUSIC && it.id !in hidden }
        return when (chip) {
            FeedChip.ALL -> base.filter { it.kind == MediaKind.VIDEO }
            FeedChip.SHORTS -> base.filter { it.kind == MediaKind.SHORT }
            else -> base.filter { it.kind == MediaKind.VIDEO && it.genre == chip }
        }
    }

    fun shorts(items: List<Media>): List<Media> = items.filter { it.kind == MediaKind.SHORT }

    fun music(items: List<Media>, section: MusicSection, query: String = ""): List<Media> {
        val songs = items.filter { it.kind == MediaKind.MUSIC }
        val filtered = if (section == MusicSection.ALL) songs else songs.filter { it.section == section.wire }
        val q = query.trim()
        if (q.isEmpty()) return filtered
        return filtered.filter { it.title.contains(q, true) || it.creator.contains(q, true) || it.genre.contains(q, true) }
    }

    fun search(items: List<Media>, query: String, hideAdult: Boolean): List<Media> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return items.filter { allowed(it, hideAdult) }.filter {
            it.title.contains(q, true) || it.creator.contains(q, true) || it.genre.contains(q, true) || it.blurb.contains(q, true)
        }
    }

    fun related(items: List<Media>, current: Media, hideAdult: Boolean): List<Media> =
        items.filter { it.id != current.id && it.kind == current.kind && allowed(it, hideAdult) }.take(RELATED_LIMIT)

    /** Videos shown under "Hot videos" in My Files. */
    fun hotVideos(items: List<Media>, hidden: List<String>): List<Media> =
        items.filter { it.kind == MediaKind.VIDEO && it.id !in hidden && !it.adult }
}
