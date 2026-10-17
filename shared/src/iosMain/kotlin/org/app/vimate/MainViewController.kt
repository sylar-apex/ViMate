package org.app.vimate

import androidx.compose.ui.window.ComposeUIViewController
import org.app.vimate.di.initApp
import org.app.vimate.shell.LaunchRequest
import org.app.vimate.shell.LaunchRequests
import platform.Foundation.NSProcessInfo

/** `--url=<link>` in the launch arguments (e.g. `simctl launch <device> <bundle> --url=...`) opens that link, as on desktop. */
fun MainViewController() = ComposeUIViewController {
    initApp()
    App(embeddedPlayer = true)
}.also {
    NSProcessInfo.processInfo.arguments.filterIsInstance<String>().firstOrNull { it.startsWith("--url=") }
        ?.let { LaunchRequests.post(LaunchRequest.OpenUrl(it.removePrefix("--url="))) }
}
