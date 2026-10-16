package org.app.vimate

import androidx.compose.ui.window.ComposeUIViewController
import org.app.vimate.di.initApp

fun MainViewController() = ComposeUIViewController {
    initApp()
    App(embeddedPlayer = true)
}
