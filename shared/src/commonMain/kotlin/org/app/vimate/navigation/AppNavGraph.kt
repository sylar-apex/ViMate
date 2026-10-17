package org.app.vimate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab

/** Registers every [Routes] destination with its screen from Screens.kt. */
internal fun NavGraphBuilder.appNavGraph(tab: () -> Int) {
    composable<Routes.Tabs> { TabPage(tab()) }
    composable<Routes.Search> { SearchRoute(it.toRoute()) }
    composable<Routes.Detail> { DetailRoute(it.toRoute()) }
    composable<Routes.Shorts> { ShortsRoute(it.toRoute()) }
    composable<Routes.Sites> { SitesRoute() }
    composable<Routes.Browser> { BrowserRoute(it.toRoute()) }
    composable<Routes.YtPlaylist> { YtPlaylistRoute(it.toRoute()) }
    composable<Routes.YtSignIn> { YtSignInRoute() }
    composable<Routes.GoogleLogin> { GoogleLoginRoute() }
    composable<Routes.AccountFeed> { AccountFeedRoute(it.toRoute()) }
    composable<Routes.BatchSelect> { BatchSelectRoute(it.toRoute()) }
    composable<Routes.MusicCollection> { MusicCollectionRoute(it.toRoute()) }
    composable<Routes.MusicSearch> { MusicSearchRoute() }
    composable<Routes.History> { HistoryRoute() }
    composable<Routes.Playlists> { PlaylistsRoute() }
    composable<Routes.PlaylistDetail> { PlaylistDetailRoute(it.toRoute()) }
    composable<Routes.Subscriptions> { SubscriptionsRoute() }
    composable<Routes.JunkClean> { JunkCleanRoute() }
    composable<Routes.Boost> { BoostRoute() }
    composable<Routes.Help> { HelpRoute() }
    composable<Routes.Feedback> { FeedbackRoute() }
    composable<Routes.Settings> { SettingsRoute() }
    composable<Routes.SettingsGroup> { SettingsGroupRoute(it.toRoute()) }
    composable<Routes.Private> { PrivateRoute() }
    composable<Routes.Apps> { AppsRoute() }
}

/** POC Root: `when (nav.tab) { 0 -> Home; 1 -> Music; 2 -> Files; else -> Me }`. */
@Composable
private fun TabPage(tab: Int) = when (tab) {
    Tab.HOME -> HomeTab()
    Tab.MUSIC -> MusicTab()
    Tab.FILES -> FilesTab()
    else -> MeTab()
}
