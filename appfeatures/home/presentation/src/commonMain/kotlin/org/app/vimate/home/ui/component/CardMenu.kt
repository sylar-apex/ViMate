package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_clock
import org.app.vimate.appres.resources.ic_flag
import org.app.vimate.appres.resources.ic_share
import org.app.vimate.appres.resources.ic_unhappy
import org.app.vimate.appres.resources.menu_add_watch_later
import org.app.vimate.appres.resources.menu_not_interested
import org.app.vimate.appres.resources.menu_remove_watch_later
import org.app.vimate.appres.resources.menu_report
import org.app.vimate.appres.resources.share
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal class CardMenuActions(
    val onWatchLater: (String) -> Unit,
    val onShare: (String) -> Unit,
    val onNotInterested: (String) -> Unit,
    val onReport: (String) -> Unit,
)

/** Pinned to the right edge below the ⋮ button, or above it near the bottom of the screen. */
private class BelowAnchorEnd(private val endMarginPx: Int, private val overlapPx: Int) : PopupPositionProvider {
    override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
        val x = windowSize.width - popupContentSize.width - endMarginPx
        val below = anchorBounds.bottom - overlapPx
        val y = if (below + popupContentSize.height <= windowSize.height) below else anchorBounds.top - popupContentSize.height + overlapPx
        return IntOffset(x, y.coerceAtLeast(0))
    }
}

/** Watch Later, Share, Not interested, Report. Four fixed rows, so a plain Column. */
@Composable
internal fun CardMenu(mediaId: String, inWatchLater: Boolean, actions: CardMenuActions, onDismiss: () -> Unit) {
    val density = LocalDensity.current
    val provider = remember(density) {
        with(density) { BelowAnchorEnd(HomeLayoutSpec.menuEndMargin.roundToPx(), HomeLayoutSpec.menuOverlap.roundToPx()) }
    }
    Popup(popupPositionProvider = provider, onDismissRequest = onDismiss, properties = PopupProperties(focusable = true)) {
        Column(
            Modifier.width(HomeLayoutSpec.menuWidth).shadow(6.dp, RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.surface)
                .border(0.4.dp, ViColors.menuBorder),
        ) {
            MenuRow(Res.drawable.ic_clock, stringResource(if (inWatchLater) Res.string.menu_remove_watch_later else Res.string.menu_add_watch_later)) {
                onDismiss(); actions.onWatchLater(mediaId)
            }
            MenuRow(Res.drawable.ic_share, stringResource(Res.string.share)) { onDismiss(); actions.onShare(mediaId) }
            MenuRow(Res.drawable.ic_unhappy, stringResource(Res.string.menu_not_interested)) { onDismiss(); actions.onNotInterested(mediaId) }
            MenuRow(Res.drawable.ic_flag, stringResource(Res.string.menu_report)) { onDismiss(); actions.onReport(mediaId) }
        }
    }
}

@Composable
private fun MenuRow(icon: DrawableResource, label: String, onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    Row(Modifier.fillMaxWidth().height(HomeLayoutSpec.menuRow).clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(14.dp))
        Icon(painterResource(icon), null, tint = ink, modifier = Modifier.size(HomeLayoutSpec.menuIcon))
        Spacer(Modifier.width(11.dp))
        Text(label, color = ink, fontSize = HomeLayoutSpec.menuText, maxLines = 1)
    }
}
