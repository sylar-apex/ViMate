package org.app.vimate.appcore.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** Bottom tabs, in POC order (Navigator.tab index). */
object Tab {
    const val HOME = 0
    const val MUSIC = 1
    const val FILES = 2
    const val ME = 3
}

/**
 * POC ui/Nav.kt `Navigator` on top of the navigation-compose back stack. Screens get it from
 * [LocalAppNavigator] and call it exactly as the POC called `LocalNav.current`.
 */
@Stable
class AppNavigator(private val nav: NavController) {
    /** Selected bottom tab ([Tab]). */
    var tab by mutableIntStateOf(Tab.HOME)

    /** Video for which the download sheet is open (shown over any screen). */
    var downloadFor by mutableStateOf<Video?>(null)

    /** True when a sub-page is above the tabs (POC `stack.size > 1`). */
    val canGoBack: Boolean get() = nav.previousBackStackEntry != null

    /** A place in the title-bar history: a tab, or a page opened over [tab]. */
    private data class Place(val tab: Int, val route: Any?)

    // Every place visited (tabs and pages), and where the title bar's back / forward arrows stand in it.
    private val history = mutableStateListOf(Place(Tab.HOME, null))
    private var index by mutableIntStateOf(0)

    /**
     * Title-bar refresh: reloads the browser page, else opens the current page again as a fresh back-stack
     * entry. A new entry gets new view models, so every API call of that page runs again.
     */
    fun refresh() {
        webNav?.let { it.reload(); return }
        val route = history.getOrNull(index)?.route
        if (route != null && nav.previousBackStackEntry != null) {
            nav.popBackStack(); nav.navigate(route)
        } else nav.navigate(Routes.Tabs) { popUpTo(Routes.Tabs) { inclusive = true } }
    }

    /** The in-app browser while it is open: the arrows step through its own pages first. */
    var webNav by mutableStateOf<WebNav?>(null)

    val canGoHistoryBack: Boolean get() = webNav?.canBack() == true || index > 0
    val canGoForward: Boolean get() = webNav?.canForward() == true || index < history.lastIndex

    private fun record(place: Place) {
        if (history.getOrNull(index) == place) return
        while (history.lastIndex > index) history.removeAt(history.lastIndex)
        history += place; index = history.lastIndex
    }

    fun go(route: Any) { record(Place(tab, route)); nav.navigate(route) }

    /** Pops one screen (system / page back); false when already on the tabs. */
    fun back(): Boolean = if (nav.previousBackStackEntry != null) {
        nav.popBackStack()
        if (index > 0) index--
        true
    } else false

    /** Title-bar back: the browser's previous page, else the previous place (page or tab). */
    fun historyBack() {
        webNav?.takeIf { it.canBack() }?.let { it.back(); return }
        if (index > 0) { index--; show(history[index]) }
    }

    /** Title-bar forward: the browser's next page, else the place left with [historyBack]. */
    fun forward() {
        webNav?.takeIf { it.canForward() }?.let { it.forward(); return }
        if (index < history.lastIndex) { index++; show(history[index]) }
    }

    private fun show(p: Place) {
        nav.popBackStack(Routes.Tabs, inclusive = false)
        tab = p.tab
        p.route?.let { nav.navigate(it) }
    }

    /** Clears every sub-page and selects [tab]. */
    fun home(tab: Int) {
        nav.popBackStack(Routes.Tabs, inclusive = false)
        this.tab = tab
        record(Place(tab, null))
    }

    /** Opens a YouTube link natively, anything else in the in-app browser. */
    fun open(url: String) {
        val id = YouTube.idFromUrl(url)
        if (id != null) go(Routes.Detail.of(Video(id, "")))
        else go(Routes.Browser(if ("://" in url) url else "https://$url"))
    }

    /** Item waiting to be saved: the shell shows the "Save to bookmarks" folder picker while set. */
    var bookmarkFor by mutableStateOf<org.app.vimate.sharedlogic.library.Bookmark?>(null)

    /** Asks where to bookmark [v] (a video, or a song when [music]). */
    fun bookmark(v: Video, music: Boolean = false) {
        bookmarkFor = org.app.vimate.sharedlogic.library.Bookmark(0, null,
            if (music) org.app.vimate.sharedlogic.library.BookmarkKind.Music else org.app.vimate.sharedlogic.library.BookmarkKind.Video,
            v.title, v.url, v.thumbnail, v.channel, v.id)
    }

    /** Asks where to bookmark the web page [url]. */
    fun bookmarkSite(url: String, title: String) {
        bookmarkFor = org.app.vimate.sharedlogic.library.Bookmark(0, null, org.app.vimate.sharedlogic.library.BookmarkKind.Site, title.ifEmpty { url }, url)
    }

    /** Opens the download sheet for [video] (POC `nav.downloadFor = v`). */
    fun download(video: Video) { downloadFor = video }
}

/** Back / forward inside the in-app browser, for the title-bar arrows. */
interface WebNav {
    fun canBack(): Boolean
    fun canForward(): Boolean
    fun back()
    fun forward()
    fun reload()
}

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> { error("no AppNavigator") }
