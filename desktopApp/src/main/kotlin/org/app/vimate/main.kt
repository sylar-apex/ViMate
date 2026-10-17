package org.app.vimate

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
 * Optional launch arguments: `--size=W,H` (dp), `--open=home|music|files|me|settings|sites|search|shorts` and `--url=<link>`.
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
    Window(
        onCloseRequest = ::exitApplication,
        title = "ViMate",
        state = rememberWindowState(size = size, position = WindowPosition(Alignment.Center)),
    ) {
        App(embeddedPlayer = true)
    }
}

private fun openArg(name: String): LaunchRequest? = when (name) {
    "home" -> LaunchRequest.OpenTab(Tab.HOME)
    "music" -> LaunchRequest.OpenTab(Tab.MUSIC)
    "files" -> LaunchRequest.OpenTab(Tab.FILES)
    "me" -> LaunchRequest.OpenTab(Tab.ME)
    "settings" -> LaunchRequest.OpenPage(Routes.Settings)
    "sites" -> LaunchRequest.OpenPage(Routes.Sites)
    "search" -> LaunchRequest.OpenPage(Routes.Search())
    "shorts" -> LaunchRequest.OpenPage(Routes.Shorts())
    else -> null
}
