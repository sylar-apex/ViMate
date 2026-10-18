package org.app.vimate

import org.app.vimate.appcore.adaptive.LocalTitleBarArea
import org.app.vimate.appcore.adaptive.LocalInTitleBar
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.CompositionLocalProvider
import org.app.vimate.appcore.theme.ViTheme
import org.app.vimate.appcore.theme.ViColors
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import org.koin.mp.KoinPlatform
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.platform.HostSignals
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kdownloader.model.DownloadEvent
import org.app.kdownloader.KDownloader
import kotlinx.coroutines.flow.filterIsInstance
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.kmediaplayer.desktop.DesktopPlayback
import org.app.vimate.di.initApp
import org.app.vimate.sites.ui.web.DesktopWebRuntime
import org.app.vimate.shell.LaunchRequest
import org.app.vimate.shell.LaunchRequests

private val WindowSize = DpSize(1280.dp, 820.dp)

/**
 * Optional launch arguments: `--size=W,H` (dp), `--open=home|music|files|me|settings|settings-others|sites|search|shorts` and `--url=<link>`.
 * Useful for checking the phone, tablet and desktop layouts of a page directly.
 */
fun main(args: Array<String>) {
    DesktopWebRuntime.start()
    // Load libVLC off the UI thread so the window never waits for it.
    Thread({ DesktopPlayback.engine }, "libvlc-init").apply { isDaemon = true }.start()
    runApp(args)
}

private fun runApp(args: Array<String>) = application {
    initApp()
    val opt = args.mapNotNull { a -> a.removePrefix("--").split("=", limit = 2).takeIf { it.size == 2 }?.let { it[0] to it[1] } }.toMap()
    val size = opt["size"]?.split(",")?.mapNotNull { it.toIntOrNull() }?.takeIf { it.size == 2 }?.let { DpSize(it[0].dp, it[1].dp) } ?: WindowSize
    opt["open"]?.let { openArg(it) }?.let(LaunchRequests::post)
    opt["url"]?.let { LaunchRequests.post(LaunchRequest.OpenUrl(it)) }
    LaunchedEffect(Unit) { notifyCompletedDownloads() }
    Window(
        onCloseRequest = ::exitApplication,
        title = "ViMate",
        state = rememberWindowState(size = size, position = WindowPosition(Alignment.Center)),
    ) {
        // Copied Link Detection reads the clipboard whenever the window comes back to the front (Android: on resume).
        DisposableEffect(window) {
            val signals = KoinPlatform.getKoin().get<HostSignals>()
            val focus = object : java.awt.event.WindowAdapter() {
                override fun windowGainedFocus(e: java.awt.event.WindowEvent) = signals.onResume()
            }
            window.addWindowFocusListener(focus)
            signals.onResume()
            onDispose { window.removeWindowFocusListener(focus) }
        }
        // macOS: transparent title bar, so the window top takes the app's theme instead of the system grey bar.
        val mac = remember { System.getProperty("os.name").startsWith("Mac") }
        if (mac) LaunchedEffect(window) {
            window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            window.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
        }
        CompositionLocalProvider(
            LocalInTitleBar provides mac,
            org.app.kmediaplayer.ui.music.LocalWindowTopInset provides if (mac) 28.dp else 0.dp,
            LocalTitleBarArea provides { content -> WindowDraggableArea { content() } },
        ) { App(embeddedPlayer = true) }
    }
}

private fun openArg(name: String): LaunchRequest? = when (name) {
    "home" -> LaunchRequest.OpenTab(Tab.HOME)
    "music" -> LaunchRequest.OpenTab(Tab.MUSIC)
    "files" -> LaunchRequest.OpenTab(Tab.FILES)
    "me" -> LaunchRequest.OpenTab(Tab.ME)
    "settings" -> LaunchRequest.OpenPage(Routes.Settings)
    "settings-others" -> LaunchRequest.OpenPage(Routes.SettingsGroup("Others"))
    "sites" -> LaunchRequest.OpenPage(Routes.Sites)
    "search" -> LaunchRequest.OpenPage(Routes.Search())
    "shorts" -> LaunchRequest.OpenPage(Routes.Shorts())
    else -> null
}

/** Settings › Download complete notification: a system notification per finished file (Android uses its download service). */
private suspend fun notifyCompletedDownloads() {
    val koin = KoinPlatform.getKoin()
    val settings = koin.get<ObservableSettings>()
    val platform = koin.get<PlatformActions>()
    koin.get<KDownloader>().events.filterIsInstance<DownloadEvent.Completed>().collect { e ->
        if (settings[AppKeys.completeNotify]) platform.notify("Download complete", e.task.fileName)
    }
}


