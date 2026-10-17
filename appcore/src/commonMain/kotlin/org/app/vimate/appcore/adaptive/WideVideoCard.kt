package org.app.vimate.appcore.adaptive

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.openDetail
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_download
import org.app.vimate.appres.resources.common_more
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_more_vert
import org.app.vimate.appres.resources.ic_play_fill
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/**
 * Feed card for tablet / desktop grids: rounded thumbnail that lifts and zooms on hover, play button and
 * download / more actions fading in on hover, then avatar, two-line title and channel · views · age.
 */
@Composable
fun WideVideoCard(v: Video, onDownload: () -> Unit, onMenu: (() -> Unit)? = null, onOpen: (Video) -> Unit = openDetail()) {
    val nav = LocalAppNavigator.current
    val h = rememberHover()
    val lift by animateDpAsState(if (h.hovered) 14.dp else 0.dp)
    val zoom by animateFloatAsState(if (h.hovered) 1.05f else 1f)
    val shape = RoundedCornerShape(16.dp)
    Column(Modifier.fillMaxWidth().hover(h).clickable(interactionSource = h.source, indication = null) { onOpen(v) }) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(16f / 9)
                .shadow(lift, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
                .clip(shape).background(ViColors.thumbPlaceholder),
        ) {
            AsyncImage(v.thumbnail, null, Modifier.fillMaxSize().graphicsLayer(scaleX = zoom, scaleY = zoom), contentScale = ContentScale.Crop)
            androidx.compose.animation.AnimatedVisibility(h.hovered, Modifier.fillMaxSize(), enter = fadeIn(), exit = fadeOut()) {
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(ViColors.shade40, Color.Transparent, ViColors.shade60)))) {
                    Box(Modifier.align(Alignment.Center).size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = .92f)), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_play_fill, Modifier.size(30.dp), ViColors.ink)
                    }
                    Row(Modifier.align(Alignment.TopEnd).padding(10.dp)) {
                        GlassButton(Res.drawable.ic_download, stringResource(Res.string.common_download), onDownload)
                        GlassButton(Res.drawable.ic_more_vert, stringResource(Res.string.common_more)) { onMenu?.invoke() ?: nav.download(v) }
                    }
                }
            }
            if (v.duration.isNotEmpty()) Text(
                v.duration, Modifier.align(Alignment.BottomEnd).padding(10.dp).clip(RoundedCornerShape(6.dp))
                    .background(ViColors.shade60).padding(horizontal = 6.dp, vertical = 2.dp),
                color = Color.White, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.SemiBold,
            )
        }
        Row(Modifier.padding(top = 12.dp, end = 4.dp)) {
            Avatar(v.channelAvatar, 36)
            Column(Modifier.padding(start = 12.dp)) {
                Text(v.title, color = ViColors.ink, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(v.channel, Modifier.padding(top = 4.dp), color = ViColors.metaGrey, fontSize = 13.sp, lineHeight = 17.sp,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                val meta = listOf(v.views, v.published).filter { it.isNotEmpty() }.joinToString("  •  ")
                if (meta.isNotEmpty()) Text(meta, color = ViColors.metaGrey, fontSize = 13.sp, lineHeight = 17.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun GlassButton(icon: org.jetbrains.compose.resources.DrawableResource, label: String, onClick: () -> Unit) {
    Box(
        Modifier.padding(start = 6.dp).size(34.dp).clip(CircleShape).background(ViColors.shade60).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Ic(icon, Modifier.size(18.dp), Color.White, label) }
}
