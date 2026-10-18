package org.app.vimate.sharedlogic.library

import kotlinx.serialization.Serializable
import org.app.vimate.sharedlogic.data.StoreFactory
import org.app.kmediaplayer.util.nowMs

/** What a bookmark points at; decides how it opens (video page, music player, browser). */
@Serializable
enum class BookmarkKind { Video, Music, Site }

/** A folder of bookmarks; [parentId] null is the top level. Folders nest without limit. */
@Serializable
data class BookmarkFolder(val id: Long, val name: String, val parentId: Long? = null)

/** A saved video, song or web page inside folder [folderId] (null = top level). */
@Serializable
data class Bookmark(
    val id: Long, val folderId: Long?, val kind: BookmarkKind, val title: String, val url: String,
    val thumbnail: String = "", val subtitle: String = "", val videoId: String? = null, val addedAt: Long = nowMs(),
)

/** Bookmarks with nested folders (replaces the old playlists), kept on this device. */
class Bookmarks(stores: StoreFactory) {
    private val store = stores.open("bookmarks")
    val folders = PersistedList(store, "folders", BookmarkFolder.serializer(), limit = 2000)
    val items = PersistedList(store, "items", Bookmark.serializer(), limit = 20000)

    fun createFolder(name: String, parentId: Long?): Long {
        val id = nowMs()
        folders.update { it + BookmarkFolder(id, name.trim().ifEmpty { "Folder" }, parentId) }
        return id
    }

    fun renameFolder(id: Long, name: String) = folders.update { l -> l.map { if (it.id == id) it.copy(name = name.trim().ifEmpty { it.name }) else it } }

    /** Deletes folder [id], its subfolders and everything saved in them. */
    fun deleteFolder(id: Long) {
        val all = folders.items.value
        fun descendants(f: Long): Set<Long> = setOf(f) + all.filter { it.parentId == f }.flatMap { descendants(it.id) }
        val gone = descendants(id)
        folders.update { l -> l.filter { it.id !in gone } }
        items.update { l -> l.filter { it.folderId !in gone } }
    }

    /** Saves [b] into [folderId]; the same url already there is replaced, not duplicated. */
    fun save(b: Bookmark, folderId: Long?) = items.update { l ->
        listOf(b.copy(id = nowMs(), folderId = folderId, addedAt = nowMs())) + l.filterNot { it.folderId == folderId && it.url == b.url }
    }

    fun remove(id: Long) = items.update { l -> l.filter { it.id != id } }

    fun move(id: Long, folderId: Long?) = items.update { l -> l.map { if (it.id == id) it.copy(folderId = folderId) else it } }

    /** Folders from the top level down to [id], for the breadcrumb. */
    fun path(id: Long?): List<BookmarkFolder> {
        val all = folders.items.value.associateBy { it.id }
        val out = ArrayDeque<BookmarkFolder>()
        var cur = id?.let(all::get)
        while (cur != null) { out.addFirst(cur); cur = cur.parentId?.let(all::get) }
        return out.toList()
    }
}
