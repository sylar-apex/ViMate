package org.app.vimate.music.ui

import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import org.app.vimate.appres.resources.sites_cancel
import org.app.vimate.appres.resources.batch_download
import org.app.vimate.appres.resources.batch_select_all
import org.app.vimate.appres.resources.batch_added
import org.app.vimate.batch.ui.component.BatchDownloadDialog
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.component.LocalToast
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.border
import org.app.vimate.appres.resources.music_play_all
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.adaptive.hover
import coil3.compose.AsyncImage
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.music.ui.component.text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.VideoRow
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_d_save
import org.app.vimate.appres.resources.ic_d_share
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.appres.resources.music_play
import org.app.vimate.appres.resources.music_playlist_detail
import org.app.vimate.appres.resources.music_save
import org.app.vimate.appres.resources.music_share
import org.app.vimate.appres.resources.music_videos_count
import org.app.vimate.music.ui.component.PageHeader
import org.jetbrains.compose.resources.stringResource

/** VidMate "PlayList Detail": title + owner, play/share/save row, red download-all button, rows with download and menu. */
@Composable
fun YtPlaylistScreen(listId: String, viewModel: YtPlaylistViewModel = koinViewModel(key = "ytpl:$listId")) {
    LaunchedEffect(listId) { viewModel.setEvent(YtPlaylistContract.Event.Load(listId)) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    YtPlaylistContent(listId, state, viewModel::setEvent)
}

@Composable
fun YtPlaylistContent(listId: String, state: YtPlaylistContract.State, onEvent: (YtPlaylistContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val share = rememberShareText()
    val p = state
    BoxWithConstraints(Modifier.fillMaxSize()) {
    // Desktop / tablets: YouTube-style cover card on the left, numbered list on the right.
    if (maxWidth >= 840.dp) return@BoxWithConstraints WidePlaylist(listId, state, onEvent)
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        PageHeader(stringResource(Res.string.music_playlist_detail)) { nav.back() }
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().background(ViColors.hotSites).padding(start = 13.dp, top = 16.dp, end = 13.dp)) {
                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ViColors.ink, maxLines = 2)
                    Text(p.owner, Modifier.padding(top = 6.dp), fontSize = 15.sp, color = ViColors.metaGrey)
                    Row(Modifier.padding(top = 18.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                        Ic(Res.drawable.ic_play, Modifier.size(28.dp).clickable { p.videos.firstOrNull()?.let { nav.go(Routes.Detail.of(it)) } }, ViColors.ink, stringResource(Res.string.music_play))
                        Ic(Res.drawable.ic_d_share, Modifier.size(28.dp).clickable { share("https://www.youtube.com/playlist?list=$listId") }, ViColors.ink, stringResource(Res.string.music_share))
                        Ic(Res.drawable.ic_d_save, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.music_save))
                        Spacer(Modifier.weight(1f))
                        Box(Modifier.size(40.dp).clip(CircleShape).background(VRed).clickable {
                            p.videos.takeIf { it.isNotEmpty() }?.let { nav.go(Routes.BatchSelect.of(it)) }
                        }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_d_download, Modifier.size(22.dp), ViColors.white) }
                    }
                }
                Text(stringResource(Res.string.music_videos_count, p.videos.size), Modifier.padding(13.dp), fontSize = 15.sp, color = ViColors.metaGrey)
            }
            if (state.loading) item { Loading() }
            state.error?.let { e -> item { ErrorBox(e.text()) { onEvent(YtPlaylistContract.Event.Retry) } } }
            items(p.videos, key = { it.id }) { v ->
                VideoRow(v, { nav.go(Routes.Detail.of(v)) }) {
                    Box(Modifier.size(32.dp).clickable { nav.download(v) }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_download, Modifier.size(22.dp), ViColors.metaGrey) }
                }
            }
        }
    }
    }
}

