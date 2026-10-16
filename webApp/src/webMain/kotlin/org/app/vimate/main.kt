package org.app.vimate

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.app.vimate.di.initApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initApp()
    ComposeViewport {
        App()
    }
}
