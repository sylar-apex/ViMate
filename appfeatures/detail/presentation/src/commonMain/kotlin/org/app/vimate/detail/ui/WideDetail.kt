package org.app.vimate.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.component.openDetail
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_autoplay
import org.app.vimate.appres.resources.detail_comments
import org.app.vimate.appres.resources.detail_download
import org.app.vimate.appres.resources.detail_play_audio
import org.app.vimate.appres.resources.detail_popup
import org.app.vimate.appres.resources.detail_related_videos
import org.app.vimate.appres.resources.detail_show_less
import org.app.vimate.appres.resources.detail_show_more
import org.app.vimate.appres.resources.ic_chevron_down
import org.app.vimate.appres.resources.ic_d_audio
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_d_popup
import org.app.vimate.appres.resources.ic_more_vert
import org.app.vimate.appres.resources.ic_thumb_down
import org.app.vimate.appres.resources.ic_thumb_up
import org.app.vimate.detail.ui.component.rememberCommentsPager
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.app.vimate.sharedlogic.yt.Comment
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

private val CardShape = RoundedCornerShape(14.dp)
private val PillShape = RoundedCornerShape(50)
/** Comments shown inline under the video before the thread stops paging. */
private const val INLINE_COMMENTS = 40

/** What the wide video page can do; [DetailScreen] wires these to the same handlers as the phone layout. */
internal class WideActions(
    val onAudio: () -> Unit,
    val onDownload: () -> Unit,
    val onRelatedDownload: (Video) -> Unit,
    /** Null where the platform has no popup window. */
    val onPopup: (() -> Unit)?,
)

/**
 * Video page on desktop and tablets, YouTube-style: player, title, one row with the channel, likes,
 * Play Audio and Download, description and the comments thread in the main column; related videos with the autoplay
 * switch in a side column. Both columns scroll on their own.
 */
@Composable
internal fun WideDetail(
    player: @Composable () -> Unit,
    v: Video,
    likes: String,
    description: String,
    commentCount: String,
    commentsToken: String?,
    related: List<Video>,
    loading: Boolean,
    audioMode: Boolean,
    autoNext: Boolean,
    onAutoNext: (Boolean) -> Unit,
    actions: WideActions,
) {
    val open = openDetail()
    // Each video starts both columns at the top.
    val mainList = remember(v.id) { LazyListState() }
    val sideList = remember(v.id, related.firstOrNull()?.id) { LazyListState() }
    Row(Modifier.fillMaxSize().background(ViColors.wideCanvas).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LazyColumn(Modifier.weight(1f).fillMaxHeight(), state = mainList, contentPadding = PaddingValues(bottom = 24.dp)) {
            item(key = "player") { Box(Modifier.fillMaxWidth().clip(CardShape)) { player() } }
            item(key = "title") {
                Text(v.title, Modifier.padding(top = 12.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp,
                    color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            item(key = "channel") { ChannelRow(v, likes, audioMode, actions) }
            if (description.isNotBlank()) item(key = "description") { Description(description) }
            comments(commentCount, commentsToken)
        }
        Column(Modifier.width(DetailLayoutSpec.SIDE_WIDTH).fillMaxHeight().clip(CardShape).background(ViColors.surface)) {
            Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.detail_related_videos), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
                Text(stringResource(Res.string.detail_autoplay), Modifier.padding(end = 6.dp), fontSize = 12.sp, color = VRed)
                Toggle(autoNext, onAutoNext)
            }
            LazyColumn(Modifier.fillMaxSize(), state = sideList, contentPadding = PaddingValues(bottom = 12.dp)) {
                items(related, key = { "r" + it.id }) { r -> RelatedRow(r, { open(r) }, { actions.onRelatedDownload(r) }) }
                item(key = "loading") { if (loading) Loading() }
            }
        }
    }
}

/** One row: avatar, channel and stats, the like/dislike pill, then Play Audio, Popup (Android) and Download. */
@Composable
private fun ChannelRow(v: Video, likes: String, audioMode: Boolean, a: WideActions) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Avatar(v.channelAvatar, 38)
        Column(Modifier.weight(1f).padding(start = 2.dp, end = 4.dp)) {
            Text(v.channel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val meta = listOf(v.views, v.published).filter { it.isNotEmpty() }.joinToString(" · ")
            if (meta.isNotEmpty()) Text(meta, fontSize = 12.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(Modifier.height(34.dp).clip(PillShape).background(ViColors.detailLikePill).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Ic(Res.drawable.ic_thumb_up, Modifier.size(17.dp), ViColors.ink)
            if (likes.isNotEmpty()) Text(likes, Modifier.padding(start = 6.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ViColors.ink, maxLines = 1)
            Box(Modifier.padding(horizontal = 10.dp).size(1.dp, 16.dp).background(ViColors.detailLikeDivider))
            Ic(Res.drawable.ic_thumb_down, Modifier.size(17.dp), ViColors.ink)
        }
        Pill(Res.drawable.ic_d_audio, stringResource(Res.string.detail_play_audio), accent = audioMode, onClick = a.onAudio)
        a.onPopup?.let { Pill(Res.drawable.ic_d_popup, stringResource(Res.string.detail_popup), onClick = it) }
        Pill(Res.drawable.ic_d_download, stringResource(Res.string.detail_download), accent = true, bg = ViColors.detailDownloadPill, onClick = a.onDownload)
    }
}

@Composable
private fun Pill(icon: DrawableResource, label: String, accent: Boolean = false, bg: Color = ViColors.detailLikePill, onClick: () -> Unit) {
    val fg = if (accent) VRed else ViColors.ink
    Row(Modifier.height(34.dp).clip(PillShape).background(bg).clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Ic(icon, Modifier.size(17.dp), fg)
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = fg, maxLines = 1)
    }
}

@Composable
private fun Description(text: String) {
    var expanded by remember(text) { mutableStateOf(false) }
    val plain = remember(text) { text.htmlToPlain() }
    Column(Modifier.padding(top = 10.dp).fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(ViColors.detailLikePill)
        .clickable { expanded = !expanded }.padding(horizontal = 12.dp, vertical = 10.dp)) {
        Text(plain, fontSize = 13.sp, lineHeight = 18.sp, color = ViColors.ink, maxLines = if (expanded) Int.MAX_VALUE else 3, overflow = TextOverflow.Ellipsis)
        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(if (expanded) Res.string.detail_show_less else Res.string.detail_show_more), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
            Ic(Res.drawable.ic_chevron_down, Modifier.padding(start = 4.dp).size(16.dp), ViColors.ink)
        }
    }
}

/** Comments header and the thread inline. */
private fun LazyListScope.comments(count: String, token: String?) {
    item(key = "comments_header") {
        Column(Modifier.padding(top = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.detail_comments), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
                if (count.isNotEmpty()) Text(count, Modifier.padding(start = 6.dp), fontSize = 13.sp, color = ViColors.metaGrey)
            }
        }
    }
    if (token != null) item(key = "comments_thread_$token") { CommentThread(token) }
}

