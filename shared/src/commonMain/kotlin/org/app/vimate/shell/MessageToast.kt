package org.app.vimate.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.app.vimate.appcore.message.text
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.event.AppMessage

/** The black pill toast near the bottom. Tapping it, or a few seconds, dismisses it. */
@Composable
internal fun BoxScope.MessageToast(events: AppEvents, aboveNav: Boolean) {
    var message by remember { mutableStateOf<AppMessage?>(null) }
    LaunchedEffect(events) { events.messages.collect { message = it } }
    LaunchedEffect(message) {
        if (message != null) {
            delay(ShellLayoutSpec.TOAST_MILLIS)
            message = null
        }
    }
    val current = message ?: return
    Box(
        Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
            .padding(bottom = if (aboveNav) ShellLayoutSpec.navHeight + ShellLayoutSpec.toastAboveNav else ShellLayoutSpec.toastBottom)
            .clip(ViShapes.pill).background(ViColors.black.copy(alpha = ViAlpha.TOAST)).clickable { message = null }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(current.text(), color = ViColors.white, fontSize = ShellLayoutSpec.toastText)
    }
}
