package org.app.vimate

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.app.vimate.di.initApp

private val WindowSize = DpSize(420.dp, 860.dp)

fun main() = application {
    initApp()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ViMate",
        state = rememberWindowState(size = WindowSize, position = WindowPosition(Alignment.Center)),
    ) {
        App()
    }
}