@Composable
private fun CommentThread(token: String) {
    val pager = rememberCommentsPager(token)
    val list by pager.comments.collectAsState()
    val loading by pager.loading.collectAsState()
    Column {
        list.forEach { CommentRow(it) }
        if (loading) Loading()
        // Keep paging until a screenful of comments is in; the full thread is one scroll away in the comments panel.
        if (!loading && list.isNotEmpty() && list.size < INLINE_COMMENTS) LaunchedEffect(list.size) { pager.more() }
    }
}

@Composable
private fun CommentRow(c: Comment) {
    Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Avatar(c.avatar, 32)
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(c.author, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = ViColors.ink, maxLines = 1)
                if (c.published.isNotEmpty()) Text(" · ${c.published}", fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1)
            }
            Text(c.text, Modifier.padding(top = 2.dp), fontSize = 13.sp, lineHeight = 18.sp, color = ViColors.ink)
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_thumb_up, Modifier.size(14.dp), ViColors.metaGrey)
                if (c.likes.isNotEmpty()) Text(c.likes, Modifier.padding(start = 4.dp), fontSize = 12.sp, color = ViColors.metaGrey)
                Ic(Res.drawable.ic_thumb_down, Modifier.padding(start = 14.dp).size(14.dp), ViColors.metaGrey)
            }
        }
    }
}

/** Related video: 160dp thumbnail, two-line title, channel, views · age and a download button. */
@Composable
private fun RelatedRow(v: Video, onClick: () -> Unit, onDownload: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 5.dp)) {
        Thumb(v.thumbnail, Modifier.width(136.dp).aspectRatio(16f / 9), corner = 8, duration = v.duration)
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text(v.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 17.sp, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (v.channel.isNotEmpty()) Text(v.channel, Modifier.padding(top = 2.dp), fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val meta = listOf(v.views, v.published).filter { it.isNotEmpty() }.joinToString(" · ")
            if (meta.isNotEmpty()) Text(meta, fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(Modifier.size(24.dp).clip(CircleShape).clickable(onClick = onDownload), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_more_vert, Modifier.size(16.dp), ViColors.ink)
        }
    }
}

@Composable
private fun Toggle(on: Boolean, onChange: (Boolean) -> Unit) {
    Box(Modifier.size(34.dp, 19.dp).clip(CircleShape).background(if (on) VRed else ViColors.detailRule).clickable { onChange(!on) }.padding(2.5.dp)) {
        Box(Modifier.align(if (on) Alignment.CenterEnd else Alignment.CenterStart).size(14.dp).clip(CircleShape).background(Color.White))
    }
}

/** YouTube descriptions arrive with `<br>` line breaks and HTML entities. */
private fun String.htmlToPlain(): String =
    replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n").replace(Regex("<[^>]+>"), "")
        .replace("&apos;", "'").replace("&#39;", "'").replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")
        .replace("&nbsp;", " ").replace("&amp;", "&")
