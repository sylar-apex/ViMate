package org.app.vimate.home.ui.wide

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.adaptive.WideButton
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.openDetail
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** Featured first video of the feed: large thumbnail beside title, channel and Watch / Download buttons. */
@Composable
internal fun HeroCard(v: Video, onDownload: () -> Unit) {
    val open = openDetail()
    val h = rememberHover()
    val zoom by animateFloatAsState(if (h.hovered) 1.04f else 1f)
    val shape = RoundedCornerShape(24.dp)
    BoxWithConstraints(
        Modifier.fillMaxWidth().clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).hover(h).padding(16.dp),
    ) {
        // Side by side on desktop; stacked when the page is tablet-narrow.
        if (maxWidth >= 760.dp) Row(horizontalArrangement = Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
            HeroThumb(v, zoom, Modifier.weight(1.35f)) { open(v) }
            HeroInfo(v, Modifier.weight(1f), onDownload) { open(v) }
        } else Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            HeroThumb(v, zoom, Modifier.fillMaxWidth()) { open(v) }
            HeroInfo(v, Modifier.fillMaxWidth(), onDownload) { open(v) }
        }
    }
}

@Composable
private fun HeroThumb(v: Video, zoom: Float, modifier: Modifier, open: (Video) -> Unit) {
    Box(modifier.aspectRatio(16f / 9).clip(RoundedCornerShape(18.dp)).background(ViColors.thumbPlaceholder).clickable { open(v) }) {
        AsyncImage(v.thumbnail, null, Modifier.fillMaxSize().graphicsLayer(scaleX = zoom, scaleY = zoom), contentScale = ContentScale.Crop)
        Box(Modifier.align(Alignment.Center).size(68.dp).clip(CircleShape).background(Color.White.copy(alpha = .92f)), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_play_fill, Modifier.size(36.dp), ViColors.ink)
        }
        if (v.duration.isNotEmpty()) Text(v.duration, Modifier.align(Alignment.BottomEnd).padding(14.dp).clip(RoundedCornerShape(6.dp)).background(ViColors.shade60)
            .padding(horizontal = 8.dp, vertical = 3.dp), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HeroInfo(v: Video, modifier: Modifier, onDownload: () -> Unit, open: (Video) -> Unit) {
    Column(modifier.padding(end = 12.dp)) {
        Row(Modifier.clip(CircleShape).background(ViColors.tintRed).padding(horizontal = 12.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(VRed))
            Text(stringResource(Res.string.wide_hero_badge), Modifier.padding(start = 8.dp), color = VRed, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
        }
        Text(v.title, Modifier.padding(top = 16.dp), color = ViColors.ink, fontSize = 26.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold,
            maxLines = 3, overflow = TextOverflow.Ellipsis)
        Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(v.channelAvatar, 40)
            Column(Modifier.padding(start = 12.dp)) {
                Text(v.channel, color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(listOf(v.views, v.published).filter { it.isNotEmpty() }.joinToString("  •  "), color = ViColors.sectionLabel, fontSize = 13.sp, maxLines = 1)
            }
        }
        Row(Modifier.padding(top = 26.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WideButton(stringResource(Res.string.wide_hero_watch), icon = Res.drawable.ic_play_fill) { open(v) }
            WideButton(stringResource(Res.string.common_download), filled = false, icon = Res.drawable.ic_download, onClick = onDownload)
        }
    }
}
