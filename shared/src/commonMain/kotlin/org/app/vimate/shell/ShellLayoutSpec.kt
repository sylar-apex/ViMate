package org.app.vimate.shell

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Sizes of the app shell: bottom navigation, mini player, toast. */
object ShellLayoutSpec {
    val navHeight = 52.dp
    val navCorner = 26.dp
    val navSidePadding = 12.dp
    val navTopGap = 4.4.dp
    val navIconBox = 32.dp
    val navIcon = 26.dp
    val navLabel = 8.5.sp
    val navLabelLine = 11.sp
    val miniHeight = 56.dp
    val basketGap = 31.dp
    val toastAboveNav = 80.dp
    val toastBottom = 16.dp
    val toastText = 14.sp
    const val TOAST_MILLIS = 3_500L
}
