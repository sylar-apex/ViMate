package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_download
import org.app.vimate.appres.resources.common_hot_videos
import org.app.vimate.appres.resources.common_more
import org.app.vimate.appres.resources.common_save
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_more_vert
import org.app.vimate.appres.resources.ic_play_fill
import org.app.vimate.appres.resources.ic_plus
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** Default "open this video" action: push [Routes.Detail] (POC `nav.go(Route.Detail(v))`). */
@Composable
fun openDetail(): (Video) -> Unit {
    val nav = LocalAppNavigator.current
    return { nav.go(Routes.Detail.of(it)) }
}

/**
 * Home feed card, measured from VidMate (dp): 12 side margin, 16:9 thumb (189 tall on 360 width) with
 * 28dp "+" badge, bottom 60dp shade, views 10dp from left, duration pill 10dp from right/bottom;
 * then 34dp avatar 12dp below, 16sp title from x=46, meta 10dp below with 32dp download + menu at right.
 * [onMenu] null opens the download sheet, as in the POC.
 */
@Composable
fun VideoCard(
    v: Video,
    onDownload: () -> Unit,
    onMenu: (() -> Unit)? = null,
    aspect: Float = 16f / 9,
    onOpen: (Video) -> Unit = openDetail(),
) {
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxWidth().clickable { onOpen(v) }.padding(horizontal = 12.dp)) {
        Box(Modifier.fillMaxWidth().aspectRatio(aspect).clip(RoundedCornerShape(12.dp)).background(ViColors.thumbPlaceholder)) {
            AsyncImage(v.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, ViColors.shade60))))
            // Centre play button (44dp translucent circle), as on VidMate cards.
            Box(Modifier.align(Alignment.Center).size(44.dp).clip(CircleShape).background(ViColors.shade40), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_play_fill, Modifier.size(30.dp), Color.White)
            }
            Box(Modifier.size(28.dp).background(ViColors.shade40, RoundedCornerShape(bottomEnd = 10.dp)), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_plus, Modifier.size(22.dp), Color.White, stringResource(Res.string.common_save))
            }
            if (v.views.isNotEmpty()) Text(v.views, color = Color.White, fontSize = 13.5.sp, lineHeight = 14.7.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 10.dp, bottom = 10.dp))
            if (v.duration.isNotEmpty()) Box(Modifier.align(Alignment.BottomEnd).padding(end = 10.dp, bottom = 10.dp).height(16.dp)
                .background(ViColors.shade60, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
                Text(v.duration, color = Color.White, fontSize = 12.sp, lineHeight = 14.sp)
            }
        }
        Row(Modifier.padding(top = 12.dp)) {
            Avatar(v.channelAvatar, 34)
            Column(Modifier.padding(start = 12.dp)) {
                Text(v.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 18.5.sp, color = ViColors.ink,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(listOf(v.channel, v.published).filter { it.isNotEmpty() }.joinToString(" · "), Modifier.weight(1f).offset(y = 3.dp),
                        color = ViColors.metaGrey, fontSize = 13.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Box(Modifier.size(32.dp).clickable(onClick = onDownload), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_download, Modifier.size(23.5.dp), ViColors.metaGrey, stringResource(Res.string.common_download))
                    }
                    Box(Modifier.padding(start = 4.dp).size(32.dp).clickable { onMenu?.invoke() ?: nav.download(v) }, contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_more_vert, Modifier.size(20.dp), ViColors.metaGrey, stringResource(Res.string.common_more))
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

/** Compact row: small thumbnail left, text right. Used for related videos, history, playlists. */
@Composable
fun VideoRow(v: Video, onClick: () -> Unit, trailing: @Composable () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Thumb(v.thumbnail, Modifier.width(150.dp).aspectRatio(16f / 9), corner = 10, duration = v.duration)
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(v.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium, fontSize = 13.5.sp, lineHeight = 18.sp, color = ViColors.ink)
            Text(listOf(v.channel, v.views).filter { it.isNotEmpty() }.joinToString(" · "), Modifier.padding(top = 4.dp), fontSize = 12.sp,
                color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        trailing()
    }
}

/**
 * VidMate "Hot videos" block (measured): header at x=15dp, 35dp tall; two 156dp columns 18dp apart,
 * rows 142.7dp; 156x87.7dp image, duration pill 7dp/6dp from the corner; 2-line bold title 122dp wide
 * 4dp under the image; 24dp download icon at x=132dp in the card.
 */
fun LazyListScope.hotVideos(videos: List<Video>, onDownload: (Video) -> Unit, onOpen: ((Video) -> Unit)? = null) {
    if (videos.isEmpty()) return
    item(key = "hot_header") {
        Box(Modifier.fillMaxWidth().height(35.dp).padding(start = 15.dp), contentAlignment = Alignment.CenterStart) {
            Text(stringResource(Res.string.common_hot_videos), fontWeight = FontWeight.Bold, fontSize = 15.2.sp, lineHeight = 21.sp, color = ViColors.ink)
        }
    }
    val rows = videos.chunked(2)
    items(rows.size, key = { "hot_$it" }) { r ->
        val open = onOpen ?: openDetail()
        Row(Modifier.padding(start = 15.dp).height(142.7.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            rows[r].forEach { v -> GridCard(v, { onDownload(v) }, open) }
        }
    }
}

@Composable
fun GridCard(v: Video, onDownload: () -> Unit, onOpen: (Video) -> Unit = openDetail()) {
    Column(Modifier.width(156.dp).clickable { onOpen(v) }) {
        Box(Modifier.size(156.dp, 87.7.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.thumbPlaceholder)) {
            AsyncImage(v.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            if (v.duration.isNotEmpty()) Box(Modifier.align(Alignment.BottomEnd).padding(end = 7.dp, bottom = 6.dp).height(13.7.dp)
                .background(ViColors.shade60, RoundedCornerShape(3.dp)).padding(horizontal = 3.dp), contentAlignment = Alignment.Center) {
                Text(v.duration, color = Color.White, fontSize = 11.sp, lineHeight = 12.sp)
            }
        }
        Row(Modifier.padding(top = 4.dp)) {
            Text(v.title, Modifier.width(122.dp), fontWeight = FontWeight.Bold, fontSize = 13.5.sp, lineHeight = 17.5.sp, color = ViColors.ink,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Box(Modifier.padding(start = 10.dp, top = 3.dp).size(24.dp).clickable(onClick = onDownload), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_download, Modifier.size(22.dp), ViColors.metaGrey, stringResource(Res.string.common_download))
            }
        }
    }
}
