package org.app.vimate.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.adaptive.readableWidth
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.sharedlogic.yt.Video

/*
 * One composable per destination of the main window (POC MainActivity.RouteScreen + tab pages).
 */

// Tab pages (Routes.Tabs, POC nav.tab 0..3)
@Composable internal fun HomeTab() = org.app.vimate.home.ui.HomeScreen()
@Composable internal fun MusicTab() = org.app.vimate.music.ui.MusicScreen()
@Composable internal fun FilesTab() = org.app.vimate.files.ui.FilesScreen()
@Composable internal fun MeTab() = org.app.vimate.me.ui.MeScreen()

// Sub-pages
@Composable internal fun SearchRoute(r: Routes.Search) = org.app.vimate.search.ui.SearchScreen(r.query)
@Composable internal fun DetailRoute(r: Routes.Detail) = Readable(DETAIL_WIDTH) { org.app.vimate.detail.ui.DetailScreen(r.video) }
@Composable internal fun ShortsRoute(r: Routes.Shorts) = org.app.vimate.shorts.ui.ShortsScreen(r.start)
@Composable internal fun SitesRoute() = org.app.vimate.sites.ui.SitesScreen()
@Composable internal fun BrowserRoute(r: Routes.Browser) = org.app.vimate.sites.ui.BrowserScreen(r.url)
// Full width: the playlist screen lays itself out for wide windows.
@Composable internal fun YtPlaylistRoute(r: Routes.YtPlaylist) = org.app.vimate.music.ui.YtPlaylistScreen(r.listId)
@Composable internal fun YtSignInRoute() = Readable { org.app.vimate.me.ui.SubscriptionsScreen(forceSignIn = true) }
@Composable internal fun GoogleLoginRoute() = Readable { org.app.vimate.me.ui.GoogleLoginScreen() }
@Composable internal fun AccountFeedRoute(r: Routes.AccountFeed) = Readable { org.app.vimate.me.ui.AccountFeedScreen(r.title, r.browseId) }
@Composable internal fun BatchSelectRoute(r: Routes.BatchSelect) = Readable { org.app.vimate.batch.ui.BatchSelectScreen(r.videos) }
@Composable internal fun MusicCollectionRoute(r: Routes.MusicCollection) = org.app.vimate.music.ui.MusicCollectionScreen(r)
@Composable internal fun MusicSearchRoute() = Readable { org.app.vimate.music.ui.MusicSearchScreen() }
@Composable internal fun HistoryRoute() = Readable { org.app.vimate.me.ui.HistoryScreen() }
@Composable internal fun PlaylistsRoute() = Readable { org.app.vimate.me.ui.PlaylistsScreen() }
@Composable internal fun PlaylistDetailRoute(r: Routes.PlaylistDetail) = Readable { org.app.vimate.me.ui.PlaylistDetailScreen(r.id) }
@Composable internal fun SubscriptionsRoute() = Readable { org.app.vimate.me.ui.SubscriptionsScreen() }
@Composable internal fun JunkCleanRoute() = Readable { org.app.vimate.me.ui.JunkCleanScreen() }
@Composable internal fun BoostRoute() = Readable { org.app.vimate.me.ui.BoostScreen() }
@Composable internal fun HelpRoute() = Readable { org.app.vimate.me.ui.HelpScreen() }
@Composable internal fun FeedbackRoute() = Readable { org.app.vimate.me.ui.FeedbackScreen() }
@Composable internal fun SettingsRoute() = org.app.vimate.settings.ui.SettingsScreen()
@Composable internal fun SettingsGroupRoute(r: Routes.SettingsGroup) = org.app.vimate.settings.ui.SettingsGroupScreen(r.group)
@Composable internal fun PrivateRoute() = Readable { org.app.vimate.files.ui.PrivateScreen() }
@Composable internal fun AppsRoute() = Readable { org.app.vimate.me.ui.AppsScreen() }

/** The download sheet over any screen (POC `DownloadSheet(v) { nav.downloadFor = null }`). */
@Composable
internal fun DownloadSheetSlot(video: Video, onDismiss: () -> Unit) = org.app.vimate.downloads.ui.DownloadSheet(video, onDismiss)

private val DETAIL_WIDTH = 1280.dp

/** Phone-style pages stay phone-width and centred on tablets and desktop; the feed screens use grids instead. */
@Composable
private fun Readable(max: Dp = 720.dp, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(ViColors.wideCanvas), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.readableWidth(max).fillMaxHeight()) { content() }
    }
}
