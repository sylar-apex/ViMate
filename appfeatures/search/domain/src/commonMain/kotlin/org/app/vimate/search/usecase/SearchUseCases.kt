package org.app.vimate.search.usecase

import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.flow.StateFlow
import org.app.vimate.search.constant.SearchTab
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.yt.Page
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

class SearchHistoryUseCase(private val library: Library) {
    val history: StateFlow<List<String>> get() = library.searches.items
    fun add(q: String) { library.addSearch(q) }
    fun clear() { library.searches.clear() }
}

class GetSuggestionsUseCase(private val youTube: YouTube) {
    suspend operator fun invoke(query: String): List<String> = youTube.suggestions(query)
}

class SearchVideosUseCase(private val youTube: YouTube) {
    suspend operator fun invoke(q: String, filter: YouTube.Filter, continuation: String?): Page<Video> = youTube.search(q, filter, continuation)
}

class SearchMusicUseCase(private val youTube: YouTube) {
    suspend operator fun invoke(q: String): List<Video> = youTube.searchMusic(q)
}

/** Browser URL for the tabs that hand the query to a website (POC Picture/Facebook/Instagram). */
object SiteSearchUrl {
    fun of(tab: SearchTab, q: String): String? {
        val e = q.encodeURLParameter(spaceToPlus = true)
        return when (tab) {
            SearchTab.Picture -> "https://www.google.com/search?tbm=isch&q=$e"
            SearchTab.Facebook -> "https://m.facebook.com/search/videos/?q=$e"
            SearchTab.Instagram -> "https://www.instagram.com/explore/search/keyword/?q=$e"
            else -> null
        }
    }
}
