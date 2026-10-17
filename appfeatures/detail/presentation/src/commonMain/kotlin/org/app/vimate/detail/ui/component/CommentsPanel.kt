package org.app.vimate.detail.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_close
import org.app.vimate.appres.resources.detail_comments
import org.app.vimate.appres.resources.detail_comments_count
import org.app.vimate.appres.resources.detail_login_comment
import org.app.vimate.appres.resources.detail_no_comments
import org.app.vimate.appres.resources.detail_replies
import org.app.vimate.appres.resources.ic_c_close
import org.app.vimate.appres.resources.ic_c_filter
import org.app.vimate.appres.resources.ic_c_reply
import org.app.vimate.appres.resources.ic_c_user
import org.app.vimate.appres.resources.ic_thumb_down
import org.app.vimate.appres.resources.ic_thumb_up
import org.app.vimate.detail.repository.DetailRepository
import org.app.vimate.detail.viewmodel.CommentsPager
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Remembers a comments pager for [token] and loads its first page. */
@Composable
internal fun rememberCommentsPager(token: String): CommentsPager {
    val repo = koinInject<DetailRepository>()
    val scope = rememberCoroutineScope()
    val pager = remember(token) { CommentsPager(repo, scope, token) }
    LaunchedEffect(pager) { pager.more() }
    return pager
}

/** VidMate's comments panel (replaces the page under the player). Geometry measured in dp from the panel top. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun CommentsPanel(token: String, count: String, onClose: () -> Unit) {
    BackHandler(onBack = onClose)
    val pager = rememberCommentsPager(token)
    val comments by pager.comments.collectAsState()
    val loading by pager.loading.collectAsState()
    val grey = ViColors.metaGrey
    Column(Modifier.fillMaxSize().background(ViColors.surface)) {
        Box(Modifier.fillMaxWidth().height(46.dp)) {
            Row(Modifier.offset(x = 12.dp, y = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.detail_comments), fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 21.7.sp, color = ViColors.ink)
                if (count.isNotEmpty()) Text(stringResource(Res.string.detail_comments_count, count), fontSize = 15.sp, lineHeight = 21.7.sp, color = grey)
            }
            Box(Modifier.offset(x = 280.dp, y = 7.dp).size(32.dp), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_c_filter, Modifier.size(26.dp), ViColors.ink) }
            Box(Modifier.offset(x = 320.dp, y = 7.dp).size(32.dp).clickable(onClick = onClose), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_c_close, Modifier.size(26.dp), ViColors.ink, stringResource(Res.string.detail_close))
            }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(ViColors.detailCommentsRule))
        Row(Modifier.fillMaxWidth().height(58.dp).padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(ViColors.detailCommentUserBg), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_c_user, Modifier.size(22.dp), ViColors.detailCommentUserIcon)
            }
            Text(stringResource(Res.string.detail_login_comment), Modifier.padding(start = 12.dp), fontSize = 16.3.sp, color = grey)
        }
        Box(Modifier.fillMaxWidth().height(0.7.dp).background(ViColors.gap))
        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(comments, key = { i, _ -> i }) { i, c ->
                if (i >= comments.size - 3) LaunchedEffect(comments.size) { pager.more() }
                Box(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Box(Modifier.offset(x = 12.dp)) { Avatar(c.avatar, 36) }
                    Column(Modifier.padding(start = 60.dp, end = 12.dp)) {
                        Text("${c.author} · ${c.published}", fontSize = 12.6.sp, lineHeight = 14.7.sp, color = grey, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(c.text, Modifier.padding(top = 5.dp), fontSize = 14.6.sp, lineHeight = 17.7.sp, color = ViColors.ink)
                        val hasReplies = c.replies.isNotEmpty() && c.replies != "0"
                        Row(Modifier.padding(top = 16.dp).height(28.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(Modifier.width(59.dp), verticalAlignment = Alignment.CenterVertically) {
                                Ic(Res.drawable.ic_thumb_up, Modifier.size(17.dp), grey); Text(" ${c.likes}", fontSize = 13.5.sp, color = grey)
                            }
                            Box(Modifier.width(59.dp)) { Ic(Res.drawable.ic_thumb_down, Modifier.size(17.dp), grey) }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Ic(Res.drawable.ic_c_reply, Modifier.size(16.dp), grey); if (hasReplies) Text(" ${c.replies}", fontSize = 13.5.sp, color = grey)
                            }
                        }
                        if (hasReplies) Text(stringResource(Res.string.detail_replies, c.replies), Modifier.padding(top = 18.dp), fontSize = 14.2.sp, color = ViColors.link)
                        Spacer(Modifier.height(16.dp))
                    }
                }
                Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(0.7.dp).background(ViColors.gap))
            }
            item(key = "footer") { if (loading) Loading() else if (comments.isEmpty()) Empty(stringResource(Res.string.detail_no_comments)) }
        }
    }
}
