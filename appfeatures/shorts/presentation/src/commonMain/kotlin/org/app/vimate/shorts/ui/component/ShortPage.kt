package org.app.vimate.shorts.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import org.app.kmediaplayer.ui.inline.InlineVideoSurface
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.clickableNoRipple
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.jetbrains.compose.resources.stringResource

/** One Short. Overlay measured from VidMate (dp): caption at (20, 687) 254 wide; avatar 30 at (20, 738);
 *  name at x=60; +FOLLOW 92x29 at x=147; right rail at x=294, 50dp cells every 68dp from y=348. */
@Composable
fun ShortPage(
    v: Video, info: VideoInfo?, current: Boolean, subscribedIds: Set<String>,
    onTogglePlay: () -> Unit, onFollow: (Channel) -> Unit, onShare: () -> Unit, onDownload: () -> Unit,
) {
    Box(Modifier.fillMaxSize().clickableNoRipple(onTogglePlay)) {
        // Thumbnail underneath; the video surface covers it once a stream is playing.
        AsyncImage(v.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter("https://i.ytimg.com/vi/${v.id}/hqdefault.jpg"))
        if (current && info != null) InlineVideoSurface(Modifier.fillMaxSize())
        val ch = info?.video
        Column(Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 25.dp)) {
            Text(ch?.title ?: v.title, Modifier.width(254.dp), color = ViColors.white, fontSize = 15.sp, lineHeight = 20.3.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.padding(top = 10.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
                Avatar(ch?.channelAvatar ?: v.channelAvatar, 30)
                Text((ch?.channel ?: v.channel).ifEmpty { stringResource(Res.string.shorts_youtube) }, Modifier.padding(start = 10.dp).widthIn(max = 70.dp), color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.width(20.dp))
                val cid = ch?.channelId?.ifEmpty { null } ?: v.channelId.ifEmpty { "name:" + (ch?.channel ?: v.channel) }
                val followed = cid in subscribedIds
                Box(Modifier.size(92.dp, 29.dp).clip(RoundedCornerShape(2.dp)).background(if (followed) ViColors.shortsFollowing else ViColors.shortsFollow)
                    .clickable { onFollow(Channel(cid, ch?.channel ?: v.channel, ch?.channelAvatar ?: v.channelAvatar)) }, contentAlignment = Alignment.Center) {
                    Text(stringResource(if (followed) Res.string.shorts_following else Res.string.shorts_follow), color = ViColors.white, fontSize = 15.sp, lineHeight = 17.sp)
                }
            }
        }
        Column(Modifier.align(Alignment.TopStart).padding(start = 294.dp, top = 348.dp).width(50.dp)) {
            RailButton(Res.drawable.ic_s_like, v.views.substringBefore(' ').ifEmpty { stringResource(Res.string.shorts_like) }) {}
            RailButton(Res.drawable.ic_s_dislike, stringResource(Res.string.shorts_dislike)) {}
            RailButton(Res.drawable.ic_s_comment, stringResource(Res.string.shorts_comments)) {}
            RailButton(Res.drawable.ic_s_share, stringResource(Res.string.shorts_share), onShare)
            RailButton(Res.drawable.ic_d_download, stringResource(Res.string.shorts_download), onDownload)
            Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_s_more, Modifier.size(30.dp), ViColors.white, stringResource(Res.string.shorts_more)) }
        }
    }
}
