package org.app.vimate.appcore.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/** True when the app draws into the window title bar (macOS transparent title bar), so the shell puts its top bar there. */
val LocalInTitleBar = staticCompositionLocalOf { false }

/** Wraps title-bar content so dragging it moves the window (desktop); plain content elsewhere. */
val LocalTitleBarArea = staticCompositionLocalOf<@Composable (@Composable () -> Unit) -> Unit> { { it() } }
