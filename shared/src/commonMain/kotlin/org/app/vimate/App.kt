package org.app.vimate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.psbapp.navigation.utils.LocalNavHostController
import com.psbapp.navigation.utils.PSBNavHost
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.music.MiniPlayer
import org.app.kmediaplayer.ui.music.NowPlayingScreen
import org.app.kmediaplayer.ui.settings.settingsVersion
import org.app.vimate.appcore.adaptive.LocalWindowClass
import org.app.vimate.appcore.adaptive.ProvideWindowClass
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.clickableNoRipple
import org.app.vimate.appcore.image.InstallImageLoader
import org.app.vimate.appcore.navigation.AppNavigator
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViTheme
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.shell_clip_detected
import org.app.vimate.appres.resources.shell_clip_dismiss
import org.app.vimate.appres.resources.shell_clip_download
import org.app.vimate.navigation.DownloadSheetSlot
import org.app.vimate.navigation.appNavGraph
import org.app.vimate.player.ui.EmbeddedPlayer
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.AppRoute
import org.app.vimate.sharedlogic.platform.HostWatchers
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.shell.BottomBar
import org.app.vimate.shell.LaunchRequest
import org.app.vimate.shell.LaunchRequests
import org.app.vimate.shell.MessageToast
import org.app.vimate.shell.ShellLayoutSpec
import org.app.vimate.shell.SideBar
import org.app.vimate.shell.ToastState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * The main window (POC MainActivity.Root): tab pages or the current sub-page, the mini player, the floating
 * bottom bar, the copied-link prompt, the download sheet and Now Playing. [embeddedPlayer] shows the video
 * player inside this window, for platforms without a separate player screen.
 */
@Composable
fun App(embeddedPlayer: Boolean = false) {
    InstallImageLoader()
    ViTheme {
        settingsVersion()
        val navController = rememberNavController()
        val nav = remember(navController) { AppNavigator(navController) }
        val toast = remember { ToastState() }
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalAppNavigator provides nav,
            LocalToast provides toast::show,
        ) {
            ProvideWindowClass {
                Root(nav, toast, embeddedPlayer) {
                    PSBNavHost(navController, startDestination = Routes.Tabs, isAnimationEnabled = false) { appNavGraph { nav.tab } }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Root(nav: AppNavigator, toast: ToastState, embeddedPlayer: Boolean, host: @Composable () -> Unit) {
    val events = koinInject<AppEvents>()
    val launcher = koinInject<PlaybackLauncher>()
    val watchers = koinInject<HostWatchers>()
    val downloads = koinInject<VidDownloads>()
    val navController = LocalNavHostController.current
    val entry by navController.currentBackStackEntryAsState()
    val dest = entry?.destination
    val onTabs = dest == null || dest.hasRoute(Routes.Tabs::class)
    val nowPlaying by launcher.nowPlaying.collectAsState()
    val clip by watchers.copiedLink.collectAsState()

    LaunchedEffect(nav) {
        LaunchRequests.requests.collect { r ->
            LaunchRequests.consume()
            when (r) {
                is LaunchRequest.OpenTab -> nav.home(r.tab)
                is LaunchRequest.OpenVideo -> {
                    val current = navController.currentBackStackEntry?.takeIf { it.destination.hasRoute(Routes.Detail::class) }
                    val same = current != null && runCatching { current.toRoute<Routes.Detail>().video.id == r.id }.getOrDefault(false)
                    if (!same) nav.go(Routes.Detail.of(Video(r.id, "")))
                }
                is LaunchRequest.OpenUrl -> nav.open(r.url)
            }
        }
    }
    LaunchedEffect(events) {
        events.routes.collect { route ->
            events.consumeRoute()
            when (route) {
                AppRoute.HOME -> nav.home(Tab.HOME)
                AppRoute.MUSIC -> nav.home(Tab.MUSIC)
                AppRoute.FILES, AppRoute.FILES_PRIVATE, AppRoute.VIDEO, AppRoute.DOWNLOADS -> nav.home(Tab.FILES)
                AppRoute.ME -> nav.home(Tab.ME)
            }
        }
    }

    val wide = LocalWindowClass.current.isWide
    val items by downloads.tasks.collectAsState()
    val activeDownloads = items.count { it.state != TaskState.Done && it.state != TaskState.Failed }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxSize()) {
            // Wide windows: permanent side bar on every page instead of the floating bottom bar.
            if (wide) SideBar(nav.tab, activeDownloads) { nav.home(it) }
            Column(Modifier.weight(1f).fillMaxHeight()) {
                Box(Modifier.weight(1f)) { host() }
                val queue by KMediaPlayer.queue.collectAsState()
                val hideMini = dest != null && (dest.hasRoute(Routes.Shorts::class) || dest.hasRoute(Routes.Detail::class))
                val barBelow = onTabs && !wide
                if (queue.isNotEmpty() && !hideMini)
                    Box(Modifier.clickableNoRipple { launcher.nowPlaying.value = true }.padding(bottom = if (barBelow) ShellLayoutSpec.miniAboveBar else 0.dp)) { MiniPlayer() }
                if (!barBelow) Spacer(Modifier.navigationBarsPadding())
            }
        }
        // Floating bar over the content, as in VidMate (content scrolls underneath).
        if (onTabs && !wide) Box(Modifier.align(Alignment.BottomCenter)) {
            BottomBar(nav.tab, activeDownloads) { nav.tab = it }
        }
        clip?.let { url ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp).padding(bottom = 90.dp),
                action = { TextButton(onClick = { watchers.consume(); nav.open(url) }) { Text(stringResource(Res.string.shell_clip_download), color = VRed) } },
                dismissAction = { IconButton(onClick = { watchers.consume() }) { Icon(Icons.Default.Close, stringResource(Res.string.shell_clip_dismiss)) } },
            ) { Text(stringResource(Res.string.shell_clip_detected, url), maxLines = 2) }
        }
        nav.downloadFor?.let { v -> DownloadSheetSlot(v) { nav.downloadFor = null } }
        if (nowPlaying) Surface(Modifier.fillMaxSize()) { NowPlayingScreen { launcher.nowPlaying.value = false } }
        MessageToast(events, toast, aboveNav = onTabs && !wide)
        if (embeddedPlayer) EmbeddedPlayer()
    }
    BackHandler(enabled = nav.canGoBack || nav.tab != Tab.HOME || nowPlaying) {
        when {
            nowPlaying -> launcher.nowPlaying.value = false
            !nav.back() -> nav.tab = Tab.HOME
        }
    }
}
