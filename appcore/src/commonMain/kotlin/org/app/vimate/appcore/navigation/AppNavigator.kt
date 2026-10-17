package org.app.vimate.appcore.navigation

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

    fun go(route: Any) { nav.navigate(route) }

    /** Pops one screen; false when already on the tabs. */
    fun back(): Boolean = if (nav.previousBackStackEntry != null) { nav.popBackStack(); true } else false

    /** Clears every sub-page and selects [tab]. */
    fun home(tab: Int) {
        nav.popBackStack(Routes.Tabs, inclusive = false)
        this.tab = tab
    }

    /** Opens a YouTube link natively, anything else in the in-app browser. */
    fun open(url: String) {
        val id = YouTube.idFromUrl(url)
        if (id != null) go(Routes.Detail.of(Video(id, "")))
        else go(Routes.Browser(if ("://" in url) url else "https://$url"))
    }

    /** Opens the download sheet for [video] (POC `nav.downloadFor = v`). */
    fun download(video: Video) { downloadFor = video }
}

val LocalAppNavigator = staticCompositionLocalOf<AppNavigator> { error("no AppNavigator") }
