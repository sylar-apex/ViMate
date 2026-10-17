package org.app.vimate.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.VideoRow
import org.app.vimate.appcore.component.openDetail
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_related
import org.app.vimate.appres.resources.ic_thumb_down
import org.app.vimate.appres.resources.ic_thumb_up
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

private val CardShape = RoundedCornerShape(12.dp)

/**
 * Video page on desktop and tablets: player, info and comments on the left, related videos in a fixed
 * side column, both scrolling on their own. The full comments list replaces the related column.
 */
@Composable
internal fun WideDetail(
    player: @Composable () -> Unit,
    v: Video,
    likes: String,
    actions: @Composable () -> Unit,
    comments: @Composable () -> Unit,
    commentsPanel: (@Composable () -> Unit)?,
    related: List<Video>,
    loading: Boolean,
) {
    val open = openDetail()
    Row(Modifier.fillMaxSize().background(ViColors.wideCanvas).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LazyColumn(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item(key = "player") { Box(Modifier.fillMaxWidth().clip(CardShape)) { player() } }
            item(key = "info") { WideInfo(v, likes, actions) }
            item(key = "comments") { Box(Modifier.fillMaxWidth().clip(CardShape).background(ViColors.white).padding(top = 12.dp)) { comments() } }
        }
        Box(Modifier.width(DetailLayoutSpec.SIDE_WIDTH).fillMaxHeight().clip(CardShape).background(ViColors.white)) {
            if (commentsPanel != null) commentsPanel()
            else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 12.dp)) {
                item(key = "related") {
                    Text(stringResource(Res.string.detail_related), Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
                }
                items(related, key = { "r" + it.id }) { r -> VideoRow(r, onClick = { open(r) }) }
                item(key = "loading") { if (loading) Loading() }
            }
        }
    }
}

/** Title, channel, likes and the action row in one card under the player. */
@Composable
private fun WideInfo(v: Video, likes: String, actions: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(CardShape).background(ViColors.white).padding(16.dp)) {
        Text(v.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Row(Modifier.padding(top = 12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Avatar(v.channelAvatar, 40)
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(v.channel, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                val meta = listOf(v.views, v.published).filter { it.isNotEmpty() }.joinToString(" · ")
                if (meta.isNotEmpty()) Text(meta, fontSize = 12.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(Modifier.height(32.dp).clip(CircleShape).background(ViColors.detailLikePill).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_thumb_up, Modifier.size(20.dp), ViColors.ink)
                if (likes.isNotEmpty()) Text(likes, Modifier.padding(start = 6.dp), fontSize = 13.sp, color = ViColors.ink, maxLines = 1)
                Box(Modifier.padding(horizontal = 10.dp).size(1.dp, 16.dp).background(ViColors.detailLikeDivider))
                Ic(Res.drawable.ic_thumb_down, Modifier.size(20.dp), ViColors.ink)
            }
        }
        Box(Modifier.padding(vertical = 12.dp).fillMaxWidth().height(1.dp).background(ViColors.detailRule))
        Row(Modifier.height(56.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { actions() }
    }
}
