package org.app.vimate.appcore.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.koin.compose.koinInject

/**
 * POC `Context.toast(msg)`: shows [msg] in the shell's short toast. Provided by the app shell;
 * `val toast = LocalToast.current; toast("Saved")`.
 */
val LocalToast = staticCompositionLocalOf<(String) -> Unit> { {} }

/** POC `Context.shareText(text)`: the system share sheet with plain text. */
@Composable
fun rememberShareText(): (String) -> Unit {
    val platform = koinInject<PlatformActions>()
    return { platform.share(it, null) }
}
