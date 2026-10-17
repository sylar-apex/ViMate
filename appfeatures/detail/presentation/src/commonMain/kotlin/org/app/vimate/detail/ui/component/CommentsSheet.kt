package org.app.vimate.detail.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Avatar
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_comments
import org.app.vimate.appres.resources.detail_no_comments
import org.jetbrains.compose.resources.stringResource

/** POC CommentsSheet: comments in a bottom sheet (kept from the POC; the page uses [CommentsPanel]). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CommentsSheet(token: String, onDismiss: () -> Unit) {
    val pager = rememberCommentsPager(token)
    val comments by pager.comments.collectAsState()
    val loading by pager.loading.collectAsState()
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(stringResource(Res.string.detail_comments), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn(Modifier.fillMaxHeight(.8f)) {
            itemsIndexed(comments, key = { i, _ -> i }) { i, c ->
                if (i >= comments.size - 3) LaunchedEffect(comments.size) { pager.more() }
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Avatar(c.avatar, 32)
                    Column(Modifier.padding(start = 10.dp)) {
                        Text("${c.author} · ${c.published}", fontSize = 12.sp, color = Color.Gray)
                        Text(c.text, fontSize = 14.sp)
                        if (c.likes.isNotEmpty()) Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ThumbUp, null, Modifier.size(14.dp)); Text(" ${c.likes}", fontSize = 12.sp)
                        }
                    }
                }
            }
            item(key = "footer") { if (loading) Loading() else if (comments.isEmpty()) Empty(stringResource(Res.string.detail_no_comments)) }
        }
    }
}
