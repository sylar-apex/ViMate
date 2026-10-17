package org.app.vimate.music.data

import org.app.vimate.sharedlogic.yt.Shelf
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** YouTube Music data used by the Music tab and its sub-pages (POC ui/MusicScreen.kt `YouTube.*` calls). */
interface MusicRepository {
    val tabs: List<YouTube.MusicTab>
    suspend fun shelves(tab: YouTube.MusicTab): List<Shelf>
    suspend fun collection(browseId: String, params: String?): Pair<String, List<Video>>
    suspend fun search(query: String): List<Video>
    suspend fun playlist(listId: String): YouTube.PlaylistPage
}

class MusicRepositoryImpl : MusicRepository {
    override val tabs: List<YouTube.MusicTab> get() = YouTube.MusicTab.entries
    override suspend fun shelves(tab: YouTube.MusicTab) = YouTube.music(tab)
    override suspend fun collection(browseId: String, params: String?) = YouTube.musicCollection(browseId, params)
    override suspend fun search(query: String) = YouTube.searchMusic(query)
    override suspend fun playlist(listId: String) = YouTube.playlist(listId)
}
