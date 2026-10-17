package org.app.vimate.music.usecase

import org.app.vimate.music.data.MusicRepository
import org.app.vimate.sharedlogic.yt.Shelf
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** Shelves of a Music tab, the "Quick picks" shelf first (POC MusicScreen sort). */
class GetMusicShelvesUseCase(private val repo: MusicRepository) {
    val tabs: List<YouTube.MusicTab> get() = repo.tabs
    suspend operator fun invoke(tab: YouTube.MusicTab): List<Shelf> =
        repo.shelves(tab).sortedByDescending { it.videos.isNotEmpty() && it.title.contains("Quick", true) }
}

class GetMusicCollectionUseCase(private val repo: MusicRepository) {
    suspend operator fun invoke(browseId: String, params: String?): Pair<String, List<Video>> = repo.collection(browseId, params)
}

/** Blank query → no results, as the POC. */
class SearchMusicUseCase(private val repo: MusicRepository) {
    suspend operator fun invoke(query: String): List<Video> = if (query.isBlank()) emptyList() else repo.search(query)
}

class GetYtPlaylistUseCase(private val repo: MusicRepository) {
    suspend operator fun invoke(listId: String): YouTube.PlaylistPage = repo.playlist(listId)
}
