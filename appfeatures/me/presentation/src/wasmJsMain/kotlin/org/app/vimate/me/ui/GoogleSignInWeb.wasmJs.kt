package org.app.vimate.me.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.me_login_external
import org.jetbrains.compose.resources.stringResource

/** No embedded browser here: opens Google sign-in in the system browser. */
@Composable
actual fun GoogleSignInWeb(url: String, modifier: Modifier, onSignedIn: () -> Unit) {
    val uri = LocalUriHandler.current
    LaunchedEffect(url) { runCatching { uri.openUri(url) } }
    Box(modifier.padding(24.dp), contentAlignment = Alignment.Center) { Text(stringResource(Res.string.me_login_external), color = ViColors.muted) }
}