@Composable
private fun WidePlaylist(listId: String, p: YtPlaylistContract.State, onEvent: (YtPlaylistContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val share = rememberShareText()
    val cover = p.videos.firstOrNull()?.thumbnail
    val toast = LocalToast.current
    val addedText = stringResource(Res.string.batch_added, 0)
    // Select mode happens in place: numbers turn into checks; the dialog opens over this page.
    var selecting by remember(listId) { mutableStateOf(false) }
    var picked by remember(listId) { mutableStateOf(emptySet<String>()) }
    var dialog by remember { mutableStateOf(false) }
    if (dialog) BatchDownloadDialog(p.videos.filter { it.id in picked }, { dialog = false }) { n ->
        selecting = false; toast(addedText.replace("0", "$n")); nav.home(Tab.FILES)
    }
    Row(Modifier.fillMaxSize().background(ViColors.wideCanvas).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        // Cover card: the first video's art over a dark brand wash, as on YouTube.
        Box(Modifier.width(300.dp).fillMaxHeight().clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF7A2E22), Color(0xFF3A1A16), ViColors.wideCanvas)))) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Box(Modifier.fillMaxWidth().aspectRatio(16f / 9).clip(RoundedCornerShape(14.dp)).background(ViColors.black)) {
                    if (cover != null) AsyncImage(cover, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Text(p.title, Modifier.padding(top = 14.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp, color = Color.White, maxLines = 3)
                if (p.owner.isNotEmpty()) Text(p.owner, Modifier.padding(top = 8.dp), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = .9f))
                Text(stringResource(Res.string.music_videos_count, p.videos.size), Modifier.padding(top = 4.dp), fontSize = 13.sp, color = Color.White.copy(alpha = .7f))
                Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.height(36.dp).clip(CircleShape).background(Color.White).clickable { p.videos.firstOrNull()?.let { nav.go(Routes.Detail.of(it)) } }
                        .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ic(Res.drawable.ic_play, Modifier.size(20.dp), Color.Black)
                        Text(stringResource(Res.string.music_play_all), Modifier.padding(start = 6.dp), color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    RoundAction(Res.drawable.ic_d_download, VRed) { if (p.videos.isNotEmpty()) { selecting = true; picked = p.videos.map { it.id }.toSet() } }
                    RoundAction(Res.drawable.ic_d_share, Color.White.copy(alpha = .15f)) { share("https://www.youtube.com/playlist?list=$listId") }
                }
            }
        }
        Column(Modifier.weight(1f).fillMaxHeight()) {
            if (selecting) SelectBar(picked.size, p.videos.size, picked.size == p.videos.size,
                onAll = { picked = if (picked.size == p.videos.size) emptySet() else p.videos.map { it.id }.toSet() },
                onCancel = { selecting = false }, onDownload = { if (picked.isNotEmpty()) dialog = true })
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 20.dp)) {
                if (p.loading) item { Loading() }
                p.error?.let { e -> item { ErrorBox(e.text()) { onEvent(YtPlaylistContract.Event.Retry) } } }
                itemsIndexed(p.videos, key = { _, v -> v.id }) { i, v ->
                    val on = v.id in picked
                    NumberedRow(i + 1, v, if (selecting) on else null,
                        { if (selecting) picked = if (on) picked - v.id else picked + v.id else nav.go(Routes.Detail.of(v)) }) { nav.download(v) }
                }
            }
        }
    }
}

@Composable
private fun RoundAction(icon: org.jetbrains.compose.resources.DrawableResource, bg: Color, onClick: () -> Unit) {
    Box(Modifier.size(36.dp).clip(CircleShape).background(bg).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Ic(icon, Modifier.size(20.dp), Color.White)
    }
}

/** Compact bar over the list in select mode: Select all, count, Cancel, Download. */
@Composable
private fun SelectBar(n: Int, total: Int, all: Boolean, onAll: () -> Unit, onCancel: () -> Unit, onDownload: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 6.dp).clip(RoundedCornerShape(12.dp)).background(ViColors.tileBg).padding(start = 6.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onAll).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(30.dp), contentAlignment = Alignment.Center) { Check(all) }; Text(stringResource(Res.string.batch_select_all), Modifier.padding(start = 12.dp), fontSize = 13.sp, color = ViColors.ink)
        }
        Spacer(Modifier.weight(1f))
        Text("$n/$total", fontSize = 13.sp, color = ViColors.sectionLabel)
        Text(stringResource(Res.string.sites_cancel), Modifier.padding(start = 14.dp).clip(RoundedCornerShape(8.dp)).clickable(onClick = onCancel).padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 13.sp, color = ViColors.ink)
        Row(Modifier.padding(start = 6.dp).height(32.dp).clip(RoundedCornerShape(8.dp)).background(if (n > 0) VRed else ViColors.batchDoneOff).clickable(enabled = n > 0, onClick = onDownload)
            .padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Ic(Res.drawable.ic_d_download, Modifier.size(14.dp), Color.White)
            Text(stringResource(Res.string.batch_download), Modifier.padding(start = 6.dp), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun Check(on: Boolean) {
    Box(Modifier.size(18.dp).clip(CircleShape).background(if (on) VRed else Color.Transparent).border(1.5.dp, if (on) VRed else ViColors.batchRadioOff, CircleShape),
        contentAlignment = Alignment.Center) { if (on) Icon(Icons.Filled.Check, null, Modifier.size(12.dp), tint = Color.White) }
}

/** Compact playlist row: position (or a check in select mode), thumbnail, two-line title, channel · views, download. */
@Composable
private fun NumberedRow(n: Int, v: Video, checked: Boolean?, onOpen: () -> Unit, onDownload: () -> Unit) {
    val h = rememberHover()
    // Separate rounded rows; a ticked row gets a light red wash instead of a solid grey block.
    val bg = when { checked == true -> ViColors.detailDownloadPill; h.hovered -> ViColors.hover; else -> Color.Transparent }
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp).clip(RoundedCornerShape(10.dp)).background(bg).hover(h).clickable(onClick = onOpen)
        .padding(vertical = 5.dp, horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(30.dp), contentAlignment = Alignment.Center) {
            if (checked != null) Check(checked) else Text("$n", color = ViColors.sectionLabel, fontSize = 12.sp)
        }
        Thumb(v.thumbnail, Modifier.width(150.dp).aspectRatio(16f / 9), corner = 8, duration = v.duration)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(v.title, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, lineHeight = 18.sp, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(listOf(v.channel, v.views).filter { it.isNotEmpty() }.joinToString(" • "), Modifier.padding(top = 4.dp), fontSize = 11.5.sp, color = ViColors.sectionLabel, maxLines = 1)
        }
        if (checked == null) Box(Modifier.size(30.dp).clip(CircleShape).clickable(onClick = onDownload), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_download, Modifier.size(17.dp), ViColors.ink)
        }
    }
}
