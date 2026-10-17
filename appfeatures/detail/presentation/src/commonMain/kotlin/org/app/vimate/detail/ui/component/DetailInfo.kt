package org.app.vimate.detail.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_comments
import org.app.vimate.appres.resources.detail_comments_count
import org.app.vimate.appres.resources.ic_selector
import org.app.vimate.appres.resources.ic_thumb_down
import org.app.vimate.appres.resources.ic_thumb_up
import org.app.vimate.sharedlogic.yt.Comment
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** Title block: 44dp avatar, 2-line title, channel · published, like pill 102x28dp at x=242. */
@Composable
internal fun DetailTitle(v: Video, likes: String) {
    Column {
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().height(77.dp)) {
            Box(Modifier.padding(start = 12.dp, top = 8.dp)) { Avatar(v.channelAvatar, 44) }
            Text(v.title, Modifier.padding(start = 66.dp, top = 12.dp, end = 12.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 17.7.sp,
                color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(listOf(v.channel, v.published).filter { it.isNotEmpty() }.joinToString(" · "), Modifier.padding(start = 66.dp, top = 55.dp).width(166.dp),
                color = ViColors.metaGrey, fontSize = 13.5.sp, lineHeight = 16.3.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(Modifier.padding(start = 242.dp, top = 50.dp).size(102.dp, 28.dp).clip(CircleShape).background(ViColors.detailLikePill).padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_thumb_up, Modifier.size(24.dp), ViColors.ink)
                Text(likes, Modifier.padding(start = 2.dp).width(30.dp), fontSize = 12.5.sp, lineHeight = 14.sp, color = ViColors.ink, maxLines = 1)
                Box(Modifier.size(1.dp, 17.dp).background(ViColors.detailLikeDivider))
                Ic(Res.drawable.ic_thumb_down, Modifier.padding(start = 8.dp).size(24.dp), ViColors.ink)
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.detailRule))
    }
}

/** Five 64dp actions from x=16, 66dp stride; 28dp icons 16dp under the rule. */
@Composable
internal fun DetailActions(content: @Composable () -> Unit) {
    Column {
        Row(Modifier.padding(start = 16.dp, top = 16.dp).height(64.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) { content() }
        Box(Modifier.padding(top = 2.7.dp).fillMaxWidth().height(1.dp).background(ViColors.detailRule))
    }
}

/**
 * Measured from VidMate: 336x81.7dp #F5F5F5 card; "Comments" 16sp at (10, 8.7), grey "·count"; 16dp selector at x=310;
 * 32dp avatar at (10, 37.7) (letter on a colour if no photo), single-line 15.5sp comment at x=50.
 */
@Composable
internal fun CommentsCard(count: String, first: Comment?, enabled: Boolean, onOpen: () -> Unit) {
    Column {
        Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(81.7.dp).clip(RoundedCornerShape(12.dp)).background(ViColors.detailRule)
            .clickable(enabled = enabled, onClick = onOpen)) {
            Row(Modifier.offset(x = 10.dp, y = 8.7.dp).height(19.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.detail_comments), fontSize = 14.8.sp, lineHeight = 19.sp, color = ViColors.ink)
                if (count.isNotEmpty()) Text(stringResource(Res.string.detail_comments_count, count), fontSize = 14.8.sp, lineHeight = 19.sp, color = ViColors.metaGrey)
            }
            Ic(Res.drawable.ic_selector, Modifier.offset(x = 310.5.dp, y = 7.5.dp).size(19.dp), ViColors.ink)
            Box(Modifier.offset(x = 10.dp, y = 37.7.dp)) {
                val who = first?.author.orEmpty().removePrefix("@")
                if (first?.avatar.isNullOrEmpty()) Box(Modifier.size(32.dp).clip(CircleShape).background(ViColors.detailCommentLetter), contentAlignment = Alignment.Center) {
                    Text(who.take(1).ifEmpty { " " }, color = Color.White, fontSize = 15.sp)
                } else Avatar(first.avatar, 32)
            }
            Text(first?.text ?: "", Modifier.offset(x = 50.dp, y = 45.5.dp).padding(end = 60.dp), maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontSize = 13.6.sp, lineHeight = 18.sp, color = ViColors.ink)
        }
        Spacer(Modifier.height(16.dp))
    }
}
