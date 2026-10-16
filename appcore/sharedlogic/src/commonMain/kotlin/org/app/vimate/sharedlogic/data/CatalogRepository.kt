package org.app.vimate.sharedlogic.data

import org.app.vimate.sharedlogic.catalog.Catalog
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.model.MediaKind
import org.app.vimate.sharedlogic.model.Rendition

/** The open catalog plus local files wrapped as [Media] so they can be played like catalog titles. */
class CatalogRepository {
    private val local = mutableMapOf<String, Media>()

    val items: List<Media> get() = Catalog.items

    fun media(id: String): Media? = Catalog.byId(id) ?: local[id]

    /** Registers a file that is not in the catalog (download, import or device library) and returns its id. */
    fun registerLocal(uri: String, title: String, kind: FileKind, durationMs: Long, bytes: Long): Media {
        val id = LOCAL_PREFIX + uri
        val media = Media(
            id = id,
            title = title,
            creator = "",
            durationMs = durationMs,
            kind = if (kind == FileKind.MUSIC) MediaKind.MUSIC else MediaKind.VIDEO,
            genre = "",
            section = "",
            blurb = "",
            renditions = listOf(Rendition(LOCAL_QUALITY, uri, bytes)),
        )
        local[id] = media
        return media
    }

    companion object {
        const val LOCAL_PREFIX = "local:"
        const val LOCAL_QUALITY = "local"
    }
}
