package org.app.vimate.home.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.DurationBadge
import org.app.vimate.appcore.component.MediaThumb
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.download
import org.app.vimate.appres.resources.home_add_to_batch
import org.app.vimate.appres.resources.home_remove_from_batch
import org.app.vimate.appres.resources.ic_check
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_more
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.appres.resources.ic_plus
import org.app.vimate.appres.resources.more
import org.app.vimate.appres.resources.play
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.model.Media
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Callbacks of one feed card. */
internal class FeedCardActions(
    val onOpen: (String) -> Unit,
    val onToggleBasket: (String) -> Unit,
    val onDownload: (String) -> Unit,
    val menu: CardMenuActions,
)

/** One video: thumbnail with batch toggle, quality and duration, then avatar, title, download and more. */
@Composable
internal fun FeedCard(media: Media, inBasket: Boolean, inWatchLater: Boolean, actions: FeedCardActions) {
    val scale by animateFloatAsState(if (inBasket) HomeLayoutSpec.SELECTED_SCALE else 1f)
    Box(Modifier.fillMaxWidth().background(if (inBasket) ViColors.selected else ViColors.transparent)) {
        Column(
            Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
                .padding(start = HomeLayoutSpec.cardPadding, end = HomeLayoutSpec.cardPadding, bottom = HomeLayoutSpec.cardBottom),
        ) {
            CardThumbnail(media, inBasket, actions)
            CardInfo(media, inWatchLater, actions)
        }
    }
}

@Composable
private fun CardThumbnail(media: Media, inBasket: Boolean, actions: FeedCardActions) {
    Box(
        Modifier.fillMaxWidth().aspectRatio(HomeLayoutSpec.VIDEO_RATIO).clip(RoundedCornerShape(HomeLayoutSpec.cardCorner))
            .clickable { actions.onOpen(media.id) },
    ) {
        MediaThumb(media, Modifier.fillMaxSize())
        Box(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(HomeLayoutSpec.cardShade)
                .background(Brush.verticalGradient(listOf(ViColors.transparent, ViColors.black.copy(alpha = ViAlpha.CARD_SHADE)))),
        )
        Box(
            Modifier.align(Alignment.Center).size(HomeLayoutSpec.playCircle).background(ViColors.black.copy(alpha = ViAlpha.PLAY_BUTTON), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(Res.drawable.ic_play), stringResource(Res.string.play), tint = ViColors.white, modifier = Modifier.size(HomeLayoutSpec.playIcon).offset(x = 1.dp))
        }
        Box(
            Modifier.size(HomeLayoutSpec.basketBadge)
                .background(if (inBasket) ViColors.red else ViColors.black.copy(alpha = ViAlpha.BASKET_BADGE), RoundedCornerShape(bottomEnd = HomeLayoutSpec.cardCorner))
                .clickable { actions.onToggleBasket(media.id) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(if (inBasket) Res.drawable.ic_check else Res.drawable.ic_plus),
                stringResource(if (inBasket) Res.string.home_remove_from_batch else Res.string.home_add_to_batch),
                tint = ViColors.white, modifier = Modifier.size(HomeLayoutSpec.basketIcon),
            )
        }
        Text(
            Renditions.topQuality(media),
            color = ViColors.white,
            fontSize = HomeLayoutSpec.qualityText,
            style = TextStyle(shadow = Shadow(ViColors.black.copy(alpha = ViAlpha.TEXT_SHADOW), Offset(0f, 1f), 3f)),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = HomeLayoutSpec.badgeInset, bottom = HomeLayoutSpec.badgeInset),
        )
        DurationBadge(media, Modifier.align(Alignment.BottomEnd).padding(end = HomeLayoutSpec.badgeInset, bottom = HomeLayoutSpec.badgeInset))
    }
}

@Composable
private fun CardInfo(media: Media, inWatchLater: Boolean, actions: FeedCardActions) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(Modifier.clickable { actions.onOpen(media.id) }.padding(top = 12.dp)) {
        Avatar(media.creator, HomeLayoutSpec.avatar)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                media.title, color = MaterialTheme.colorScheme.onSurface, fontSize = HomeLayoutSpec.titleText,
                lineHeight = HomeLayoutSpec.titleLine, maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            Row(Modifier.fillMaxWidth().height(HomeLayoutSpec.metaRow), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    listOfNotNull(media.creator, media.year.takeIf { it > 0 }?.toString()).joinToString(" · "),
                    color = mutedInk, fontSize = HomeLayoutSpec.metaText, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(top = 4.dp),
                )
                Box(Modifier.size(HomeLayoutSpec.metaRow).clickable { actions.onDownload(media.id) }, contentAlignment = Alignment.Center) {
                    Icon(painterResource(Res.drawable.ic_download), stringResource(Res.string.download), tint = mutedInk, modifier = Modifier.size(HomeLayoutSpec.metaRow))
                }
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(HomeLayoutSpec.metaRow).clickable { menuOpen = true }, contentAlignment = Alignment.Center) {
                    Icon(painterResource(Res.drawable.ic_more), stringResource(Res.string.more), tint = mutedInk, modifier = Modifier.size(HomeLayoutSpec.moreIcon))
                    if (menuOpen) CardMenu(media.id, inWatchLater, actions.menu) { menuOpen = false }
                }
            }
        }
    }
}
