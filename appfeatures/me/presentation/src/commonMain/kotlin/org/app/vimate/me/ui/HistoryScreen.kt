package org.app.vimate.me.ui

import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.*
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.component.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** History, as VidMate: "Local history" tab with a horizontal strip of recent videos, YouTube/Website history rows, sign-in card. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HistoryScreen(viewModel: LibraryViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val yt = state.watch
    val sites = state.sites
    var sub by remember { mutableStateOf<String?>(null) }
    var menu by remember { mutableStateOf(false) }
    if (sub != null) {
        BackHandler { sub = null }
        Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
            PageHeader(stringResource(if (sub == "yt") Res.string.me_youtube_history else Res.string.me_website_history)) { sub = null }
            LazyColumn {
                if (sub == "yt") {
                    if (yt.isEmpty()) item(key = "empty") { Empty(stringResource(Res.string.me_no_watch_history)) }
                    items(yt, key = { it.id }) { v ->
                        VideoRow(v, { nav.go(Routes.Detail.of(v)) }) {
                            IconButton(onClick = { viewModel.setEvent(LibraryContract.Event.RemoveWatched(v.id)) }) { Ic(Res.drawable.ic_close, Modifier.size(20.dp), ViColors.meIconGrey) }
                        }
                    }
                } else {
                    if (sites.isEmpty()) item(key = "empty") { Empty(stringResource(Res.string.me_no_websites)) }
                    items(sites, key = { it.url }) { x ->
                        ListItem(headlineContent = { Text(x.title.ifEmpty { x.url }, maxLines = 1) }, supportingContent = { Text(x.url, maxLines = 1, fontSize = 12.sp) },
                            modifier = Modifier.clickable { nav.go(Routes.Browser(x.url)) })
                    }
                }
            }
        }
        return
    }
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding().background(ViColors.surface)) {
        PageHeader(stringResource(Res.string.me_history)) { nav.back() }
        Box(Modifier.fillMaxWidth().height(46.dp)) {
            Text(stringResource(Res.string.me_local_history), Modifier.padding(start = 12.dp).align(Alignment.CenterStart), fontSize = 13.sp, lineHeight = 19.sp, color = ViColors.ink)
            Box(Modifier.align(Alignment.BottomStart).padding(start = 26.7.dp).size(33.dp, 2.dp).background(ViColors.meTabRed))
            Box(Modifier.align(Alignment.CenterEnd).padding(end = 16.dp).size(24.dp)) {
                Box(Modifier.fillMaxSize().clickable { menu = true }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_vert, Modifier.size(24.dp), ViColors.meMore, stringResource(Res.string.me_more)) }
                DropdownMenu(menu, { menu = false }) {
                    DropdownMenuItem(text = { Text(stringResource(Res.string.me_clear_local_history)) }, onClick = { menu = false; viewModel.setEvent(LibraryContract.Event.ClearLocalHistory) })
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.meDivider))
        if (yt.isEmpty()) Box(Modifier.fillMaxWidth().height(147.dp), contentAlignment = Alignment.Center) { Text(stringResource(Res.string.me_no_history_yet), color = ViColors.meGrey) }
        else LazyRow(Modifier.height(147.dp), contentPadding = PaddingValues(start = 13.dp, top = 10.dp)) {
            items(yt.take(org.app.vimate.me.domain.constant.MeUrls.HISTORY_STRIP), key = { it.id }) { v ->
                Column(Modifier.width(148.dp).clickable { nav.go(Routes.Detail.of(v)) }.padding(start = 3.dp, end = 13.dp)) {
                    Box(Modifier.size(132.dp, 74.dp).background(ViColors.meThumbBg)) {
                        coil3.compose.AsyncImage(v.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        val badge = v.duration.ifEmpty { stringResource(Res.string.me_shorts) }
                        Box(Modifier.align(Alignment.BottomEnd).padding(end = 6.dp, bottom = 6.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.meBadge).padding(horizontal = 4.dp)) {
                            Text(badge, color = ViColors.white, fontSize = 12.sp, lineHeight = 17.7.sp)
                        }
                    }
                    Text(v.title, Modifier.padding(top = 7.dp), fontSize = 14.sp, lineHeight = 16.3.sp, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(v.channel, fontSize = 12.5.sp, lineHeight = 16.3.sp, color = ViColors.meGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth().height(6.dp).background(ViColors.meBand))
        HistoryRow(Res.drawable.ic_h_youtube, stringResource(Res.string.me_youtube_history)) { sub = "yt" }
        HistoryRow(Res.drawable.ic_h_web, stringResource(Res.string.me_website_history)) { sub = "web" }
        Spacer(Modifier.weight(1f))
        Row(Modifier.padding(start = 15.dp, end = 15.dp, bottom = 22.dp).fillMaxWidth().height(59.dp).shadow(2.dp).background(ViColors.surface).padding(start = 10.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.me_login_more), Modifier.weight(1f).padding(end = 12.dp), fontWeight = FontWeight.Bold, fontSize = 14.5.sp, lineHeight = 17.sp, color = ViColors.ink, maxLines = 2)
            Box(Modifier.size(73.dp, 31.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.meTabRed).clickable { nav.go(Routes.GoogleLogin) }, contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.me_sign_in), color = ViColors.white, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
