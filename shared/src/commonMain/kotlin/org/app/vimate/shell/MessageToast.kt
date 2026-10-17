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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

/** The current toast: a plain string ([org.app.vimate.appcore.component.LocalToast]) or an [AppMessage]. */
@Stable
internal class ToastState {
    var plain by mutableStateOf<String?>(null)
    var message by mutableStateOf<AppMessage?>(null)
    var serial by mutableStateOf(0)

    fun show(text: String) { message = null; plain = text; serial++ }
    fun show(m: AppMessage) { plain = null; message = m; serial++ }
    fun clear() { plain = null; message = null }
}

/** The black pill toast near the bottom (POC Toast.LENGTH_SHORT). Tapping it, or a short delay, dismisses it. */
@Composable
internal fun BoxScope.MessageToast(events: AppEvents, state: ToastState, aboveNav: Boolean) {
    LaunchedEffect(events) { events.messages.collect { state.show(it) } }
    LaunchedEffect(state.serial) {
        delay(ShellLayoutSpec.TOAST_MILLIS)
        state.clear()
    }
    val text = state.plain ?: state.message?.text() ?: return
    Box(
        Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
            .padding(bottom = if (aboveNav) ShellLayoutSpec.navHeight + ShellLayoutSpec.toastAboveNav else ShellLayoutSpec.toastBottom)
            .clip(ViShapes.pill).background(ViColors.black.copy(alpha = ViAlpha.TOAST)).clickable { state.clear() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text, color = ViColors.white, fontSize = ShellLayoutSpec.toastText)
    }
}
