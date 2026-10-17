package org.app.vimate.shell

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Sizes of the app shell: bottom bar, mini player offset, toast. */
object ShellLayoutSpec {
    val navHeight = 52.dp
    /** POC: the mini player sits 67dp above the bottom on the tabs, over the floating bar. */
    val miniAboveBar = 67.dp
    val toastAboveNav = 80.dp
    val toastBottom = 16.dp
    val toastText = 14.sp
    const val TOAST_MILLIS = 2_000L
}
