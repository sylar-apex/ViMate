package org.app.vimate.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.app.vimate.appcore.component.BackBar
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.sharedlogic.yt.Video

/*
 * One composable per destination of the main window (POC MainActivity.RouteScreen + tab pages).
 * Every body is a placeholder; replace each with the feature screen.
 */

// Tab pages (Routes.Tabs, POC nav.tab 0..3)
@Composable internal fun HomeTab() = org.app.vimate.home.ui.HomeScreen()
@Composable internal fun MusicTab() = org.app.vimate.music.ui.MusicScreen()
@Composable internal fun FilesTab() = org.app.vimate.files.ui.FilesScreen()
@Composable internal fun MeTab() = org.app.vimate.me.ui.MeScreen()

// Sub-pages
@Composable internal fun SearchRoute(r: Routes.Search) = org.app.vimate.search.ui.SearchScreen(r.query)
@Composable internal fun DetailRoute(r: Routes.Detail) = org.app.vimate.detail.ui.DetailScreen(r.video)
@Composable internal fun ShortsRoute(r: Routes.Shorts) = org.app.vimate.shorts.ui.ShortsScreen(r.start)
@Composable internal fun SitesRoute() = org.app.vimate.sites.ui.SitesScreen()
@Composable internal fun BrowserRoute(r: Routes.Browser) = org.app.vimate.sites.ui.BrowserScreen(r.url)
@Composable internal fun YtPlaylistRoute(r: Routes.YtPlaylist) = org.app.vimate.music.ui.YtPlaylistScreen(r.listId)
@Composable internal fun YtSignInRoute() = org.app.vimate.me.ui.SubscriptionsScreen(forceSignIn = true)
@Composable internal fun GoogleLoginRoute() = org.app.vimate.me.ui.GoogleLoginScreen()
@Composable internal fun AccountFeedRoute(r: Routes.AccountFeed) = org.app.vimate.me.ui.AccountFeedScreen(r.title, r.browseId)
@Composable internal fun BatchSelectRoute(r: Routes.BatchSelect) = org.app.vimate.batch.ui.BatchSelectScreen(r.videos)
@Composable internal fun MusicCollectionRoute(r: Routes.MusicCollection) = org.app.vimate.music.ui.MusicCollectionScreen(r)
@Composable internal fun MusicSearchRoute() = org.app.vimate.music.ui.MusicSearchScreen()
@Composable internal fun HistoryRoute() = org.app.vimate.me.ui.HistoryScreen()
@Composable internal fun PlaylistsRoute() = org.app.vimate.me.ui.PlaylistsScreen()
@Composable internal fun PlaylistDetailRoute(r: Routes.PlaylistDetail) = org.app.vimate.me.ui.PlaylistDetailScreen(r.id)
@Composable internal fun SubscriptionsRoute() = org.app.vimate.me.ui.SubscriptionsScreen()
@Composable internal fun JunkCleanRoute() = org.app.vimate.me.ui.JunkCleanScreen()
@Composable internal fun BoostRoute() = org.app.vimate.me.ui.BoostScreen()
@Composable internal fun HelpRoute() = org.app.vimate.me.ui.HelpScreen()
@Composable internal fun FeedbackRoute() = org.app.vimate.me.ui.FeedbackScreen()
@Composable internal fun SettingsRoute() = org.app.vimate.settings.ui.SettingsScreen()
@Composable internal fun SettingsGroupRoute(r: Routes.SettingsGroup) = org.app.vimate.settings.ui.SettingsGroupScreen(r.group)
@Composable internal fun PrivateRoute() = org.app.vimate.files.ui.PrivateScreen()
@Composable internal fun AppsRoute() = org.app.vimate.me.ui.AppsScreen()

/** The download sheet over any screen (POC `DownloadSheet(v) { nav.downloadFor = null }`). */
@Composable
internal fun DownloadSheetSlot(video: Video, onDismiss: () -> Unit) = org.app.vimate.downloads.ui.DownloadSheet(video, onDismiss)

@Composable
private fun TodoScreen(name: String, back: Boolean = true) {
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize()) {
        if (back) BackBar(name, onBack = { nav.back() }) else Box(Modifier.statusBarsPadding())
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("TODO screen: $name") }
    }
}
