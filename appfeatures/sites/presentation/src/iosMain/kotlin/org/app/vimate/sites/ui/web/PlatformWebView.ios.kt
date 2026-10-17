package org.app.vimate.sites.ui.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.sites_external_note
import org.app.vimate.appres.resources.sites_open_again
import org.jetbrains.compose.resources.stringResource

actual val hasInAppWebView: Boolean = false

@Composable
actual fun PlatformWebView(startUrl: String, controller: BrowserController, callbacks: WebCallbacks, modifier: Modifier, sniff: Boolean, userAgent: String?) {
    val uri = LocalUriHandler.current
    fun open() { runCatching { uri.openUri(startUrl) } }
    LaunchedEffect(startUrl) { open(); callbacks.onPageStarted(startUrl) }
    Column(modifier.background(ViColors.surface).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(Res.string.sites_external_note), color = ViColors.metaGrey, fontSize = 14.sp, textAlign = TextAlign.Center)
        TextButton(onClick = ::open) { Text(stringResource(Res.string.sites_open_again), color = VRed) }
    }
}
